package com.example.acs.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainServer {
    public static void main(String[] args) {
        Set<SavedCode> savedCodes = new HashSet<>();
        ServerAuth sa = new ServerAuth(savedCodes);
        ServerMoney sm = new ServerMoney(savedCodes);
        UnusedCode unusedCode = new UnusedCode(savedCodes);
        (new Thread(sa)).start();
        (new Thread(sm)).start();
        (new Thread(unusedCode)).start();
    }
}
