package util;

import conf.Configuration;
import requests.*;
import requests.master.*;
import requests.model.Command;
import requests.slave.GetSlaveRequest;
import requests.slave.InfoSlaveRequest;
import requests.slave.LLenSlaveRequest;
import requests.slave.LRangeSlaveRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static util.RespConstants.ASTERISK;

public class RequestParser {
    // Private constructor to prevent instantiations
    private RequestParser() {}

    public static List<Request> parseRequests(InputStream is, Configuration conf) throws IOException {
        List<Request> requests = new ArrayList<>();

        while (is.available() > 0) {
            char c = (char) is.read();
            if (c != ASTERISK) {
                throw new IOException("Invalid byte, expected array " + c);
            }

            List<Object> itemsTmp = RespDeserializer.parseArray(is);
            List<String> items = itemsTmp.stream().map(i -> (String) i).toList();

            if (conf.isMaster()) {
                addMasterRequests(requests, items);
            } else {
                addSlaveRequests(requests, items);
            }
        }

        return requests;
    }

    private static void addMasterRequests(List<Request> requests, List<String> items) {
        Command command = Command.getCommandByName(items.get(0).toUpperCase());

        switch (command) {
            case COMMAND:
                requests.add(new CommandRequest());
                break;
            case PING:
                requests.add(new PingRequest());
                break;
            case ECHO:
                requests.add(new EchoRequest(items.get(1)));
                break;
            case SET:
                Long timeToExpireInMillis = items.size() == 3 ? null : Long.parseLong(items.get(4));
                requests.add(new SetMasterRequest(items.get(1), items.get(2), timeToExpireInMillis));
                break;
            case GET:
                requests.add(new GetMasterRequest(items.get(1)));
                break;
            case DEL:
                requests.add(new DelMasterRequest(items.subList(2, items.size())));
                break;
            case INCR:
                requests.add(new IncrMasterRequest(items.get(1)));
                break;
            case RPUSH:
                requests.add(new RPushMasterRequest(items.get(1), items.subList(2, items.size())));
                break;
            case LPUSH:
                requests.add(new LPushMasterRequest(items.get(1), items.subList(2, items.size())));
                break;
            case LPOP:
                Integer count = items.size() == 3 ? Integer.parseInt(items.get(2)) : null;
                requests.add(new LPopMasterRequest(items.get(1), count));
                break;
            case LRANGE:
                requests.add(new LRangeMasterRequest(items.get(1), Integer.parseInt(items.get(2)), Integer.parseInt(items.get(3))));
                break;
            case LLEN:
                requests.add(new LLenMasterRequest(items.get(1)));
                break;
            case INFO:
                requests.add(new InfoMasterRequest());
                break;
            case REPLCONF:
                requests.add(new ReplConfMasterRequest());
                break;
            case PSYNC:
                requests.add(new PSyncMasterRequest());
                break;
            case MULTI:
                requests.add(new MultiMasterRequest());
                break;
            case EXEC:
                requests.add(new ExecMasterRequest());
                break;
            case DISCARD:
                requests.add(new DiscardMasterRequest());
                break;
            case TYPE:
                requests.add(new TypeMasterCommand(items.get(1)));
                break;
            default:
                requests.add(new InvalidRequest(items));
        }
    }

    private static void addSlaveRequests(List<Request> requests, List<String> items) throws IOException {
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
                requests.add(new SetMasterRequest(items.get(1), items.get(2), timeToExpireInMillis));
                break;
            case GET:
                requests.add(new GetSlaveRequest(items.get(1)));
                break;
            case LRANGE:
                requests.add(new LRangeSlaveRequest(items.get(1), Integer.parseInt(items.get(2)), Integer.parseInt(items.get(3))));
                break;
            case LLEN:
                requests.add(new LLenSlaveRequest(items.get(1)));
                break;
            case INFO:
                requests.add(new InfoSlaveRequest());
                break;
            default:
                throw new IOException("Invalid command " + command);
        }
    }
}
