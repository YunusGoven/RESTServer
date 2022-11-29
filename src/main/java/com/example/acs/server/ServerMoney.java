package com.example.acs.server;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.security.KeyStore;

public class ServerMoney implements Runnable{
    private SSLServerSocket sslServerSocket;
    private final int PORT_MONEY = 5555;

    public ServerMoney(){}

    @Override
    public void run() {
        System.out.println(2);
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new FileInputStream(getClass().getResource("/yunand.p12").getPath()), "dalgov".toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SUNX509");
            kmf.init(ks, "dalgov".toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(ks);

            SSLContext sc = SSLContext.getInstance("SSL");
            TrustManager[] trustManagers = tmf.getTrustManagers();
            sc.init(kmf.getKeyManagers(),trustManagers, null);

            SSLServerSocketFactory sslServerSocketFactory = sc.getServerSocketFactory();
            sslServerSocket = (SSLServerSocket)sslServerSocketFactory.createServerSocket(PORT_MONEY);
            System.out.println("[ACS SERVER MONEY ON] Serveur ip: "+ InetAddress.getLocalHost().getHostAddress() +" sur le port : "+PORT_MONEY);

            loop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loop() {
        while (!sslServerSocket.isClosed()) {
            try {
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                System.out.println("[ ACS SERVER MONEY ON] [Client "+ sslSocket.getInetAddress().getHostAddress()+ "] s'est connecté !");
                ServerClientRunnable sc = new ServerClientRunnable(sslSocket, " ACS SERVER MONEY");
                (new Thread(sc)).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
