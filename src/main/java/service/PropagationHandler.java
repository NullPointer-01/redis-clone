package service;

import core.Replica;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropagationHandler {
    protected static final Logger LOGGER = Logger.getLogger(PropagationHandler.class.getName());

    private final byte[] request;
    private final Set<Replica> replicas;

    public PropagationHandler(byte[] request, Set<Replica> replicas) {
        this.request = request;
        this.replicas = replicas;
    }

    public void propagateRequests() {
        CompletableFuture.runAsync(() -> {
            for (Replica replica : replicas) {
                try {
                    OutputStream outputStream = replica.getClient().getOutputStream();
                    outputStream.write(request);
                    outputStream.flush();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Exception propagating request to replica. " + e);
                }
            }
        });
    }
}
