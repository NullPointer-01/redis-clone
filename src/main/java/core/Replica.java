package core;

import requests.model.Client;

import java.net.Socket;

public class Replica {
    private final Client client;

    public Replica(Client client) {
        this.client = client;
    }

    public Socket getSocket() {
        return client.getSocket();
    }
}
