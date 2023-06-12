package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Server {
    private ServerSocket serverSocket;
    private List<ClientThread> clients = new ArrayList<>();

    public void removeClient(ClientThread clientThread){
        clients.remove(clientThread);
    }
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
            sender.sendMessage(client.getLogin());
        }
    }
    private Optional<ClientThread> getClient(String clientName) {
        return clients.stream()
                .filter(client -> clientName.equals(client.getLogin()))
                .findFirst();
    }
    public void whisper(String message, ClientThread sender){
        String[] temp = message.split(" ");

        for (var client : clients){
            if (client.getLogin().equals(temp[0])){
                client.sendMessage(temp[1]);
                return;
            }
        }
        sender.sendMessage("cant find user");
    }
    public void sendFile(String message, ClientThread sender) throws IOException {
        String[] temp = message.split(" ");
        String recipientName = temp[0];
        long fileSize = Long.parseLong(temp[1]);
        String fileName = temp[2];

        Optional<ClientThread> recipient = getClient(recipientName);

        if (recipient.isPresent()){
            DataInputStream dataInputStream = new DataInputStream(sender.getSocket().getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(recipient.get().getSocket().getOutputStream());

            byte[] buffer = new byte[64];
            long receivedSize = 0;
            int count;

            recipient.get().sendMessage("/file:"+ sender.getLogin() + " " + fileSize + " " + fileName);
            while (receivedSize < fileSize){
                count = dataInputStream.read(buffer);
                receivedSize+=count;
                System.out.println(receivedSize + " " + (fileSize-receivedSize));
                dataOutputStream.write(buffer, 0, count);
            }
        }else sender.sendMessage("/file:" + recipientName);
    }
}