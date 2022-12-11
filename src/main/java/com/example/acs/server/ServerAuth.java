package com.example.acs.server;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.security.KeyStore;
import java.util.Map;
import java.util.Set;

public class ServerAuth implements Runnable {
    private SSLServerSocket sslServerSocket;
    private final int PORT_AUTH = 2555;
    private final Set<SavedCode> savedCode;

    public ServerAuth(Set<SavedCode> savedCode){
        this.savedCode = savedCode;
    }

    @Override
    public void run() {
        System.out.println(1);
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
            sslServerSocket = (SSLServerSocket)sslServerSocketFactory.createServerSocket(PORT_AUTH);
            System.out.println("[ACS SERVER AUTH ON] Serveur ip: "+ InetAddress.getLocalHost().getHostAddress() +" sur le port : "+PORT_AUTH);

            loop();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loop method for listen new connection to ACS Auth Server (from client), start a new thread foreach client
     */
    private void loop() {
        while (!sslServerSocket.isClosed()) {
            try {
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                System.out.println("[ACS SERVER AUTH] [Client "+ sslSocket.getInetAddress().getHostAddress()+ "] s'est connecté !");
                ServerClientRunnable sc = new ServerClientRunnable(sslSocket, ServerName.ACS_SERVER_AUTH, savedCode);
                (new Thread(sc)).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
