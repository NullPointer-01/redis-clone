package requests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import repository.RepositoryManager;
import repository.Storage;
import requests.model.Response;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static util.RespConstants.NULL_BULK_STRING;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GetRequestTest {

    private Storage<String, String> storage;

    @BeforeAll
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

        storage.delete(List.of("myKey"));
    }

    @Test
    public void shouldReturnNullForMissingKey() {
        String missingKey = "missingKey";

        GetRequest getRequest = new GetRequest(missingKey);
        Response response = getRequest.execute();

        assertArrayEquals(NULL_BULK_STRING.getBytes(), response.getResponse());
    }
}
