package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainClient {
    public static void main(String[] args) {
        try {
            ConnectionThread connectionThread = new ConnectionThread("localhost",5000);

            connectionThread.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Whats ur login?");
            String clientName = reader.readLine();
            System.out.println("Welcome " + clientName);
            connectionThread.login(clientName);

            while (true){
                String message = reader.readLine();
                if (message.startsWith("/")){
                    String words[] = message.split(" ", 2);
                    switch (words[0]){
                        case "/w" -> connectionThread.whisper(words[1]);
                        case "/online" -> connectionThread.online();
                        case "/file" -> connectionThread.sendFile(words[1]);
                    }
                }else connectionThread.sendMessage(message);
            }
        }catch (IOException e){
            System.out.println("ex 1");
            throw new RuntimeException(e);
        }

    }
}