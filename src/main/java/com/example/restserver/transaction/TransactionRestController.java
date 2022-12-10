package com.example.restserver.transaction;

import com.example.restserver.communicator.ACQCommunicator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@RequestMapping(value = "/transaction")
public class TransactionRestController {

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity buy(@RequestBody String code)  {
        ACQCommunicator acqCommunicator = null;
        try {
            //Modifier l'ip s'il le faut
            acqCommunicator = new ACQCommunicator(InetAddress.getLocalHost(),1458);
            acqCommunicator.init();
            acqCommunicator.sendMessage(code);
            String a = acqCommunicator.readLine();
            acqCommunicator.close();
            return ResponseEntity.ok(a);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
