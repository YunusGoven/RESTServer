package com.example.acs.server;

public class MainServer {
    public static void main(String[] args) {
        ServerAuth sa = new ServerAuth();
        ServerMoney sm = new ServerMoney();
        (new Thread(sa)).start();
        (new Thread(sm)).start();
    }
}
