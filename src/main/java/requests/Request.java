package requests;

import requests.model.Command;

import java.io.IOException;
import java.net.Socket;

public interface Request {
    Command getCommand();

    void execute(Socket client) throws IOException;

    default void postExecute(Socket client) {}
}
