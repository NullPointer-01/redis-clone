package requests.master.repl;

import conf.ConfigurationManager;
import conf.MasterConfiguration;
import core.Replica;
import requests.AbstractRequest;
import requests.model.Client;
import requests.model.Command;
import requests.model.Response;
import service.MasterReplicationHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.logging.Level;

import static util.RespConstants.*;

public class PSyncMasterRequest extends AbstractRequest {
    private static final String EMPTY_RDB_FILE_PATH = "persistence/empty_db_base64";

    private final MasterConfiguration configuration;

    public PSyncMasterRequest() {
        super(Command.PSYNC);
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        this.configuration = (MasterConfiguration) configurationManager.getConfiguration();
    }

    @Override
    public Response doExecute() {
        String response = FULL_RESYNC + " " + configuration.getMasterReplId() + " 0" + CRLF;
        return new Response(response);
    }

    @Override
    public void postExecute(Client client) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(EMPTY_RDB_FILE_PATH)) {
            Objects.requireNonNull(is, "empty_db_base64 resource missing from classpath");

            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String emptyFileBase64 = br.readLine();

            byte[] emptyFileBytes = Base64.getDecoder().decode(emptyFileBase64);
            byte[] header = (DOLLAR + emptyFileBytes.length + CRLF).getBytes(StandardCharsets.UTF_8);

            byte[] data = new byte[header.length + emptyFileBytes.length];
            System.arraycopy(header, 0, data, 0, header.length);
            System.arraycopy(emptyFileBytes, 0, data,  header.length, emptyFileBytes.length);

            client.write(data);

            configuration.addReplica(new Replica(client));

            MasterReplicationHandler handler = MasterReplicationHandler.getInstance();
            if (!handler.isRunning()) handler.start();

        }   catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception getting rdb file " + e);
        }
    }
}
