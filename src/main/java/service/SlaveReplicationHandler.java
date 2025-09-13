package service;

import conf.Configuration;
import conf.SlaveConfiguration;
import requests.Request;
import requests.model.Client;
import util.RequestParser;
import util.RespSerializer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static util.RespConstants.OK_SIMPLE_STRING;
import static util.RespConstants.PONG_SIMPLE_STRING;

public class SlaveReplicationHandler extends Thread {
    private static final Logger LOGGER = Logger.getLogger(SlaveReplicationHandler.class.getName());
    private static final Pattern FULL_RESYNC_PATTERN = Pattern.compile("^\\+FULLRESYNC (?<replId>\\w+) (?<offset>\\d+)\\r\\n$");

    private final Configuration conf;

    public SlaveReplicationHandler(Configuration conf) {
        this.conf = conf;
    }

    @Override
    public void run() {
        syncWithMaster();
    }

    private void syncWithMaster () {
        SlaveConfiguration slaveConfiguration = (SlaveConfiguration) conf;

        String masterHost = slaveConfiguration.getMasterHost();
        int masterPort = slaveConfiguration.getMasterPort();
        int retryDelay = 1000;

        while (true) {
            try (SocketChannel ignored = SocketChannel.open()) {
                ignored.connect(new InetSocketAddress(masterHost, masterPort));
                break;
            } catch (IOException e) {
                LOGGER.log(Level.INFO, "Port not open, retrying...", e);
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException ignored) {
                }
            }
        }

        try (SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.connect(new InetSocketAddress(masterHost, masterPort));
            socketChannel.configureBlocking(true);

            Client client = new Client(socketChannel);

            initiateHandshake(client);
            receiveRdbFile(client);

            LOGGER.log(Level.INFO, "Waiting for requests from master...");
            while (true) {
                ByteBuffer buffer = client.getBuffer();

                int read = client.read(buffer);
                if (read == -1) {
                    break;
                }

                List<Request> requests = RequestParser.parseReplicationRequests(buffer);

                for (Request request : requests) {
                    request.execute(client);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initiateHandshake(Client client) throws IOException {
        // Max of length of (PONG, OK, FULL_RESYNC)
        int pongLen = PONG_SIMPLE_STRING.getBytes().length;
        int okLen = OK_SIMPLE_STRING.getBytes().length;
        int fullResyncLen = 56; // +FULLRESYNC <REPL_ID> 0\r\n

        ByteBuffer buffer = client.getBuffer();

        // Send PING
        String PING = RespSerializer.asArray(List.of("PING"));
        client.write(PING.getBytes(StandardCharsets.UTF_8));

        client.read(buffer);
        String response1 = new String(buffer.array(), 0, pongLen, StandardCharsets.UTF_8);
        buffer.clear();

        if (!PONG_SIMPLE_STRING.equals(response1)) {
            throw new IOException("Handshake with master failed, expected PONG");
        }

        // Send REPLCONF listening-port
        String REPL_CONF_1 = RespSerializer.asArray(List.of("REPLCONF", "listening-port", conf.getPort().toString()));
        client.write(REPL_CONF_1.getBytes(StandardCharsets.UTF_8));

        client.read(buffer);
        String response2 = new String(buffer.array(), 0, okLen, StandardCharsets.UTF_8);
        buffer.clear();

        if (!OK_SIMPLE_STRING.equals(response2)) {
            throw new IOException("Handshake with master failed, expected OK for REPLCONF listening-port");
        }

        // Send REPLCONF capa
        String REPL_CONF_2 = RespSerializer.asArray(List.of("REPLCONF", "capa", "psync2"));
        client.write(REPL_CONF_2.getBytes(StandardCharsets.UTF_8));

        client.read(buffer);
        String response3 = new String(buffer.array(), 0, okLen, StandardCharsets.UTF_8);
        buffer.clear();

        if (!OK_SIMPLE_STRING.equals(response3)) {
            throw new IOException("Handshake with master failed, expected OK for REPLCONF capa");
        }

        // Send PSYNC
        String PSYNC = RespSerializer.asArray(List.of("PSYNC", "?", "-1"));
        client.write(PSYNC.getBytes(StandardCharsets.UTF_8));

        client.read(buffer);
        String response4 = new String(buffer.array(), 0, fullResyncLen, StandardCharsets.UTF_8);
        buffer.clear();

        Matcher matcher = FULL_RESYNC_PATTERN.matcher(response4);
        if (!matcher.matches()) {
            throw new IOException("Handshake with master failed, expected proper response for PSYNC");
        }

        LOGGER.log(Level.INFO, "Handshake completed");
    }

    private void receiveRdbFile(Client client) throws IOException {
        ByteBuffer buffer = client.getBuffer();
        client.read(buffer);

        RequestParser.parseRdbFile(buffer);
    }
}
