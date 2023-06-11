package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private List<ClientThread> clients = new ArrayList<>();

    public Server(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listen() throws IOException {
        while(true) {
            Socket socket = serverSocket.accept();
            ClientThread thread = new ClientThread(socket);
            clients.add(thread);
            thread.start();
        }
    }
}