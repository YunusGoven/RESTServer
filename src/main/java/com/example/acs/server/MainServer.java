package com.example.acs.server;

import java.util.HashMap;
import java.util.Map;

public class MainServer {
    public static void main(String[] args) {
        Map<UserCardInfo, String> savedCode = new HashMap<>();
        ServerAuth sa = new ServerAuth(savedCode);
        ServerMoney sm = new ServerMoney(savedCode);
        (new Thread(sa)).start();
        (new Thread(sm)).start();
    }
}
