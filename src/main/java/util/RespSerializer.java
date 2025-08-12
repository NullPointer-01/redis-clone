package util;

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
}
