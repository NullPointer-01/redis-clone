package requests.master;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import repository.RepositoryManager;
import repository.Storage;
import requests.master.strings.SetMasterRequest;
import requests.model.Response;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static util.RespConstants.OK_SIMPLE_STRING;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SetMasterRequestTest {
    private Storage<String, String> storage;

    @BeforeAll
    public void init() {
        storage = RepositoryManager.getInstance();
    }

    @Test
    public void testSetWithNoTTL() {
        String key = "key";
        String value = "value";

        SetMasterRequest setRequest = new SetMasterRequest(key, value, null);
        Response response = setRequest.doExecute();

        assertTrue(storage.get(key).isPresent());
        assertEquals(value, storage.get(key).get());
        assertArrayEquals(OK_SIMPLE_STRING.getBytes(), response.getResponse());

        storage.delete(List.of(key)); // Cleanup
    }

    @Test
    public void testSetWithTTL() {
        String key = "key";
        String value = "value";
        Long ttl = 100L;

        SetMasterRequest setRequest = new SetMasterRequest(key, value, ttl);
        Response response = setRequest.doExecute();

        assertTrue(storage.get(key).isPresent());
        assertEquals(value, storage.get(key).get());

        try {
            Thread.sleep(100);
            assertTrue(storage.get(key).isEmpty()); // Check is empty after expiry
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertArrayEquals(OK_SIMPLE_STRING.getBytes(), response.getResponse());
        storage.delete(List.of(key)); // Cleanup
    }
}
