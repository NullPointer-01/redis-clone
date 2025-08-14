package util;

import java.util.List;

import static util.RespConstants.*;

public class RespSerializer {
    // Private constructor to prevent instantiations
    private RespSerializer() {}

    public static String asBulkString(String data) {
        int length = data.length();
        if (length == 0) return EMPTY_BULK_STRING;

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
}
