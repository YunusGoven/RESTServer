package com.example.acq.server;

import com.example.acs.server.ServerMoney;

public class MainServer {
    public static void main(String[] args) {
        ServerA sa = new ServerA();
        (new Thread(sa)).start();
    }
}
