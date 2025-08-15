package requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.RepositoryManager;
import repository.Storage;
import requests.model.Response;

import static org.junit.jupiter.api.Assertions.*;
import static util.RespConstants.OK_SIMPLE_STRING;

public class SetRequestTest {
    private Storage<String, String> storage;

    @BeforeEach
    public void init() {
        storage = RepositoryManager.getInstance();
    }

    @Test
    public void testSetWithNoTTL() {
        String key = "key";
        String value = "value";

        SetRequest setRequest = new SetRequest(key, value, null);
        Response response = setRequest.execute();

        assertTrue(storage.get(key).isPresent());
        assertEquals(value, storage.get(key).get());
        assertArrayEquals(OK_SIMPLE_STRING.getBytes(), response.getResponse());
    }

    @Test
    public void testSetWithTTL() {
        String key = "key";
        String value = "value";
        Long ttl = 100L;

        SetRequest setRequest = new SetRequest(key, value, ttl);
        Response response = setRequest.execute();

        assertTrue(storage.get(key).isPresent());
        assertEquals(value, storage.get(key).get());

        try {
            Thread.sleep(100);
            assertTrue(storage.get(key).isEmpty()); // Check is empty after expiry
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertArrayEquals(OK_SIMPLE_STRING.getBytes(), response.getResponse());
    }
}
