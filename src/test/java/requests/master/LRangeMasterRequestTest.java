package requests.master;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import repository.RepositoryManager;
import repository.Storage;
import requests.model.Response;
import util.RespSerializer;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.RespConstants.EMPTY_RESP_ARRAY;

public class LRangeMasterRequestTest {

    private static Storage<String, String> storage;

    @BeforeAll
    public static void init() {
        storage = RepositoryManager.getInstance();
    }

    @Test
    public void testElementsReturnedForRPush() {
        String listKey = "myList";
        List<String> elements = Arrays.asList("one", "two", "three");
        storage.lPush(listKey, elements);

        LRangeMasterRequest request = new LRangeMasterRequest(listKey, 0, 2);
        Response response = request.doExecute();

        // Elements in reverse order
        List<String> expectedElements = Arrays.asList("three", "two", "one");

        String expectedResp = RespSerializer.asArray(expectedElements);
        String actualResp = new String(response.getResponse(), StandardCharsets.UTF_8);

        assertEquals(expectedResp, actualResp);
        storage.delete(List.of(listKey));
    }

    @Test
    public void testElementsReturnedInReverseForLPush() {
        String listKey = "myList";
        List<String> elements = Arrays.asList("one", "two", "three");
        storage.lPush(listKey, elements);

        LRangeMasterRequest request = new LRangeMasterRequest(listKey, 0, 2);
        Response response = request.doExecute();

        // Elements in reverse order
        List<String> expectedElements = Arrays.asList("three", "two", "one");

        String expectedResp = RespSerializer.asArray(expectedElements);
        String actualResp = new String(response.getResponse(), StandardCharsets.UTF_8);

        assertEquals(expectedResp, actualResp);
        storage.delete(List.of(listKey));
    }

    @Test
    public void testNegativeRange() {
        String listKey = "myListNegativeIndex";

        List<String> elements = Arrays.asList("one", "two", "three", "four", "five");
        storage.rPush(listKey, elements);

        LRangeMasterRequest request = new LRangeMasterRequest(listKey, 1, -2);
        Response response = request.doExecute();

        List<String> expectedElements = Arrays.asList("two", "three", "four");

        String expectedResp = RespSerializer.asArray(expectedElements);
        String actualResp = new String(response.getResponse(), StandardCharsets.UTF_8);

        assertEquals(expectedResp, actualResp);
        storage.delete(List.of(listKey));
    }

    @Test
    public void testNegativeRangeBothIndicesNegative() {
        String listKey = "myListNegativeIndices";

        List<String> elements = Arrays.asList("one", "two", "three", "four", "five");
        storage.rPush(listKey, elements);

        LRangeMasterRequest request = new LRangeMasterRequest(listKey, -5, -2);
        Response response = request.doExecute();

        List<String> expectedElements = Arrays.asList("one", "two", "three", "four");

        String expectedResp = RespSerializer.asArray(expectedElements);
        String actualResp = new String(response.getResponse(), StandardCharsets.UTF_8);

        assertEquals(expectedResp, actualResp);
        storage.delete(List.of(listKey));
    }

    @Test
    public void testNegativeRangeOutOfRange() {
        String listKey = "myListOutOfRange";

        List<String> elements = Arrays.asList("one", "two", "three", "four", "five");
        storage.rPush(listKey, elements);

        // StartIdx is considered as  0
        LRangeMasterRequest request = new LRangeMasterRequest(listKey, -6, 3);
        Response response = request.doExecute();

        List<String> expectedElements = Arrays.asList("one", "two", "three", "four");

        String expectedResp = RespSerializer.asArray(expectedElements);
        String actualResp = new String(response.getResponse(), StandardCharsets.UTF_8);

        assertEquals(expectedResp, actualResp);
        storage.delete(List.of(listKey));
    }

    @Test
    public void shouldReturnEmptyArrayForMissingList() {
        String listKey = "missingList";

        LRangeMasterRequest request = new LRangeMasterRequest(listKey, 0, 10);
        Response response = request.doExecute();

        String actualResp = new String(response.getResponse(), StandardCharsets.UTF_8);
        assertEquals(EMPTY_RESP_ARRAY, actualResp);
    }

    @Test
    public void shouldReturnEmptyArrayForOutOfRange() {
        String listKey = "outOfRangeList";
        storage.lPush(listKey, Arrays.asList("one", "two"));

        LRangeMasterRequest request = new LRangeMasterRequest(listKey, 10, 20);
        Response response = request.doExecute();

        String actualResp = new String(response.getResponse(), StandardCharsets.UTF_8);
        assertEquals(EMPTY_RESP_ARRAY, actualResp);
    }

    @Test
    public void shouldReturnEmptyArrayForStartGreaterThanEnd() {
        String listKey = "invalidRangeList";
        storage.lPush(listKey, Arrays.asList("one", "two", "three", "four"));

        LRangeMasterRequest request = new LRangeMasterRequest(listKey, 3, 1);
        Response response = request.doExecute();

        String actualResp = new String(response.getResponse(), StandardCharsets.UTF_8);
        assertEquals(EMPTY_RESP_ARRAY, actualResp);
    }
}
