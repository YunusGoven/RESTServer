package com.example.restserver.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test")
public class TestControllers {

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity get() {
        return ResponseEntity.ok("salut");
    }
}
