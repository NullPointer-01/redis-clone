package requests.master.pubsub;

import requests.Request;
import requests.model.Channel;
import requests.model.Client;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import static constants.Constants.SUBSCRIBE;

public class SubscribeRequest implements Request {
    private final Channel channel;

    public SubscribeRequest(String channel) {
        this.channel = new Channel(channel);
    }

    @Override
    public Command getCommand() {
        return Command.SUBSCRIBE;
    }

    @Override
    public void execute(Client client) throws IOException {
        client.startSubscription();

        client.subscribeChannel(channel);
        channel.addClient(client);

        Response response = new Response(RespSerializer.asArray(
                List.of(SUBSCRIBE, channel.getName(), String.valueOf(client.getChannels().size())))
        );
        OutputStream outputStream = client.getSocket().getOutputStream();
        outputStream.write(response.getResponse());

        outputStream.flush();
    }
}
