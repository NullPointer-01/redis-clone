package util;

import conf.Configuration;
import requests.*;
import requests.master.InfoMasterRequest;
import requests.master.TypeMasterCommand;
import requests.master.geo.GeoAddMasterRequest;
import requests.master.geo.GeoDistMasterRequest;
import requests.master.geo.GeoPosMasterRequest;
import requests.master.hashes.*;
import requests.master.lists.*;
import requests.master.pubsub.PublishRequest;
import requests.master.pubsub.SubscribeRequest;
import requests.master.pubsub.UnsubscribeRequest;
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
import requests.slave.hashes.HDelSlaveRequest;
import requests.slave.hashes.HIncrBySlaveRequest;
import requests.slave.hashes.HSetNXSlaveRequest;
import requests.slave.hashes.HSetSlaveRequest;
import requests.slave.lists.LPopSlaveRequest;
import requests.slave.lists.LPushSlaveRequest;
import requests.slave.lists.RPushSlaveRequest;
import requests.slave.repl.ReplConfSlaveRequest;
import requests.slave.strings.DelSlaveRequest;
import requests.slave.strings.IncrSlaveRequest;
import requests.slave.strings.SetSlaveRequest;
import requests.slave.zsets.ZAddSlaveRequest;
import requests.slave.zsets.ZRemSlaveRequest;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static util.RespConstants.ASTERISK;

public class RequestParser {
    // Private constructor to prevent instantiations
    private RequestParser() {}

    public static List<Request> parseRequests(ByteBuffer buffer, Configuration conf) throws IOException {
        List<Request> requests = new ArrayList<>();
        buffer.flip();

        while (buffer.hasRemaining()) {
            char c = (char) buffer.get();
            if (c != ASTERISK) {
                throw new IOException("Invalid byte, expected array " + c);
            }

            List<Object> itemsTmp = RespDeserializer.parseArray(buffer);
            List<String> items = itemsTmp.stream().map(i -> (String) i).toList();

            if (conf.isMaster()) {
                addMasterRequests(requests, items);
            } else {
                addSlaveRequests(requests, items);
            }
        }

        buffer.clear();
        return requests;
    }

    public static List<Request> parseSubscriberRequests(ByteBuffer buffer) throws IOException {
        List<Request> requests = new ArrayList<>();
        buffer.flip();

        while (buffer.hasRemaining()) {
            char c = (char) buffer.get();
            if (c != ASTERISK) {
                throw new IOException("Invalid byte, expected array " + c);
            }

            List<Object> itemsTmp = RespDeserializer.parseArray(buffer);
            List<String> items = itemsTmp.stream().map(i -> (String) i).toList();

            Command command = Command.getCommandByName(items.get(0).toUpperCase());

            switch (command) {
                case SUBSCRIBE:
                    requests.add(new SubscribeRequest(items.get(1)));
                    break;
                case UNSUBSCRIBE:
                    requests.add(new UnsubscribeRequest(items.get(1)));
                    break;
                case PSUBSCRIBE:
                case PUNSUBSCRIBE:
                case QUIT:
                case PING:
                    requests.add(new PingRequest());
                    break;
                default:
                    requests.add(new UnsupportedRequest(items));
            }
        }

        buffer.clear();
        return requests;
    }

    private static void addMasterRequests(List<Request> requests, List<String> items) {
        Command command = Command.getCommandByName(items.get(0).toUpperCase());

        try {
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
                    Long timeToExpireInMillis = items.size() == 3 ? null : Long.parseLong(items.get(3));
                    requests.add(new SetMasterRequest(items.get(1), items.get(2), timeToExpireInMillis));
                    break;
                case GET:
                    requests.add(new GetMasterRequest(items.get(1)));
                    break;
                case DEL:
                    requests.add(new DelMasterRequest(items.subList(1, items.size())));
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
                case BLPOP:
                    requests.add(new BLPopMasterRequest(items.subList(1, items.size()-1), 0));
                    break;
                case HSET:
                    requests.add(new HSetMasterRequest(items.get(1), items.subList(2, items.size())));
                    break;
                case HSETNX:
                    requests.add(new HSetNXMasterRequest(items.get(1), items.get(2), items.get(3)));
                    break;
                case HGET:
                    requests.add(new HGetMasterRequest(items.get(1), items.get(2)));
                    break;
                case HMGET:
                    requests.add(new HMGetMasterRequest(items.get(1), items.subList(2, items.size())));
                    break;
                case HGETALL:
                    requests.add(new HGetAllMasterRequest(items.get(1)));
                    break;
                case HKEYS:
                    requests.add(new HKeysMasterRequest(items.get(1)));
                    break;
                case HVALS:
                    requests.add(new HValsMasterRequest(items.get(1)));
                    break;
                case HEXISTS:
                    requests.add(new HExistsMasterRequest(items.get(1), items.get(2)));
                    break;
                case HLEN:
                    requests.add(new HLenMasterRequest(items.get(1)));
                    break;
                case HDEL:
                    requests.add(new HDelMasterRequest(items.get(1), items.subList(2, items.size())));
                    break;
                case HINCRBY:
                    requests.add(new HIncrByMasterRequest(items.get(1), items.get(2), Integer.parseInt(items.get(3))));
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
                case SUBSCRIBE:
                    requests.add(new SubscribeRequest(items.get(1)));
                    break;
                case PUBLISH:
                    requests.add(new PublishRequest(items.get(1), items.get(2)));
                    break;
                case GEOADD:
                    requests.add(new GeoAddMasterRequest(items.get(1), items.subList(2, items.size())));
                    break;
                case GEOPOS:
                    requests.add(new GeoPosMasterRequest(items.get(1), items.subList(2, items.size())));
                    break;
                case GEODIST:
                    requests.add(new GeoDistMasterRequest(items.get(1), items.get(2), items.get(3)));
                    break;
                default:
                    requests.add(new UnsupportedRequest(items));
            }
        } catch (RuntimeException e) {
            requests.add(new InvalidRequest(items));
        }
    }

    private static void addSlaveRequests(List<Request> requests, List<String> items) {
        Command command = Command.getCommandByName(items.get(0).toUpperCase());

        try {
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
                    requests.add(new GetMasterRequest(items.get(1)));
                    break;
                case LRANGE:
                    requests.add(new LRangeMasterRequest(items.get(1), Integer.parseInt(items.get(2)), Integer.parseInt(items.get(3))));
                    break;
                case LLEN:
                    requests.add(new LLenMasterRequest(items.get(1)));
                    break;
                case HGET:
                    requests.add(new HGetMasterRequest(items.get(1), items.get(2)));
                    break;
                case HMGET:
                    requests.add(new HMGetMasterRequest(items.get(1), items.subList(2, items.size())));
                    break;
                case HGETALL:
                    requests.add(new HGetAllMasterRequest(items.get(1)));
                    break;
                case HKEYS:
                    requests.add(new HKeysMasterRequest(items.get(1)));
                    break;
                case HVALS:
                    requests.add(new HValsMasterRequest(items.get(1)));
                    break;
                case HEXISTS:
                    requests.add(new HExistsMasterRequest(items.get(1), items.get(2)));
                    break;
                case HLEN:
                    requests.add(new HLenMasterRequest(items.get(1)));
                    break;
                case INFO:
                    requests.add(new InfoSlaveRequest());
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
                    requests.add(new UnsupportedRequest(items));
            }
        } catch (RuntimeException e) {
            requests.add(new InvalidRequest(items));
        }
    }

    public static List<Request> parseReplicationRequests(ByteBuffer buffer) throws IOException {
        List<Request> requests = new ArrayList<>();
        buffer.flip();

        while (buffer.hasRemaining()) {
            char c = (char) buffer.get();
            if (c != ASTERISK) {
                continue;
            }

            List<Object> itemsTmp = RespDeserializer.parseArray(buffer);
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
                case DEL:
                    requests.add(new DelSlaveRequest(items.subList(1, items.size())));
                    break;
                case INCR:
                    requests.add(new IncrSlaveRequest(items.get(1)));
                    break;
                case RPUSH:
                    requests.add(new RPushSlaveRequest(items.get(1), items.subList(2, items.size())));
                    break;
                case LPUSH:
                    requests.add(new LPushSlaveRequest(items.get(1), items.subList(2, items.size())));
                    break;
                case LPOP:
                    Integer count = items.size() == 3 ? Integer.parseInt(items.get(2)) : null;
                    requests.add(new LPopSlaveRequest(items.get(1), count));
                    break;
                case HSET:
                    requests.add(new HSetSlaveRequest(items.get(1), items.subList(2, items.size())));
                    break;
                case HSETNX:
                    requests.add(new HSetNXSlaveRequest(items.get(1), items.get(2), items.get(3)));
                    break;
                case HDEL:
                    requests.add(new HDelSlaveRequest(items.get(1), items.subList(2, items.size())));
                    break;
                case HINCRBY:
                    requests.add(new HIncrBySlaveRequest(items.get(1), items.get(2), Integer.parseInt(items.get(3))));
                    break;
                case ZADD:
                    requests.add(new ZAddSlaveRequest(items.get(1), Double.parseDouble(items.get(2)), items.get(3)));
                    break;
                case ZREM:
                    requests.add(new ZRemSlaveRequest(items.get(1), items.get(2)));
                    break;
                default:
                    throw new IOException("Invalid command " + command);
            }
        }

        buffer.clear();
        return requests;
    }

    public static void parseRdbFile(ByteBuffer buffer) {
        buffer.flip();
        buffer.clear();
    }

    public static Request parseAOFRequests(String line) {
        List<String> parts = List.of(line.split(" "));
        Command command = Command.getCommandByName(parts.get(0).toUpperCase());

        switch (command) {
            case SET:
                Long timeToExpireInMillis = parts.size() == 3 ? null : Long.parseLong(parts.get(3));
                return new SetSlaveRequest(parts.get(1), parts.get(2), timeToExpireInMillis);
            case DEL:
                return new DelSlaveRequest(parts.subList(1, parts.size()));
            case INCR:
                return new IncrSlaveRequest(parts.get(1));
            case RPUSH:
                return new RPushSlaveRequest(parts.get(1), parts.subList(2, parts.size()));
            case LPUSH:
                return new LPushSlaveRequest(parts.get(1), parts.subList(2, parts.size()));
            case LPOP:
                Integer count = parts.size() == 3 ? Integer.parseInt(parts.get(2)) : null;
                return new LPopSlaveRequest(parts.get(1), count);
            case HSET:
                return new HSetSlaveRequest(parts.get(1), parts.subList(2, parts.size()));
            case HSETNX:
                return new HSetNXSlaveRequest(parts.get(1), parts.get(2), parts.get(3));
            case HDEL:
                return new HDelSlaveRequest(parts.get(1), parts.subList(2, parts.size()));
            case HINCRBY:
                return new HIncrBySlaveRequest(parts.get(1), parts.get(2), Integer.parseInt(parts.get(3)));
            case ZADD:
                return new ZAddSlaveRequest(parts.get(1), Double.parseDouble(parts.get(2)), parts.get(3));
            case ZREM:
                return new ZRemSlaveRequest(parts.get(1), parts.get(2));
        }

        return new UnsupportedRequest(parts);
    }
}
