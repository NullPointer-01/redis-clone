package service;

import conf.Configuration;
import conf.SlaveConfiguration;
import requests.Request;
import requests.model.Client;
import util.RequestParser;
import util.RespSerializer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
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
            try (Socket ignored = new Socket(masterHost, masterPort)) {
                break;
            } catch (IOException e) {
                LOGGER.log(Level.INFO, "Port not open, retrying...");
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException ignored) {
                }
            }
        }

        try (Socket socket = new Socket(masterHost, masterPort)) {
            Client client = new Client(socket);
            BufferedInputStream is = new BufferedInputStream(socket.getInputStream());

            initiateHandshake(client);
            receiveRdbFile(client);

            LOGGER.log(Level.INFO, "Waiting for requests from master...");
            while (socket.isConnected()) {
                List<Request> requests = RequestParser.parseReplicationRequests(is);
                for (Request request : requests) {
                    request.execute(client);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initiateHandshake(Client client) throws IOException {
        Socket socket = client.getSocket();

        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();

        // Max of length of (PONG, OK, FULL_RESYNC)
        int pongLen = PONG_SIMPLE_STRING.getBytes().length;
        int okLen = OK_SIMPLE_STRING.getBytes().length;
        int fullResyncLen = 56; // +FULLRESYNC <REPL_ID> 0\r\n

        byte[] buf = new byte[fullResyncLen];

        // Send PING
        String PING = RespSerializer.asArray(List.of("PING"));
        outputStream.write(PING.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();

        inputStream.read(buf, 0, pongLen);
        String response1 = new String(buf, 0, pongLen, StandardCharsets.UTF_8);

        if (!PONG_SIMPLE_STRING.equals(response1)) {
            throw new IOException("Handshake with master failed, expected PONG");
        }

        // Send REPLCONF listening-port
        String REPL_CONF_1 = RespSerializer.asArray(List.of("REPLCONF", "listening-port", conf.getPort().toString()));
        outputStream.write(REPL_CONF_1.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();

        inputStream.read(buf, 0, okLen);
        String response2 = new String(buf, 0, okLen, StandardCharsets.UTF_8);

        if (!OK_SIMPLE_STRING.equals(response2)) {
            throw new IOException("Handshake with master failed, expected OK for REPLCONF listening-port");
        }

        // Send REPLCONF capa
        String REPL_CONF_2 = RespSerializer.asArray(List.of("REPLCONF", "capa", "psync2"));
        outputStream.write(REPL_CONF_2.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();

        inputStream.read(buf, 0, okLen);
        String response3 = new String(buf, 0, okLen, StandardCharsets.UTF_8);

        if (!OK_SIMPLE_STRING.equals(response3)) {
            throw new IOException("Handshake with master failed, expected OK for REPLCONF capa");
        }

        // Send PSYNC
        String PSYNC = RespSerializer.asArray(List.of("PSYNC", "?", "-1"));
        outputStream.write(PSYNC.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();

        inputStream.read(buf, 0, fullResyncLen);
        String response4 = new String(buf, 0, fullResyncLen, StandardCharsets.UTF_8);
        Matcher matcher = FULL_RESYNC_PATTERN.matcher(response4);

        if (!matcher.matches()) {
            throw new IOException("Handshake with master failed, expected proper response for PSYNC");
        }

        LOGGER.log(Level.INFO, "Handshake completed");
    }

    private void receiveRdbFile(Client client) throws IOException {
        Socket socket = client.getSocket();
        BufferedInputStream is = new BufferedInputStream(socket.getInputStream());

        RequestParser.parseRdbFile(is, conf);
    }
}
