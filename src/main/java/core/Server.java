package core;

import conf.Configuration;
import service.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    private final Configuration conf;

    public Server(Configuration conf) {
        this.conf = conf;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(conf.getPort())) {
            serverSocket.setReuseAddress(true);

            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket client = serverSocket.accept();
                new RequestHandler(client).start();
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception starting up redis server. " + e);
        }
    }
}
