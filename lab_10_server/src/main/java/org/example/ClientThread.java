package org.example;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientThread extends Thread {
    private Socket socket;

    public String getLogin() {
        return login;
    }

    private String login;
    private PrintWriter writer;
    private Server server;
    public ClientThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            InputStream input  = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            writer = new PrintWriter(output, true);

            String message;
            while ((message = reader.readLine()) != null) {
                String[] words = message.split(":", 2);
                switch (words[0]){
                    case "login"-> {
                        login = words[1];
                        server.Broadcast(words[1] + " has joined the chat", this);
                    }
                    case "/online" -> server.online(this);
                    case "/w" -> server.whisper(words[1], this);
                    default -> server.Broadcast(login + ": " + message, this);
                }
            }
            server.Broadcast("user" + " has left the chat", this);
        } catch (IOException e) {
            server.removeClient(this);
            throw new RuntimeException(e);
        }
    }
    public void sendMessage(String message){
        writer.println(message);
    }
}