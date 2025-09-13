package core;

import requests.model.Client;

import java.nio.ByteBuffer;

public class Replica {
    private final Client client;
    private int bytesProcessed;

    public Replica(Client client) {
        this.client = client;
    }

    public ByteBuffer getBuffer() {
        return client.getBuffer();
    }

    public void setBytesProcessed(int bytesProcessed) {
        this.bytesProcessed = bytesProcessed;
    }

    public Client getClient() {
        return client;
    }
}
