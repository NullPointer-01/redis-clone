package requests.master.pubsub;

import requests.Request;
import requests.model.Channel;
import requests.model.Client;
import requests.model.Command;
import requests.model.Response;
import service.PubSubManager;
import util.RespSerializer;

import java.io.IOException;
import java.util.List;

import static constants.Constants.UNSUBSCRIBE;

public class UnsubscribeRequest implements Request {
    private final Channel channel;

    public UnsubscribeRequest(String channelName) {
        this.channel = PubSubManager.getInstance().getChannel(channelName);
    }

    @Override
    public Command getCommand() {
        return Command.UNSUBSCRIBE;
    }

    @Override
    public void execute(Client client) throws IOException {
        client.startSubscription();

        client.unsubscribeChannel(channel);
        channel.removeClient(client);

        Response response = new Response(RespSerializer.asArray(
                List.of(UNSUBSCRIBE, channel.getName(), String.valueOf(client.getChannels().size())))
        );
        client.write(response.getResponse());
    }
}

