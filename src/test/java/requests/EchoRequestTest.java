package requests;

import org.junit.jupiter.api.Test;
import requests.model.Response;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static util.RespConstants.EMPTY_BULK_STRING;

public class EchoRequestTest {

    @Test
    public void executeSimpleString() {
        String arg = "hello";
        EchoRequest echoRequest = new EchoRequest(arg);
        Response response = echoRequest.doExecute();

        assertNotNull(response);
        assertArrayEquals("$5\r\nhello\r\n".getBytes(), response.getResponse());
    }

    @Test
    public void executeEmptyString() {
        String arg = "";
        EchoRequest echoRequest = new EchoRequest(arg);
        Response response = echoRequest.doExecute();

        assertArrayEquals(EMPTY_BULK_STRING.getBytes(), response.getResponse());
    }

    @Test
    public void executeStringWithSpecialCharacters() {
        String arg = "^23qfh:@\r\n";
        EchoRequest echoRequest = new EchoRequest(arg);
        Response response = echoRequest.doExecute();

        assertArrayEquals("$10\r\n^23qfh:@\r\n\r\n".getBytes(), response.getResponse());
    }
}
