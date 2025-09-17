package service;

import conf.ConfigurationManager;
import conf.MasterConfiguration;
import core.Replica;
import requests.model.Client;
import requests.model.Command;
import util.RespSerializer;
import util.ResponseParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MasterReplicationHandler extends Thread {
    private static final Logger LOGGER = Logger.getLogger(MasterReplicationHandler.class.getName());
    private static MasterReplicationHandler instance;

    private static final List<String> REPLCONF_GETACK = List.of("REPLCONF", "GETACK", "*");
    private static final String ACK = "ACK";

    private volatile boolean isRunning;
    private final ExecutorService executorService;
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
        this.executorService = Executors.newSingleThreadExecutor();
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
        executorService.submit(() -> {
            for (Replica replica : configuration.getReplicas()) {
                try {
                    Client client = replica.getClient();
                    client.write(request);
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
                Client client = replica.getClient();
                client.write(RespSerializer.asArray(REPLCONF_GETACK).getBytes(StandardCharsets.UTF_8));

                List<String> items = ResponseParser.parseResponse(client);

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

    public void shutdown() {
        executorService.shutdown();
    }
}
