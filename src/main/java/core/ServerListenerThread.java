package core;

import conf.Configuration;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerListenerThread extends Thread {
    private static final Logger LOGGER = Logger.getLogger(ServerListenerThread.class.getName());

    private final Configuration conf;

    public ServerListenerThread(Configuration conf) {
        this.conf = conf;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(conf.getPort())) {
            serverSocket.setReuseAddress(true);

            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket client = serverSocket.accept();
                new ClientConnectionThread(client).start();
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception starting up redis server. " + e);
        }
    }
}
