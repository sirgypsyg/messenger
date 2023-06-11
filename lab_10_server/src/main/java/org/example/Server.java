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

    public void listen()  {
        while(true) {
            try {
                Socket socket = serverSocket.accept();
                ClientThread thread = new ClientThread(socket, this);
                clients.add(thread);
                thread.start();
            }catch (IOException e){
                throw new RuntimeException(e);
            }

        }
    }
    public void Broadcast(String message, ClientThread sender){
        for (var client : clients){
            if(client == sender) {
                continue;
            }
            client.sendMessage(message);
        }
    }

    public void online(ClientThread sender){
        for (var client : clients){
            sender.sendMessage(client.getName());
        }
    }

}