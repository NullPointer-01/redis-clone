package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static util.RespConstants.ASTERISK;

public class ResponseParser {
    // Private constructor to prevent instantiations
    private ResponseParser() {}

    public static List<String> parseResponse(InputStream is) throws IOException {
        List<String> response;

        char c = (char) is.read();
        if (c != ASTERISK) {
            throw new IOException("Invalid byte, expected array " + c);
        }

        List<Object> itemsTmp = RespDeserializer.parseArray(is);
        response = itemsTmp.stream().map(i -> (String) i).toList();

        return response;
    }
}
