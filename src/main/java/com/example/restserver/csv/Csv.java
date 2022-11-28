package com.example.restserver.csv;

import com.example.restserver.users.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Csv {
    public List<User> reader() {
        List<User> userList = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/userlist.csv"));) {
            reader.readLine(); // first line
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String login = data[0];
                String password = data[1];
                User u = User.builder().login(login).password(password).build();
                userList.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }

    public boolean addUser(User user) {
        try (FileWriter fileWriter = new FileWriter("src/main/resources/userlist.csv",true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        PrintWriter printWriter = new PrintWriter(bufferedWriter)){
            String userString = String.format("%s,%s", user.getLogin(),user.getPassword());
            printWriter.println(userString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
