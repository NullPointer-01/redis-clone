package service;

import requests.model.Channel;

import java.util.HashMap;
import java.util.Map;

public class PubSubManager {
    private static PubSubManager instance;

    private final Map<String, Channel> channelsMap;

    // Private constructor to enforce singleton pattern
    private PubSubManager() {
        this.channelsMap = new HashMap<>();
    }

    public static PubSubManager getInstance() {
        if (instance == null) {
            instance = new PubSubManager();
        }

        return instance;
    }

    public Channel getChannel(String channelName) {
        if (channelsMap.containsKey(channelName)) {
            return channelsMap.get(channelName);
        }

        Channel channel = new Channel((channelName));
        channelsMap.put(channelName, channel);

        return channel;
    }
}
