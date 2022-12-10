package com.example.acq.server;

import com.example.acs.server.ServerCommunication;
import com.example.acs.server.ServerName;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Thread qui permet d'effectuer une connection entre l'api HTTPS et ACQ
 */
public class HttpClientRunnable implements Runnable{

    private Socket client;
    private ServerName servername;
    private boolean stop =false;
    private ServerCommunication serverCommunication;

    /**
     *
     * @param client  Socket du client
     * @param servername  Type de serveur
     */
    public HttpClientRunnable(Socket client, ServerName servername) {
        this.client = client;
        this.servername = servername;
        this.serverCommunication = new ServerCommunication(this.client, servername);
    }

    @Override
    public void run() {
        while (!stop) {
            // receive code by https api
            String received = serverCommunication.readLine();
            if (!"exit".equals(received)) {
                try {
                    //todo change by acs ip and port
                    ACSComunicator acsComunicator = new ACSComunicator(InetAddress.getLocalHost(), 5555);
                    acsComunicator.init();
                    // acq send code to acs
                    acsComunicator.sendMessage(received);
                    // acs send code to acq
                    String receive = acsComunicator.readLine();
                    acsComunicator.close();
                    // acq send ACK/NACK to https api
                    serverCommunication.sendMessage(receive);

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
            clientExit();
        }

    }

    /**
     * Close the connection with the https API
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
