package service;

import conf.ConfigurationConstants;
import conf.ConfigurationConstants.FSYNC_POLICY;
import conf.ConfigurationManager;
import requests.Request;
import requests.model.Client;
import util.RequestParser;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AOFPersistenceHandler {
    private static final Logger LOGGER = Logger.getLogger(AOFPersistenceHandler.class.getName());

    private static AOFPersistenceHandler instance;

    private FSYNC_POLICY fsyncPolicy;
    private ScheduledExecutorService executorService;

    private BufferedWriter writer;
    private List<String> buffer;

    public static AOFPersistenceHandler getInstance() {
        if (instance == null) {
            instance = new AOFPersistenceHandler();
        }

        return instance;
    }

    private AOFPersistenceHandler() {
        try {
            this.writer = new BufferedWriter(new FileWriter(ConfigurationConstants.AOF_FILE_PATH, true));
            this.fsyncPolicy = ConfigurationManager.getInstance().getConfiguration().getFsyncPolicy();

            if (FSYNC_POLICY.EVERY_SEC.equals(fsyncPolicy)) {
                this.buffer = new LinkedList<>();
                this.executorService = Executors.newScheduledThreadPool(1);
                this.executorService.scheduleAtFixedRate(this::flushToDisk, 1, 1, TimeUnit.SECONDS);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Append only file persistence setup failed");
        }
    }

    public void appendRequest(String request) {
        if (FSYNC_POLICY.ALWAYS.equals(fsyncPolicy)) {
            try {
                writer.write(request);
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "AOF write failed");
            }
        } else if (FSYNC_POLICY.EVERY_SEC.equals(fsyncPolicy)) {
            buffer.add(request);
        }
    }

    private void flushToDisk() {
        if (buffer.isEmpty()) return;

        try {
            for (String request : buffer) {
                writer.write(request);
                writer.newLine();
            }
            writer.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "AOF write failed");
        }

        buffer.clear();
    }

    public void loadFromAof() {
        File file = new File(ConfigurationConstants.AOF_FILE_PATH);
        if (!file.exists()) return;

        Client dummy = new Client(null);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Request request = RequestParser.parseAOFRequests(line);
                request.execute(dummy);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
