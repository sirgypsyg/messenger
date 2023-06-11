package org.example;

import java.io.*;
import java.net.Socket;

public class ConnectionThread extends Thread {
    private Socket socket;
    private PrintWriter writer;

    public ConnectionThread(String host, int port) throws IOException {
        socket = new Socket(host, port);
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            writer = new PrintWriter(outputStream, true);
            String message;
            while ((message = reader.readLine()) != null){
                System.out.println(message);
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    public void login(String clientName){
        writer.println("login:" + clientName);
    }
    public void sendMessage(String message){
        writer.println(message);
    }
}
