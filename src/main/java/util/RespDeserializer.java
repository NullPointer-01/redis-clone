package util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static util.RespConstants.CR;
import static util.RespConstants.LF;

public class RespDeserializer {
    // Private constructor to prevent instantiations
    private RespDeserializer() {}

    public static Object parse(InputStream is) throws IOException {
       char c = (char) is.read();

       switch (c) {
           case '*' -> {
               return parseArray(is);
           }
           case '$' -> {
               return parseBulkString(is);
           }
           default -> throw new IOException("Invalid byte " + c);
       }
    }

    public static List<Object> parseArray(InputStream is) throws IOException {
        List<Object> items = new ArrayList<>();

        int len = readLength(is);
        for (int i = 0; i < len; i++) {
            items.add(parse(is));
        }

        return items;
    }

    public static String parseBulkString(InputStream is) throws IOException {
        int len = readLength(is);
        byte[] data = is.readNBytes(len);

        validateCRLF(is);

        return new String(data, StandardCharsets.UTF_8);
    }

    private static int readLength(InputStream is) throws IOException {
        return Integer.parseInt(readLine(is));
    }

    private static String readLine(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        int data;

        while ((data = is.read()) != CR) {
            sb.append((char) data);
        }
        validateLF(is);

        return sb.toString();
    }

    private static void validateCRLF(InputStream is) throws IOException {
        validateCR(is);
        validateLF(is);
    }

    private static void validateCR(InputStream is) throws IOException {
        char c = (char) is.read();
        if (c != CR) {
            throw new IOException("Expected Carriage return missing");
        }
    }

    private static void validateLF(InputStream is) throws IOException {
        char c = (char) is.read();
        if (c != LF) {
            throw new IOException("Expected Line feed missing");
        }
    }
}
