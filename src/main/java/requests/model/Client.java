package requests.model;

import requests.Request;

import java.net.Socket;
import java.util.*;

public class Client {
    private final Socket socket;

    private MODE mode;
    private final List<Request> queuedRequests;
    private final Set<Channel> subscribedChannels;

    public Client(Socket socket) {
        this.socket = socket;
        this.mode = MODE.NORMAL;

        queuedRequests = new LinkedList<>();
        subscribedChannels = new HashSet<>();
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
        if (!mode.equals(MODE.NORMAL)) {
            throw new UnsupportedOperationException("Can't start a transaction");
        }
        this.mode = MODE.TRANSACTION;
    }

    public void endTransaction() {
        if (!mode.equals(MODE.TRANSACTION)) {
            throw new UnsupportedOperationException("Can't end a transaction when not in one");
        }
        this.mode = MODE.NORMAL;
        this.queuedRequests.clear();
    }

    public void startSubscription() {
        if (!mode.equals(MODE.NORMAL) && !mode.equals(MODE.SUBSCRIBED)) {
            throw new UnsupportedOperationException("Can't start a subscription");
        }
        this.mode = MODE.SUBSCRIBED;
    }

    public void endSubscription() {
        if (!mode.equals(MODE.SUBSCRIBED)) {
            throw new UnsupportedOperationException("Can't end a subscription when not in one");
        }
        this.mode = MODE.NORMAL;
    }

    public void subscribeChannel(Channel channel) {
        subscribedChannels.add(channel);
    }

    public void unsubscribeChannel(Channel channel) {
        subscribedChannels.remove(channel);
    }

    public Set<Channel> getChannels() {
        return Collections.unmodifiableSet(subscribedChannels);
    }

    public boolean inTransaction() {
        return mode.equals(MODE.TRANSACTION);
    }

    public boolean inSubscribedMode() {
        return mode.equals(MODE.SUBSCRIBED);
    }

    private enum MODE {
        NORMAL, TRANSACTION, SUBSCRIBED;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        return o == this;
    }
}
