package requests.master;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.RepositoryManager;
import repository.Storage;
import requests.master.lists.LPushMasterRequest;
import requests.master.lists.RPushMasterRequest;
import requests.model.Response;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class RPushMasterRequestTest {
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

        RPushMasterRequest request1 = new RPushMasterRequest(listKey, elements1);
        Response response1 = request1.doExecute();

        assertArrayEquals(":2\r\n".getBytes(), response1.getResponse());

        LPushMasterRequest request2 = new LPushMasterRequest(listKey, elements2);
        Response response2 = request2.doExecute();

        assertArrayEquals(":3\r\n".getBytes(), response2.getResponse());

        storage.delete(List.of(listKey)); // Cleanup
    }
}
