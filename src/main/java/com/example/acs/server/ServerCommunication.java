package com.example.acs.server;

import com.example.EncryptionAes;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Classe qui permet d'effectuer les communications avec les autres serveurs
 */
public class ServerCommunication {
    private Socket client;
    private ServerName servername;
    private BufferedReader in;
    private PrintWriter out;
    private final EncryptionAes encryptionAes;

    public ServerCommunication(Socket client, ServerName servername) {
        this.encryptionAes = new EncryptionAes();
        this.client = client;
        this.servername = servername;
        try {
            this.in = new BufferedReader(new InputStreamReader(this.client.getInputStream(), Charset.forName("UTF-8")));
            this.out = new PrintWriter(this.client.getOutputStream(),true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read crypted message from ACS and return a decrypted message
     * @return decrypted message
     */
    public String readLine() {
        try {
            String line;
            if ((line = in.readLine())!= null) {
                line = encryptionAes.decrypt(line);
                System.out.println("["+servername+"]"+" Receive from "+"[Client "+ client.getInetAddress().getHostAddress()+"] : "+ line );
                return line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  "exit";
    }

    /**
     * Read message from client and return it
     * @return message in clair
     */
    public String readLineNormal() {
        try {
            String line;
            if ((line = in.readLine())!= null) {
                System.out.println("["+servername+"]"+" Receive from "+"[Client "+ client.getInetAddress().getHostAddress()+"] : "+ line );
                return line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  "exit";
    }

    /**
     * Crypt message in AES and send to ACS
     * @param message it's the code
     */
    public void sendMessage(String message) {
        String encryptMessage = encryptionAes.encrypt(message);
        out.println(encryptMessage);
        out.flush();
        System.out.println("["+servername+"]"+" Send to "+"[Client "+ client.getInetAddress().getHostAddress()+"] : "+ message );
    }
}
