package util;

import requests.Request;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static requests.model.Command.ECHO;
import static requests.model.Command.PING;
import static org.junit.jupiter.api.Assertions.*;

class RequestParserTest {
    @Test
    void testSingleRequest() {
        String input = "*1\r\n$4\r\nPING\r\n";

        try (InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))){
            List<Request> requests = RequestParser.parseRequests(is);

            assertNotNull(requests);
            assertEquals(1, requests.size());

            Request request = requests.get(0);
            assertEquals(PING, request.getCommand());
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    void testMultipleRequests() {
        String input = "*1\r\n$4\r\nPING\r\n*2\r\n$4\r\nECHO\r\n$3\r\nhey\r\n";

        try (InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))){
            List<Request> requests = RequestParser.parseRequests(is);

            assertNotNull(requests);
            assertEquals(2, requests.size());

            assertEquals(PING, requests.get(0).getCommand());
            assertEquals(ECHO, requests.get(1).getCommand());
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    void testEmptyInput() {
        try (InputStream is = new ByteArrayInputStream(new byte[0])){
            List<Request> requests = RequestParser.parseRequests(is);
            assertTrue(requests.isEmpty());
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void testInvalidInput() {
        String invalidInput = "*1\r\n$3\r\nHAHAHA"; // Invalid bulk string

        try (InputStream is = new ByteArrayInputStream(invalidInput.getBytes(StandardCharsets.UTF_8))) {
            assertThrows(IOException.class, () -> {
                RequestParser.parseRequests(is);
            });
        } catch (IOException e) {
            fail();
        }
    }
}