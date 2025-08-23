package requests;

import org.junit.jupiter.api.Test;
import requests.model.Response;

import static org.junit.jupiter.api.Assertions.*;
import static util.RespConstants.PONG_SIMPLE_STRING;

class PingRequestTest {
    @Test
    public void shouldReturnPong() {
        PingRequest pingRequest = new PingRequest();
        Response response = pingRequest.doExecute();

        assertNotNull(response);
        assertArrayEquals(PONG_SIMPLE_STRING.getBytes(), response.getResponse());
    }
}