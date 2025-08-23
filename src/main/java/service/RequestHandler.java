package service;

import conf.Configuration;
import conf.ConfigurationManager;
import requests.Request;
import requests.model.Client;
import util.RequestParser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler extends Thread {
    private static final Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());

    private final Client client;
    private final Configuration conf;

    public RequestHandler(Socket socket) {
        this.client = new Client(socket);
        this.conf = ConfigurationManager.getInstance().getConfiguration();
    }

    @Override
    public void run() {
        try (Socket socket = client.getSocket(); BufferedInputStream bis = new BufferedInputStream(socket.getInputStream())) {
            while (socket.isConnected()) {
                List<Request> requests = RequestParser.parseRequests(bis, conf);

                for (Request request : requests) {
                    request.execute(client);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception processing client request. ", e);
        }
    }
}