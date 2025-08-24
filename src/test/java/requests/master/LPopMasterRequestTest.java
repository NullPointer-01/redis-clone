package requests.master;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import repository.RepositoryManager;
import repository.Storage;
import requests.master.lists.LPopMasterRequest;
import requests.model.Response;
import util.RespSerializer;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.RespConstants.EMPTY_RESP_ARRAY;
import static util.RespConstants.NULL_BULK_STRING;

public class LPopMasterRequestTest {

    private static Storage<String, String> storage;

    @BeforeAll
    public static void init() {
        storage = RepositoryManager.getInstance();
    }

    @Test
    public void shouldReturnBulkStringWhenSingleElementIsPopped() {
        String listKey = "myList";
        storage.lPush(listKey, Arrays.asList("one", "two", "three"));

        LPopMasterRequest request = new LPopMasterRequest(listKey, null);
        Response response = request.doExecute();

        assertArrayEquals("$5\r\nthree\r\n".getBytes(), response.getResponse());
    }

    @Test
    public void testLPopSingleElementReturnsNullBulkStringWhenEmpty() {
        String listKey = "emptyList";

        LPopMasterRequest request = new LPopMasterRequest(listKey, null);
        Response response = request.doExecute();

        assertArrayEquals(NULL_BULK_STRING.getBytes(), response.getResponse());
    }

    @Test
    public void testLPopMultipleElementsReturnsArray() {
        String listKey = "myListMulti";
        storage.lPush(listKey, Arrays.asList("a", "b", "c", "d"));

        LPopMasterRequest request = new LPopMasterRequest(listKey, 2);
        Response response = request.doExecute();

        List<String> expectedElements = Arrays.asList("d", "c");
        String expectedResp = RespSerializer.asArray(expectedElements);
        String actualResp = new String(response.getResponse(), StandardCharsets.UTF_8);

        assertEquals(expectedResp, actualResp);
    }

    @Test
    public void testLPopMultipleElementsReturnsEmptyArrayWhenNone() {
        String listKey = "emptyMultiList";

        LPopMasterRequest request = new LPopMasterRequest(listKey, 3);
        Response response = request.doExecute();

        String actualResp = new String(response.getResponse(), StandardCharsets.UTF_8);
        assertEquals(EMPTY_RESP_ARRAY, actualResp);
    }
}
