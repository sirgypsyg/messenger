package org.example;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Optional;

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
                if (message.startsWith("/file:"))
                    receiveFile(message.substring(6));
                else System.out.println(message);
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
    public void whisper(String message){
        writer.println("/w:" + message);
    }
    public void online(){
        writer.println("/online:");
    }
    public void sendFile(String message){
        String[] temp = message.split(" ");
        String recipientName = temp[0];
        String filePath = temp[1];

        // Open the File where he located in your pc
        File file = new File(filePath);
        try {
            long fileSize = file.length();
            writer.println("/file:"+ recipientName + " " + fileSize + " " + file.getName());
            // Open the File where he located in your pc
            FileInputStream fileInputStream = new FileInputStream(file);
            //create outputstream
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            byte[] buffer = new byte[64];
            int count = 0;
            // Here we  break file into chunks
            while ((count = fileInputStream.read(buffer)) > 0){
                // Send the file to Server Socket
                dataOutputStream.write(buffer, 0, count);
            }
            // close the file here
            fileInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void receiveFile(String message){
        String[] temp = message.split(" ");
        String senderName = temp[0];
        long fileSize = Long.parseLong(temp[1]);
        String fileName = temp[2];

        try {
            File file = new File(String.valueOf(Path.of(System.getProperty("java.io.tmpdir")).resolve(fileName)));
            DataInputStream fileIn = new DataInputStream(socket.getInputStream());
            FileOutputStream fileOut = new FileOutputStream(file);

            byte[] buffer = new byte[64];
            int count = 0;
            long receivedSize = 0;

            System.out.println("Receiving file from " + senderName + "...");

            while (receivedSize < fileSize){
                count = fileIn.read(buffer);
                receivedSize += count;
                System.out.println("\r" + (receivedSize*100/fileSize)+"%");
                fileOut.write(buffer,0,count);
            }

            System.out.println("file saved as: " + file.getAbsoluteFile());
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
