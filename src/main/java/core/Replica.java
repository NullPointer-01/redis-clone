package core;

import java.net.Socket;

public class Replica {
    private final Socket client;

    public Replica(Socket client) {
        this.client = client;
    }

    public Socket getClient() {
        return client;
    }
}
