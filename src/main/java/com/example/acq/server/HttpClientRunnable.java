package com.example.acq.server;

import com.example.acs.server.ServerCommunication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class HttpClientRunnable implements Runnable{

    private Socket client;
    private String servername;
    private boolean stop =false;
    private ServerCommunication serverCommunication;

    public HttpClientRunnable(Socket client, String servername) {
        this.client = client;
        this.servername = servername;
        this.serverCommunication = new ServerCommunication(this.client, servername);
    }

    @Override
    public void run() {
        while (!stop) {
            String received = serverCommunication.readLine();
            if ("exit".equals(received)) {
                clientExit();
            } else {
                try {
                    ACSComunicator acsComunicator = new ACSComunicator(InetAddress.getLocalHost(),5555 );
                    acsComunicator.init();
                    // 1. https envoi un messsage (code)
                    // 2. traitement
                    // 3. envoi code a acs
//                    acsComunicator.sendMessage(received);
                    // 4. recup msg from acs
//                    String msg = acsComunicator.readLine();
                    // 5. disconnect from acs
//                    acsComunicator.close();
                    // 6. envoi msg a https
//                    serverCommunication.sendMessage("ACK/NACK");
                    // 7. close
//                    clientExit();
                    //...
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    clientExit();
                }

            }
        }

    }

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
