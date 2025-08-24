package requests.master;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import repository.RepositoryManager;
import repository.Storage;
import requests.master.strings.DelMasterRequest;
import requests.model.Response;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DelMasterRequestTest {
    private Storage<String, String> storage;

    @BeforeAll
    void init() {
        storage = RepositoryManager.getInstance();
    }

    @Test
    void deleteValueKeys() {
        List<String> keys = Arrays.asList("key1", "key2", "key3");
        for (String key : keys) {
            storage.set(key, "value", null);
        }

        DelMasterRequest delRequest = new DelMasterRequest(List.of("key1", "key2"));
        Response response = delRequest.doExecute();

        // Key1 and Key2 should be deleted
        assertNull(storage.get("key1").orElse(null));
        assertNull(storage.get("key2").orElse(null));

        assertNotNull(storage.get("key3").orElse(null));
        assertArrayEquals(":2\r\n".getBytes(), response.getResponse());

        storage.delete(List.of("key3"));
    }

    @Test
    void deleteValueAndListKeys() {
        List<String> keys = Arrays.asList("key1", "key2", "key3");
        for (String key : keys) {
            storage.set(key, "value", null);
            storage.lPush(key, List.of("value1", "value2"));
        }

        DelMasterRequest delRequest = new DelMasterRequest(List.of("key1"));
        Response response = delRequest.doExecute();

        assertArrayEquals(":1\r\n".getBytes(), response.getResponse());

        storage.delete(List.of("key2", "key3"));
    }
}