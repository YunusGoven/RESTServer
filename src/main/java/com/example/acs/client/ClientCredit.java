package com.example.acs.client;

import com.example.EncryptionAes;
import com.example.acs.server.UserCardInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

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
        try {
            String message = null;
            message = objectMapper.writeValueAsString(userCardInfo);
            String encryptMessage = encryptionAes.encrypt(message);
            String signature = getRSASHA256Signature(message);
            out.println(encryptMessage);
            out.flush();
            System.out.println("Message (" +message+ ") is successfully send to ["+ sslSocket.getInetAddress().getHostAddress()+":"+port+ "]");
            out.println(signature);
            out.flush();
            System.out.println("Signature (" +signature+ ") is successfully send to ["+ sslSocket.getInetAddress().getHostAddress()+":"+port+ "]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get signature for message with Client private Key
     * @param message message
     * @return  Signature in String or null
     */
    private String getRSASHA256Signature(String message) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException, IOException {
        byte[] messageBytes = message.getBytes();
        File privateKeyFile = new File("src/main/resources/RSA_KEY/private_key_client.priv");
        byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
        PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpecPrivate);
        Signature s = Signature.getInstance("SHA256withRSA");
        s.initSign(privateKey);
        s.update(messageBytes);
        byte[] signatureByte = s.sign();
        return encode(signatureByte);
    }

    /**
     * Verify signature is valid with the public key of ACS
     * @param message   message in clair
     * @param signatureString  signature from acs
     * @return  true if valid else false
     */
    private boolean checkRSASHASignature(String message, String signatureString) {
        try {
            byte[] signature;
            signature = decode(signatureString);
            byte[] messageBytes = message.getBytes();
            File publicKeyFile = new File("src/main/resources/RSA_KEY/public_key_acs.pub");
            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
            X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpecPublic);
            Signature s = Signature.getInstance("SHA256withRSA");
            s.initVerify(publicKey);
            s.update(messageBytes);
            boolean ok = s.verify(signature);
            System.out.println("Signature verification result: " + ok);
            return ok;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Signature verification result: " + false);
            return false;
        }
    }

    private  String encode(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }
    private  byte[] decode(String data){
        return Base64.getDecoder().decode(data);
    }

    /**
     * Read code from ACS
     * @return code form ACS
     */
    public String readLine() {
        try {
            String receipt = in.readLine();
            String RSASHASignature = in.readLine();
            receipt = encryptionAes.decrypt(receipt);
            System.out.println("Receive (" + receipt + ") from "+"[SERVER "+ sslSocket.getInetAddress().getHostAddress() + ":" + port+ "]" );
            // Get signature
            RSASHASignature = encryptionAes.decrypt(RSASHASignature);
            boolean RSASHASignatureCheck = checkRSASHASignature(receipt, RSASHASignature);
            return RSASHASignatureCheck ? receipt : "Signature is not valid";
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
