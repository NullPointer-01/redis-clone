package requests.master;

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
public class GetMasterRequestTest {

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

        GetMasterRequest getRequest = new GetMasterRequest(key);
        Response response = getRequest.doExecute();

        assertArrayEquals("$7\r\nmyValue\r\n".getBytes(), response.getResponse());

        storage.delete(List.of("myKey"));
    }

    @Test
    public void shouldReturnNullForMissingKey() {
        String missingKey = "missingKey";

        GetMasterRequest getRequest = new GetMasterRequest(missingKey);
        Response response = getRequest.doExecute();

        assertArrayEquals(NULL_BULK_STRING.getBytes(), response.getResponse());
    }
}
