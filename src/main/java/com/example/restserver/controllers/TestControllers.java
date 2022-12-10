package com.example.restserver.controllers;

import com.example.restserver.communicator.ACQCommunicator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@RequestMapping(value = "/test")
public class TestControllers {

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get() {
        return ResponseEntity.ok("salut");
    }

    @RequestMapping(value = "/e",method = RequestMethod.GET)
    public ResponseEntity get2()  {
        ACQCommunicator acqCommunicator = null;
        try {
            //TODO se baser sur ce pour faire le paiement
            acqCommunicator = new ACQCommunicator(InetAddress.getLocalHost(),1458);
            acqCommunicator.init();
            acqCommunicator.sendMessage("4148c69b-32eb-46dd-b7f3-643774dd8235");
            String a = acqCommunicator.readLine();
            acqCommunicator.close();
            return ResponseEntity.ok(a);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
