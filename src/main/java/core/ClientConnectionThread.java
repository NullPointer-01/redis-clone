package core;

import util.RespSerializer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static util.RespConstants.CR;
import static util.RespConstants.LF;

public class ClientConnectionThread extends Thread {
    private static final Logger LOGGER = Logger.getLogger(ClientConnectionThread.class.getName());

    private final Socket client;

    public ClientConnectionThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try (OutputStream outputStream = client.getOutputStream();
             BufferedInputStream bis = new BufferedInputStream(client.getInputStream())) {

            String PONG = "+PONG\r\n";
            StringBuilder sb = new StringBuilder();
            int data;

            while ((data = bis.read()) != -1) {
                char c = (char) data;
                String command = sb.toString();

                if (c == CR && (char) bis.read() == LF) {
                    switch (command) {
                        case "PING":
                            outputStream.write(PONG.getBytes());
                            break;
                        case "ECHO":
                            bis.read(); // Skip $
                            c = (char) bis.read();

                            int len = c - '0';
                            while ((c = (char) bis.read()) != CR) {
                                len *= 10;
                                len += (c - '0');
                            }

                            System.out.println("len -> " + len);
                            bis.read(); // Skip LF

                            String arg = new String(bis.readNBytes(len));
                            String response = RespSerializer.asBulkString(arg);

                            outputStream.write(response.getBytes());
                            break;
                    }
                    sb.setLength(0);
                } else {
                    sb.append(c);
                }

                outputStream.flush();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception processing client connection. " + e);
        } finally {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Exception closing client connection. " + e);
                }
            }
        }
    }
}