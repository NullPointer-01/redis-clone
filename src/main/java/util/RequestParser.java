package util;

import conf.Configuration;
import requests.*;
import requests.master.InfoMasterRequest;
import requests.master.TypeMasterCommand;
import requests.master.lists.*;
import requests.master.repl.PSyncMasterRequest;
import requests.master.repl.ReplConfMasterRequest;
import requests.master.streams.XAddMasterRequest;
import requests.master.streams.XRangeMasterRequest;
import requests.master.streams.XReadMasterRequest;
import requests.master.strings.DelMasterRequest;
import requests.master.strings.GetMasterRequest;
import requests.master.strings.IncrMasterRequest;
import requests.master.strings.SetMasterRequest;
import requests.master.txn.DiscardMasterRequest;
import requests.master.txn.ExecMasterRequest;
import requests.master.txn.MultiMasterRequest;
import requests.master.zsets.*;
import requests.model.Command;
import requests.slave.InfoSlaveRequest;
import requests.slave.lists.LLenSlaveRequest;
import requests.slave.lists.LRangeSlaveRequest;
import requests.slave.repl.ReplConfSlaveRequest;
import requests.slave.repl.SetSlaveRequest;
import requests.slave.strings.GetSlaveRequest;

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
            case XADD:
                requests.add(new XAddMasterRequest(items.get(1), items.get(2), items.subList(3, items.size())));
                break;
            case XRANGE:
                requests.add(new XRangeMasterRequest(items.get(1), items.get(2), items.get(3)));
                break;
            case XREAD:
                requests.add(new XReadMasterRequest(items.subList(2, items.size())));
                break;
            case ZADD:
                requests.add(new ZAddMasterRequest(items.get(1), Double.parseDouble(items.get(2)), items.get(3)));
                break;
            case ZREM:
                requests.add(new ZRemMasterRequest(items.get(1), items.get(2)));
                break;
            case ZRANK:
                requests.add(new ZRankMasterRequest(items.get(1), items.get(2)));
                break;
            case ZCARD:
                requests.add(new ZCardMasterRequest(items.get(1)));
                break;
            case ZSCORE:
                requests.add(new ZScoreMasterRequest(items.get(1), items.get(2)));
                break;
            case ZRANGE:
                requests.add(new ZRangeMasterRequest(items.get(1), Integer.parseInt(items.get(2)), Integer.parseInt(items.get(3))));
                break;
            default:
                requests.add(new InvalidRequest(items));
        }
    }

    private static void addSlaveRequests(List<Request> requests, List<String> items) throws IOException {
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
                requests.add(new InvalidRequest(items));
        }
    }

    public static List<Request> parseReplicationRequests(InputStream is) throws IOException {
        List<Request> requests = new ArrayList<>();

        while (is.available() > 0) {
            char c = (char) is.read();
            if (c != ASTERISK) {
                continue;
            }

            List<Object> itemsTmp = RespDeserializer.parseArray(is);
            List<String> items = itemsTmp.stream().map(i -> (String) i).toList();

            Command command = Command.getCommandByName(items.get(0).toUpperCase());

            switch (command) {
                case REPLCONF:
                    requests.add(new ReplConfSlaveRequest());
                    break;
                case SET:
                    Long timeToExpireInMillis = items.size() == 3 ? null : Long.parseLong(items.get(4));
                    requests.add(new SetSlaveRequest(items.get(1), items.get(2), timeToExpireInMillis));
                    break;
                default:
                    throw new IOException("Invalid command " + command);
            }
        }

        return requests;
    }

    public static void parseRdbFile(InputStream is, Configuration conf) throws IOException {
        while (is.available() > 0) {
            char ignored = (char) is.read();
        }
    }
}
