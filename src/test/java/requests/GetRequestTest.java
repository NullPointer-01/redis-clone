package requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.RepositoryManager;
import repository.Storage;
import requests.model.Response;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static util.RespConstants.NULL_BULK_STRING;

public class GetRequestTest {

    private Storage<String, String> storage;

    @BeforeEach
    public void init() {
        storage = RepositoryManager.getInstance();
    }

    @Test
    public void shouldReturnValueForKey() {
        String key = "myKey";
        String value = "myValue";

        storage.set(key, value, null);

        GetRequest getRequest = new GetRequest(key);
        Response response = getRequest.execute();

        assertArrayEquals("$7\r\nmyValue\r\n".getBytes(), response.getResponse());
    }

    @Test
    public void shouldReturnNullForMissingKey() {
        String missingKey = "missingKey";

        GetRequest getRequest = new GetRequest(missingKey);
        Response response = getRequest.execute();

        assertArrayEquals(NULL_BULK_STRING.getBytes(), response.getResponse());
    }
}
