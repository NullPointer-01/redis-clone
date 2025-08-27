package core;

import conf.Configuration;
import service.RequestHandler;
import service.SlaveReplicationHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;

public class SlaveServer extends Server {
    public SlaveServer(Configuration conf) {
        super(conf);
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(conf.getPort())) {
            serverSocket.setReuseAddress(true);

            new SlaveReplicationHandler(conf).start(); // Sync with master in separate thread

            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                new RequestHandler(socket).start();
            }
        } catch (SocketException e) {
            LOGGER.log(Level.SEVERE, "Socket Exception starting up redis slave server. " + e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IO Exception starting up redis slave server. " + e);
        }
    }
}