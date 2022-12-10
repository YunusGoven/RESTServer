package com.example.acs.client;

import com.example.EncryptionAes;
import com.example.acs.server.UserCardInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * Client App qui permet de recuperer le code
 * Peut etre faire autrement :)
 */
public class MainClient {
    public static void main(String[] args) {
        try {
            ClientCredit c = new ClientCredit(2555, InetAddress.getLocalHost());
            c.init();
            UserCardInfo u8 = new UserCardInfo(1888887890, new Date(1670691977298L), 191);
            // send card info to acs
            c.sendMessage(u8);
            // read code from acs
            c.readLine();
            c.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
