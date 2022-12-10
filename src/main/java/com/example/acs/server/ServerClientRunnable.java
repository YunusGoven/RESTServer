package com.example.acs.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;

/**
 * Thread for each new client will connect to ACS and do exchange
 */
public class ServerClientRunnable implements Runnable{

    private Socket client;
    private ServerName servername;
    private boolean stop =false;
    private ServerCommunication serverCommunication;
    private CardList cardList = new CardList();
    private Map<UserCardInfo, String> savedCode;

    public ServerClientRunnable(Socket client, ServerName servername, Map<UserCardInfo, String> savedCode) {
        this.client = client;
        this.servername = servername;
        this.serverCommunication = new ServerCommunication(this.client, servername);
        this.savedCode = savedCode;
    }
    @Override
    public void run() {
        while (!stop) {
            // read message from ACQ or Client App
            String received = serverCommunication.readLine();
            if (!"exit".equals(received)) {
                // verify server type
                switch (this.servername) {
                    case ACS_SERVER_MONEY:
                        // verify code is valid
                        boolean valid = verifyCode(received);
                        // send ACK ou NACK to ACQ according to the code
                        serverCommunication.sendMessage(valid ? "ACK" : "NACK");
                        if (valid)
                            savedCode.entrySet().removeIf(entry -> entry.getValue().equals(received));
                        break;
                    case ACS_SERVER_AUTH:
                        // verify card info and generate card if exist
                        //todo verify signature
                        String code = generateCode(received);
                        // send code to Client app
                        //todo signer message
                        serverCommunication.sendMessage(code);
                        break;
                    default:
                        clientExit();
                }
            }
            // close connection between client
            clientExit();
        }
    }

    /**
     * Verify if the Server contains the code
     * @param message code from ACQ
     * @return true if contains else false
     */
    private boolean verifyCode(String message) {
        return savedCode.containsValue(message);
    }

    /**
     * Generate code for the client if card info is valid
     * @param message card info
     * @return code ou NOT VALID
     */
    private String generateCode(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserCardInfo userCardInfo = objectMapper.readValue(message, UserCardInfo.class);
            boolean valid = cardList.contains(userCardInfo);
            if (valid) {
                String code = genCode();
                savedCode.put(userCardInfo, code);
                return code;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "NOT VALID";
    }

    /**
     * Generate UUID for code
     * @return
     */
    private String genCode() {
        String generatedString = UUID.randomUUID().toString();
        return generatedString;
    }

    /**
     * Close connection between ACS and ACQ ou Client App
     */
    private void clientExit(){
        try {
            System.out.println("["+this.servername+"]"+"[Client "+ client.getInetAddress().getHostAddress()+"] : "+ "disconnected" );
            client.close();
            stop = true;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                client.close();
            } catch (IOException ex) {
            }
        }
    }
}
