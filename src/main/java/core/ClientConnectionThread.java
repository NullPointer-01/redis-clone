package core;

import model.Request;
import model.Response;
import service.RequestHandler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
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
        try (OutputStream outputStream = client.getOutputStream();
             BufferedInputStream bis = new BufferedInputStream(client.getInputStream())) {

            while (client.isConnected()) {
                if (bis.available() > 0) {
                    List<Request> requests = RequestHandler.parseRequests(bis);

                    for (Request request : requests) {
                        Response response = request.execute();

                        outputStream.write(response.getResponse());
                        outputStream.flush();
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception processing client request. " + e);
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