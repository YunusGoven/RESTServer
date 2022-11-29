package com.example.acs.server;

import java.io.IOException;
import java.net.Socket;

public class ServerClientRunnable implements Runnable{

    private Socket client;
    private String servername;
    private boolean stop =false;
    private ServerCommunication serverCommunication;

    public ServerClientRunnable(Socket client, String servername) {
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
                // 1. traitement received
                // 2. transaction
                // 3. envoyer message  ==>  serverCommunication.sendMessage(message);
                //...
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
