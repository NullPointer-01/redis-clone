package service;

import conf.ConfigurationConstants;
import requests.Request;
import requests.model.Client;
import util.RequestParser;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AOFPersistenceHandler {
    private static final Logger LOGGER = Logger.getLogger(AOFPersistenceHandler.class.getName());

    private static AOFPersistenceHandler instance;

    private ExecutorService executorService;
    private BufferedWriter writer;

    public static AOFPersistenceHandler getInstance() {
        if (instance == null) {
            instance = new AOFPersistenceHandler();
        }

        return instance;
    }

    private AOFPersistenceHandler() {
        try {
            this.writer = new BufferedWriter(new FileWriter(ConfigurationConstants.AOF_FILE_PATH, true));
            this.executorService = Executors.newSingleThreadExecutor();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Append only file persistence setup failed");
        }
    }

    public void appendRequest(String request) {
        executorService.submit(
                () -> {
                    try {
                        writer.write(request);
                        writer.newLine();
                        writer.flush();
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "AOF write failed");
                    }
                }
        );
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
