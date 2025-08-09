package core;

import util.RespSerializer;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientConnectionThread extends Thread {
    private static final Logger LOGGER = Logger.getLogger(ClientConnectionThread.class.getName());

    private final Socket client;

    public ClientConnectionThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try (OutputStream outputStream = client.getOutputStream(); BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()))) {

            String PONG = "+PONG\r\n";
            String line;

            while ((line = reader.readLine()) != null) {
                switch (line) {
                    case "PING":
                        outputStream.write(PONG.getBytes());
                        break;
                    case "ECHO":
                        reader.readLine(); // Skip CRLF
                        String arg = reader.readLine();
                        String response = RespSerializer.asBulkString(arg);

                        outputStream.write(response.getBytes());
                        break;
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