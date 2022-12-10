package com.example.acs.client;

import com.example.EncryptionAes;
import com.example.acs.server.UserCardInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.security.KeyStore;

public class ClientCredit {
    private SSLSocket sslSocket = null;
    private int port;
    private InetAddress ip;
    private BufferedReader in;
    private PrintWriter out;
    private EncryptionAes encryptionAes ;

    /**
     *
     * @param port Port que ecoute l'ACS
     * @param ip  Ip sur serveur ACS
     */
    public ClientCredit(int port, InetAddress ip) {
        this.ip = ip;
        this.port = port;
        this.encryptionAes = new EncryptionAes();
    }

    /**
     * Init SSL tunnel between client and ACS
     */
    public void init () {
        try {
            //TODO certificat
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            keystore.load(new FileInputStream(getClass().getResource("/yunand.p12").getPath()), "dalgov".toCharArray());
            KeyManagerFactory kf = KeyManagerFactory.getInstance("SUNX509");
            kf.init(keystore, "dalgov".toCharArray());
            TrustManagerFactory t = TrustManagerFactory.getInstance("X509");
            t.init(keystore);
            SSLContext sc = SSLContext.getInstance("SSL");
            TrustManager[] tm = t.getTrustManagers();
            sc.init(kf.getKeyManagers(), tm, null);
            SSLSocketFactory ssf = sc.getSocketFactory();
            this.sslSocket = (SSLSocket)ssf.createSocket(ip, port);
            this.sslSocket.startHandshake();
            System.out.println("Connection successful to ["+ sslSocket.getInetAddress().getHostAddress()+":"+port+ "]");
            in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream(), Charset.forName("UTF-8")));
            out = new PrintWriter(sslSocket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Connection not successful to ["+ ip.getHostAddress()+":"+port+ "]");
        }
    }

    /**
     * Send card info in JSON to ACS
     * @param userCardInfo user card info
     */
    public void sendMessage(UserCardInfo userCardInfo) {
        ObjectMapper objectMapper = new ObjectMapper();
        String message = null;
        try {
            message = objectMapper.writeValueAsString(userCardInfo);
            //todo signer message
            String encryptMessage = encryptionAes.encrypt(message);
            out.println(encryptMessage);
            out.flush();
            System.out.println("Message (" +message+ ") is successfully send to ["+ sslSocket.getInetAddress().getHostAddress()+":"+port+ "]");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read code from ACS
     * @return code form ACS
     */
    public String readLine() {
        try {
            String receipt = in.readLine();
            //todo verifier signature message
            receipt = encryptionAes.decrypt(receipt);
            System.out.println("Receive (" + receipt + ") from "+"[SERVER "+ sslSocket.getInetAddress().getHostAddress() + ":" + port+ "]" );
            return receipt;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return "exit";
    }

    /**
     * Close connection with Client and ACS
     */
    public void close() {
        try {
           sslSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                sslSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("Connection closed with ["+ ip.getHostAddress()+":"+port+ "]");

    }
}
