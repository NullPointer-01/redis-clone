package util;

import requests.model.Client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import static util.RespConstants.ASTERISK;

public class ResponseParser {
    // Private constructor to prevent instantiations
    private ResponseParser() {}

    public static List<String> parseResponse(Client client) throws IOException {
        List<String> response;

        ByteBuffer buffer = client.getBuffer();
        buffer.flip();

        char c = (char) buffer.get();
        if (c != ASTERISK) {
            throw new IOException("Invalid byte, expected array " + c);
        }

        List<Object> itemsTmp = RespDeserializer.parseArray(buffer);
        response = itemsTmp.stream().map(i -> (String) i).toList();

        return response;
    }
}
