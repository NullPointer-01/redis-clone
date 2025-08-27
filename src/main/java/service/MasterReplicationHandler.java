package service;

import conf.ConfigurationManager;
import conf.MasterConfiguration;
import core.Replica;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MasterReplicationHandler extends Thread {
    private static final Logger LOGGER = Logger.getLogger(MasterReplicationHandler.class.getName());
    private static MasterReplicationHandler instance;

    private volatile boolean isRunning;
    private final MasterConfiguration configuration;

    public static MasterReplicationHandler getInstance() {
        if (instance == null) {
            instance = new MasterReplicationHandler();
        }

        return instance;
    }

    private MasterReplicationHandler() {
        this.configuration = (MasterConfiguration) ConfigurationManager.getInstance().getConfiguration();
    }

    @Override
    public void run() {
        isRunning = true;
        super.run();
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
}
