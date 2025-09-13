package service;

import conf.Configuration;
import conf.ConfigurationManager;
import requests.Request;
import requests.model.Client;
import util.RequestParser;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventLoop {
    private static final Logger LOGGER = Logger.getLogger(EventLoop.class.getName());
    private static EventLoop instance;

    private final Configuration conf;

    public static EventLoop getInstance() {
        if (instance == null) {
            instance = new EventLoop();
        }

        return instance;
    }

    private EventLoop() {
        this.conf = ConfigurationManager.getInstance().getConfiguration();
    }

    public void start() throws IOException {
        try (Selector selector = Selector.open();
             ServerSocketChannel serverChannel = ServerSocketChannel.open()) {

            serverChannel.bind(new InetSocketAddress(conf.getPort()));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                Iterator<SelectionKey> keysItr = selector.selectedKeys().iterator();

                while (keysItr.hasNext()) {
                    SelectionKey key = keysItr.next();
                    keysItr.remove();

                    if (key.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();

                        SocketChannel socketChannel = channel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ, new Client(socketChannel));

                        LOGGER.log(Level.INFO, "Client connected: " + socketChannel.getRemoteAddress());
                    } else if (key.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        Client client = (Client) key.attachment();
                        ByteBuffer buffer = client.getBuffer();

                        int read;
                        try {
                            read = socketChannel.read(buffer);
                            if (read == -1) {
                                socketChannel.close();
                                continue;
                            }
                        } catch (IOException e) {
                            socketChannel.close();
                        }

                        List<Request> requests = handleRequests(client);

                        for (Request request : requests) {
                            request.execute(client);
                        }
                    }
                }
            }
        }
    }

    private List<Request> handleRequests(Client client) throws IOException {
        if (client.inSubscribedMode()) {
            return RequestParser.parseSubscriberRequests(client.getBuffer());
        }
        return RequestParser.parseRequests(client.getBuffer(), conf);
    }
}
