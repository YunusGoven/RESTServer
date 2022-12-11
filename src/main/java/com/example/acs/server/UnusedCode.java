package com.example.acs.server;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

public class UnusedCode implements Runnable {
    private Set<SavedCode> savedCodes ;

    public UnusedCode (Set<SavedCode> savedCodes) {
        this.savedCodes = savedCodes;
    }

    @Override
    public void run() {
        while (true) {
            savedCodes.forEach(savedCode -> {
                Date now = new Date();
                Instant t = savedCode.getCreatedTime().toInstant();
                Date creation = Date.from(t);
                //TODO set time for delete
                creation.setMinutes(creation.getMinutes()+2);
                if (creation.before(now)) {
                    System.out.println("[ACS_SERVER_AUTH] [CODE DELETE] " + savedCode.getCode());
                    savedCodes.remove(savedCode);
                }
            });
            try {
                //TODO set time for thread
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
