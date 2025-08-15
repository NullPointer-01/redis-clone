package requests.model;

import java.nio.charset.StandardCharsets;

public class Response {
    private final String response;

    public Response(String response) {
        this.response = response;
    }

    public byte[] getResponse() {
        return response.getBytes(StandardCharsets.UTF_8);
    }
}
