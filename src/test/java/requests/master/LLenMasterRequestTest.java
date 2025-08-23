package requests.master;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import repository.RepositoryManager;
import repository.Storage;
import requests.model.Response;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class LLenMasterRequestTest {

    private static Storage<String, String> storage;

    @BeforeAll
    public static void init() {
        storage = RepositoryManager.getInstance();
    }

    @Test
    public void shouldReturnCorrectLengthForExistingList() {
        String listKey = "myList";
        storage.lPush(listKey, Arrays.asList("one", "two", "three"));

        LLenMasterRequest lLenRequest = new LLenMasterRequest(listKey);
        Response response = lLenRequest.doExecute();

        assertArrayEquals(":3\r\n".getBytes(), response.getResponse());
    }

    @Test
    public void shouldReturnZeroForMissingList() {
        String listKey = "lLenMissingList";

        LLenMasterRequest lLenRequest = new LLenMasterRequest(listKey);
        Response response = lLenRequest.doExecute();

        assertArrayEquals(":0\r\n".getBytes(), response.getResponse());
    }
}

