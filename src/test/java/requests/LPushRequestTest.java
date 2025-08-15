package requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.RepositoryManager;
import repository.Storage;
import requests.model.Response;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class LPushRequestTest {
    private Storage<String, String> storage;

    @BeforeEach
    public void init() {
        storage = RepositoryManager.getInstance();;
    }

    @Test
    public void checkLengthAfterLPush() {
        String listKey = "myListLPush";
        List<String> elements1 = Arrays.asList("one", "two");
        List<String> elements2 = List.of("three");

        LPushRequest request1 = new LPushRequest(listKey, elements1);
        Response response1 = request1.execute();

        assertArrayEquals(":2\r\n".getBytes(), response1.getResponse());

        LPushRequest request2 = new LPushRequest(listKey, elements2);
        Response response2 = request2.execute();

        assertArrayEquals(":3\r\n".getBytes(), response2.getResponse());

        storage.delete(List.of(listKey)); // Cleanup
    }
}
