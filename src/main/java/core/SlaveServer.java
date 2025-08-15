package core;

import conf.Configuration;
import conf.SlaveConfiguration;
import service.RequestHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class SlaveServer extends Server {
    public SlaveServer(Configuration conf) {
        super(conf);
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(conf.getPort())) {
            serverSocket.setReuseAddress(true);

            initiateHandshake(); // Handshake with master

            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket client = serverSocket.accept();
                new RequestHandler(client).start();
            }
        } catch (SocketException e) {
            LOGGER.log(Level.SEVERE, "Socket Exception starting up redis slave server. " + e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "IO Exception starting up redis slave server. " + e);
        }
    }

    private void initiateHandshake() throws IOException {
        SlaveConfiguration slaveConfiguration = (SlaveConfiguration) conf;

        String masterHost = slaveConfiguration.getMasterHost();
        int masterPort = slaveConfiguration.getMasterPort();
        String PING = "*1\r\n$4\r\nPING\r\n";

        try (Socket socket = new Socket(masterHost, masterPort);
             OutputStream outputStream = socket.getOutputStream()) {

            outputStream.write(PING.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
    }
}
