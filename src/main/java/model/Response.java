package model;

public class Response {
    private final String response;

    public Response(String response) {
        this.response = response;
    }

    public byte[] getResponse() {
        return response.getBytes();
    }
}
