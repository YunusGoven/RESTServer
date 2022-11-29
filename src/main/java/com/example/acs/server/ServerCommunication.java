package com.example.acs.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class ServerCommunication {
    private Socket client;
    private String servername;
    private BufferedReader in;
    private PrintWriter out;

    public ServerCommunication(Socket client, String servername) {
        this.client = client;
        this.servername = servername;
        try {
            this.in = new BufferedReader(new InputStreamReader(this.client.getInputStream(), Charset.forName("UTF-8")));
            this.out = new PrintWriter(this.client.getOutputStream(),true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readLine() {
        try {
            String line;
            if ((line = in.readLine())!= null) {
                //todo dechiffrer message
                System.out.println("["+servername+"]"+" Receive from "+"[Client "+ client.getInetAddress().getHostAddress()+"] : "+ line );
                return line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  "exit";
    }



    public void sendMessage(String message) {
        //todo chiffrer messager
        out.println(message);
        out.flush();
        System.out.println("["+servername+"]"+" Send to "+"[Client "+ client.getInetAddress().getHostAddress()+"] : "+ message );
    }
}
