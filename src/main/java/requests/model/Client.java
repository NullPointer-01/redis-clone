package requests.model;

import requests.Request;

import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Client {
    private final Socket socket;

    private boolean inTransaction;
    private final List<Request> queuedRequests;

    public Client(Socket socket) {
        this.socket = socket;
        queuedRequests = new LinkedList<>();
    }

    public Socket getSocket() {
        return socket;
    }

    public List<Request> getQueuedRequests() {
        return Collections.unmodifiableList(queuedRequests);
    }

    public void queueRequest(Request request) {
        this.queuedRequests.add(request);
    }

    public void startTransaction() {
        this.inTransaction = true;
    }

    public void endTransaction() {
        if (!inTransaction) {
            throw new UnsupportedOperationException("Can't end a transaction when not in one");
        }
        this.inTransaction = false;
        this.queuedRequests.clear();
    }

    public boolean inTransaction() {
        return inTransaction;
    }
}
