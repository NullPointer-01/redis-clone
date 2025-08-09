package core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        try {
            OutputStream outputStream = client.getOutputStream();

            String response = "+PONG\r\n";
            outputStream.write(response.getBytes());
            outputStream.flush();
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