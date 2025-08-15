package service;

import requests.Request;
import requests.model.Response;
import util.RequestParser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler extends Thread {
    private static final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());

    private final Socket client;

    public RequestHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try (OutputStream outputStream = client.getOutputStream();
             BufferedInputStream bis = new BufferedInputStream(client.getInputStream())) {

            while (client.isConnected()) {
                List<Request> requests = RequestParser.parseRequests(bis);

                for (Request request : requests) {
                    Response response = request.execute();

                    outputStream.write(response.getResponse());
                    outputStream.flush();
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