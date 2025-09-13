package util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static util.RespConstants.CR;
import static util.RespConstants.LF;

public class RespDeserializer {
    // Private constructor to prevent instantiations
    private RespDeserializer() {}

    public static Object parse(ByteBuffer buffer) throws IOException {
       char c = (char) buffer.get();

       switch (c) {
           case '*' -> {
               return parseArray(buffer);
           }
           case '$' -> {
               return parseBulkString(buffer);
           }
           default -> throw new IOException("Invalid byte " + c);
       }
    }

    public static List<Object> parseArray(ByteBuffer buffer) throws IOException {
        List<Object> items = new ArrayList<>();

        int len = readLength(buffer);
        for (int i = 0; i < len; i++) {
            items.add(parse(buffer));
        }

        return items;
    }

    public static String parseBulkString(ByteBuffer buffer) throws IOException {
        int len = readLength(buffer);
        byte[] data = new byte[len];
        buffer.get(data);

        validateCRLF(buffer);

        return new String(data, StandardCharsets.UTF_8);
    }

    private static int readLength(ByteBuffer buffer) throws IOException {
        return Integer.parseInt(readLine(buffer));
    }

    private static String readLine(ByteBuffer buffer) throws IOException {
        StringBuilder sb = new StringBuilder();
        int data;

        while ((data = buffer.get()) != CR) {
            sb.append((char) data);
        }
        validateLF(buffer);

        return sb.toString();
    }

    private static void validateCRLF(ByteBuffer buffer) throws IOException {
        validateCR(buffer);
        validateLF(buffer);
    }

    private static void validateCR(ByteBuffer buffer) throws IOException {
        char c = (char) buffer.get();
        if (c != CR) {
            throw new IOException("Expected Carriage return missing");
        }
    }

    private static void validateLF(ByteBuffer buffer) throws IOException {
        char c = (char) buffer.get();
        if (c != LF) {
            throw new IOException("Expected Line feed missing");
        }
    }
}
