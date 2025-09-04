package service;

import conf.ConfigurationManager;
import conf.MasterConfiguration;
import core.Replica;
import requests.model.Command;
import util.RespSerializer;
import util.ResponseParser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MasterReplicationHandler extends Thread {
    private static final Logger LOGGER = Logger.getLogger(MasterReplicationHandler.class.getName());
    private static MasterReplicationHandler instance;

    private static final List<String> REPLCONF_GETACK = List.of("REPLCONF", "GETACK", "*");
    private static final String ACK = "ACK";

    private volatile boolean isRunning;
    private final MasterConfiguration configuration;

    public static MasterReplicationHandler getInstance() {
        if (instance == null) {
            instance = new MasterReplicationHandler();
            instance.setDaemon(true);
        }

        return instance;
    }

    private MasterReplicationHandler() {
        this.configuration = (MasterConfiguration) ConfigurationManager.getInstance().getConfiguration();
    }

    @Override
    public void run() {
        isRunning = true;
        while (true) {
            for (Replica replica : configuration.getReplicas()) {
                CompletableFuture.runAsync(getAckTask(replica));
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void propagateRequests(byte[] request) {
        CompletableFuture.runAsync(() -> {
            for (Replica replica : configuration.getReplicas()) {
                try {
                    OutputStream outputStream = replica.getSocket().getOutputStream();
                    outputStream.write(request);
                    outputStream.flush();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Exception propagating request to replica. " + e);
                }
            }
        });
    }

    public boolean isRunning() {
        return isRunning;
    }

    private static Runnable getAckTask(Replica replica) {
        return () -> {
            try {
                Socket socket = replica.getSocket();
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(RespSerializer.asArray(REPLCONF_GETACK).getBytes(StandardCharsets.UTF_8));
                outputStream.flush();

                InputStream is = new BufferedInputStream(socket.getInputStream());
                List<String> items = ResponseParser.parseResponse(is);

                if (!Command.REPLCONF.getName().equalsIgnoreCase(items.get(0))
                        || !ACK.equalsIgnoreCase((items.get(1)))) {

                    throw new IOException("Expected REPLCONF response");
                }

                int bytesProcessed = Integer.parseInt(items.get(2));
                replica.setBytesProcessed(bytesProcessed);

                LOGGER.log(Level.INFO, "Bytes processed " + bytesProcessed);

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Exception propagating request to replica. " + e);
            }
        };
    }
}
