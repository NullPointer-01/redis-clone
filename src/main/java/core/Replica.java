package core;

import requests.model.Client;

import java.net.Socket;

public class Replica {
    private final Client client;
    private int bytesProcessed;

    public Replica(Client client) {
        this.client = client;
    }

    public Socket getSocket() {
        return client.getSocket();
    }

    public void setBytesProcessed(int bytesProcessed) {
        this.bytesProcessed = bytesProcessed;
    }
}
