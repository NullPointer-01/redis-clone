package util;

import ds.Entry;
import ds.Pair;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static util.RespConstants.*;

public class RespSerializer {
    // Private constructor to prevent instantiations
    private RespSerializer() {}

    public static String asBulkString(String data) {
        if (data == null || data.isEmpty()) {
            return EMPTY_BULK_STRING;
        }

        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        int length = dataBytes.length; // Get bytes length in UTF-8

        return String.valueOf(DOLLAR) + length + CRLF + data + CRLF;
    }

    public static String asInteger(Integer value) {
        String sign = value < 0 ? "-" : "";
        return COLON + sign + value + CRLF;
    }

    public static String asArray(List<String> elements) {
        StringBuilder sb = new StringBuilder();
        sb.append(ASTERISK).append(elements.size()).append(CRLF);

        for (String ele : elements) {
            sb.append(asBulkString(ele));
        }
        return sb.toString();
    }

    public static String asArrayOfArrays(List<Entry<String, String>> entries) {
        int innerArraySize = 2;

        StringBuilder sb = new StringBuilder();
        sb.append(ASTERISK).append(entries.size()).append(CRLF);

        for (Entry<String, String> entry : entries) {
            sb.append(ASTERISK).append(innerArraySize).append(CRLF);
            sb.append(asBulkString(entry.getEntryId()));

            List<Pair<String, String>> keysAndValues = entry.getKeysAndValues();
            List<String> flatList = new ArrayList<>(keysAndValues.size() * 2);

            for (Pair<String, String> pair : keysAndValues) {
                flatList.add(pair.getKey());
                flatList.add(pair.getValue());
            }
            sb.append(asArray(flatList));
        }

        return sb.toString();
    }
}
