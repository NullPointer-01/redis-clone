package util;

import requests.*;
import requests.model.Command;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static util.RespConstants.ASTERISK;

public class RequestParser {
    // Private constructor to prevent instantiations
    private RequestParser() {}

    public static List<Request> parseRequests(InputStream is) throws IOException {
        List<Request> requests = new ArrayList<>();

        while (is.available() > 0) {
            char c = (char) is.read();
            if (c != ASTERISK) {
                throw new IOException("Invalid byte, expected array " + c);
            }

            List<Object> itemsTmp = RespDeserializer.parseArray(is);
            List<String> items = itemsTmp.stream().map(i -> (String) i).toList();
            Command command = Command.getCommandByName(items.get(0).toUpperCase());

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
                case DEL:
                    requests.add(new DelRequest(items.subList(2, items.size())));
                    break;
                case RPUSH:
                    requests.add(new RPushRequest(items.get(1), items.subList(2, items.size())));
                    break;
                case LPUSH:
                    requests.add(new LPushRequest(items.get(1), items.subList(2, items.size())));
                    break;
                case LPOP:
                    Integer count = items.size() == 3 ? Integer.parseInt(items.get(2)) : null;
                    requests.add(new LPopRequest(items.get(1), count));
                    break;
                case LRANGE:
                    requests.add(new LRangeRequest(items.get(1), Integer.parseInt(items.get(2)), Integer.parseInt(items.get(3))));
                    break;
                case LLEN:
                    requests.add(new LLenRequest(items.get(1)));
                    break;
                case INFO:
                    requests.add(new InfoRequest());
                    break;
                default:
                    throw new IOException("Invalid command " + command);
            }
        }

        return requests;
    }
}
