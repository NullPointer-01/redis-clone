package service;

import model.*;
import util.RespDeserializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static util.RespConstants.ASTERISK;

public class RequestHandler {
    public static List<Request> parseRequests(InputStream is) throws IOException {
        List<Request> requests = new ArrayList<>();
        char c = (char) is.read();
        if (c != ASTERISK) {
            throw new IOException("Invalid byte, expected array " + c);
        }

        List<Object> itemsTmp = RespDeserializer.parseArray(is);
        List<String > items = itemsTmp.stream().map(i -> (String) i).toList();
        Command command = Command.getCommandByName(items.get(0));

        switch (command) {
            case PING:
                requests.add(new PingRequest());
                break;
            case ECHO:
                requests.add(new EchoRequest(items.get(1)));
                break;
            case SET:
                Long timeToExpireInMillis = items.size() == 3 ? null : Long.parseLong(items.get(4));
                requests.add(new SetRequest(items.get(1), items.get(2), timeToExpireInMillis));
                break;
            case GET:
                requests.add(new GetRequest(items.get(1)));
                break;
            default:
                throw new IOException("Invalid command");
        }

        return requests;
    }
}
