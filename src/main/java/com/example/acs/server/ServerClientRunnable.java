package com.example.acs.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;

/**
 * Thread for each new client will connect to ACS and do exchange
 */
public class ServerClientRunnable implements Runnable{

    private Socket client;
    private ServerName servername;
    private boolean stop =false;
    private ServerCommunication serverCommunication;
    private CardList cardList = new CardList();
    private Set<SavedCode> savedCode;

    public ServerClientRunnable(Socket client, ServerName servername, Set<SavedCode> savedCode) {
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
                        if (valid)
                            savedCode.removeIf(e -> e.getCode().equals(received));
                        // send ACK ou NACK to ACQ according to the code
                        serverCommunication.sendMessage(valid ? "ACK" : "NACK");
                        break;
                    case ACS_SERVER_AUTH:
                        //read signature and verify if it's valid
                        String RSASHASignature = serverCommunication.readLineNormal();
                        boolean RSASHASignatureCheck = checkRSASHASignature(received, RSASHASignature);

                        // if signature ok verify card info and generate card if exist else send error
                        String code = RSASHASignatureCheck ? generateCode(received) : "NOT VALID";

                        //Signer le code et l'envoyer
                        String signature = getRSASHA256Signature(code);
                        // send code to Client app
                        serverCommunication.sendMessage(code);
                        if (signature != null)
                            serverCommunication.sendMessage(signature);
                        else
                            serverCommunication.sendMessage("Error during doing signature");
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
     * Verify signature is valid with the public key of client
     * @param message   message in clair
     * @param signatureString  signature from client
     * @return  true if valid else false
     */
    private boolean checkRSASHASignature(String message, String signatureString) {
        try {
            byte[] signature;
            signature = decode(signatureString);
            byte[] messageBytes = message.getBytes();
            File publicKeyFile = new File("src/main/resources/RSA_KEY/public_key_client.pub");
            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
            X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpecPublic);
            Signature s = Signature.getInstance("SHA256withRSA");
            s.initVerify(publicKey);
            s.update(messageBytes);
            boolean ok = s.verify(signature);
            System.out.println("["+this.servername+"]"+" Signature "+"[Client "+ client.getInetAddress().getHostAddress()+"] : "+ "Signature verification result: " + ok);
            return ok;
        } catch (Exception e) {
            System.err.println("["+this.servername+"]"+"[Client "+ client.getInetAddress().getHostAddress()+"] : "+ "Signature verification result: " + false);
            return false;
        }
    }

    /**
     * Get signature for message with ACS private key
     * @param message message
     * @return  Signature in String or null
     */
    private String getRSASHA256Signature(String message) {
        try {
            byte[] messageBytes = message.getBytes();
            File privateKeyFile = new File("src/main/resources/RSA_KEY/private_key_acs.priv");
            byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
            PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpecPrivate);
            Signature s = Signature.getInstance("SHA256withRSA");
            s.initSign(privateKey);
            s.update(messageBytes);
            byte[] signatureByte = s.sign();
            return encode(signatureByte);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private  String encode(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }
    private  byte[] decode(String data){
        return Base64.getDecoder().decode(data);
    }

    /**
     * Verify if the Server contains the code
     * @param message code from ACQ
     * @return true if contains else false
     */
    private boolean verifyCode(String message) {
        Date now = new Date();
        return savedCode.stream().anyMatch(e -> {
            Instant t = e.getCreatedTime().toInstant();
            Date creation = Date.from(t);
            //TODO st time to check
            creation.setMinutes(creation.getMinutes()+2);
            return e.getCode().equals(message) && creation.after(now);
        });
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
                if (!savedCode.add(new SavedCode(userCardInfo.getCard_number(), code, new Date()))) {
                    savedCode.removeIf(e -> e.getCardNumber() == userCardInfo.getCard_number());
                    savedCode.add(new SavedCode(userCardInfo.getCard_number(), code, new Date()));
                }
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
