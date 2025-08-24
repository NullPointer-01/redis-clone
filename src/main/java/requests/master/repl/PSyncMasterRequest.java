package requests.master.repl;

import conf.ConfigurationManager;
import conf.MasterConfiguration;
import core.Replica;
import requests.AbstractRequest;
import requests.model.Client;
import requests.model.Command;
import requests.model.Response;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;

import static util.RespConstants.*;

public class PSyncMasterRequest extends AbstractRequest {
    private static final String EMPTY_RDB_FILE_PATH = "src/main/resources/persistence/empty_db_base64";

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
        try (BufferedReader br = new BufferedReader(new FileReader(EMPTY_RDB_FILE_PATH))) {

            String emptyFileBase64 = br.readLine();
            byte[] emptyFileBytes = Base64.getDecoder().decode(emptyFileBase64);
            byte[] data = (DOLLAR + emptyFileBytes.length + CRLF).getBytes(StandardCharsets.UTF_8);

            configuration.addReplica(new Replica(client));

            OutputStream outputStream = client.getSocket().getOutputStream();
            outputStream.write(data);
            outputStream.flush();

        }   catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception getting rdb file " + e);
        }
    }
}
