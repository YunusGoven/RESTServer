package com.example.acq.server;

import com.example.acs.server.ServerClientRunnable;
import com.example.acs.server.ServerName;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.security.KeyStore;

/**
 * Serveur qui permet d'implementer un serveur ACQ
 */
public class ServerA implements Runnable{
    private SSLServerSocket sslServerSocket;
    private final int PORT = 1458;

    @Override
    public void run() {
        System.out.println(3);
        try {
            //TODO certificat
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
            sslServerSocket = (SSLServerSocket)sslServerSocketFactory.createServerSocket(PORT);
            System.out.println("[SERVER ACQ ON] Serveur ip: "+ InetAddress.getLocalHost().getHostAddress() +" sur le port : "+PORT);

            loop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method loop, listen when a new client will connect to ACQ server, start a new thread foreach client
     */
    private void loop() {
        while (!sslServerSocket.isClosed()) {
            try {
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                System.out.println("[SERVER ACQ] [Client "+ sslSocket.getInetAddress().getHostAddress()+ "] s'est connecté !");
                HttpClientRunnable sc = new HttpClientRunnable(sslSocket, ServerName.SERVER_ACQ);
                (new Thread(sc)).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
