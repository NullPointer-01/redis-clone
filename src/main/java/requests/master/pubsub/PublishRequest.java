package requests.master.pubsub;

import requests.AbstractRequest;
import requests.model.Channel;
import requests.model.Client;
import requests.model.Command;
import requests.model.Response;
import service.PubSubManager;
import util.RespSerializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;

import static constants.Constants.MESSAGE;

public class PublishRequest extends AbstractRequest {
    private final Channel channel;
    private final String message;

    public PublishRequest(String channelName, String message) {
        super(Command.PUBLISH);
        this.channel = PubSubManager.getInstance().getChannel(channelName);
        this.message = message;
    }

    @Override
    public Response doExecute() {
        return new Response(RespSerializer.asInteger(channel.getClients().size()));
    }

    @Override
    public void postExecute(Client client) {
        for (Client subscriber : channel.getClients()) {
            try {
                subscriber.write(RespSerializer.asArray(List.of(MESSAGE, channel.getName(), message)).getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Message delivery failed for client: " + client, e);
            }
        }
    }
}


