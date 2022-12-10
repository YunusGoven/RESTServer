package com.example.acq.server;

public class MainServer {
    public static void main(String[] args) {
        ServerA sa = new ServerA();
        (new Thread(sa)).start();
    }
}
