package service;

import conf.Configuration;
import conf.ConfigurationManager;
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
    private final Configuration conf;

    private boolean executeInTxn;
    private final List<Request> queuedRequests;

    public RequestHandler(Socket client) {
        this.client = client;
        this.conf = ConfigurationManager.getInstance().getConfiguration();

        this.executeInTxn = false;
        this.queuedRequests = new LinkedList<>();
    }

    @Override
    public void run() {
        try (BufferedInputStream bis = new BufferedInputStream(client.getInputStream())) {

            while (client.isConnected()) {
                List<Request> requests = RequestParser.parseRequests(bis, conf);

                for (Request request : requests) {
                    if (Command.MULTI.equals(request.getCommand())) {
                        executeInTxn = true;
                        request.execute(client);
                    } else if (Command.EXEC.equals(request.getCommand())) {
                        executeQueuedRequests(request);
                    } else if (executeInTxn) {
                        queuedRequests.add(request);
                    } else {
                        request.execute(client);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception processing client request. ", e);
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