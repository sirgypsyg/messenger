package org.example;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private Socket socket;
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

            System.out.println("New client!");
            String message;
            while ((message = reader.readLine()) != null) {
                server.Broadcast(message, this);
            }
            System.out.println("client disconnected");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendMessage(String message){
        writer.println(message);
    }
}