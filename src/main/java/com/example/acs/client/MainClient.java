package com.example.acs.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainClient {
    public static void main(String[] args) {
        try {
            ClientCredit c = new ClientCredit(2555, InetAddress.getLocalHost());
            c.init();
            c.sendMessage("coucou");
            c.readLine();
            c.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
