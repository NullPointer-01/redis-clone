package requests;

import requests.model.Client;
import requests.model.Command;

import java.io.IOException;

public interface Request {
    Command getCommand();

    void execute(Client client) throws IOException;

    default void postExecute(Client client) {}
}
