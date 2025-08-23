package core;

import conf.Configuration;
import service.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;

public class MasterServer extends Server {
    public MasterServer(Configuration conf) {
        super(conf);
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(conf.getPort())) {
            serverSocket.setReuseAddress(true);

            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                new RequestHandler(socket).start();
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception starting up redis master server. " + e);
        }
    }
}
