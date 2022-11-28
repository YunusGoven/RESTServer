package com.example.restserver.users;

import com.example.restserver.csv.Csv;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class UserRestController {
    private final Csv c = new Csv();
    private final String SALT = "DALGOV46%--";

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity login(@Validated @RequestBody User user) {
        List<User> userList = c.reader();
        if (userList.contains(user)) {
            User user1 = userList.get(userList.indexOf(user));
            String pwd = hashAndSalt(user.getPassword());
            if (pwd.equals(user1.getPassword())) {
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity register(@Validated @RequestBody User user) {
        List<User> userList = c.reader();
        if (userList.contains(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String pwd = hashAndSalt(user.getPassword());
        c.addUser(new User(user.getLogin(), pwd));

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private String hashAndSalt(String password) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(SALT.getBytes(StandardCharsets.UTF_8));
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
                // ou
                //sb.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return generatedPassword;
    }
}
