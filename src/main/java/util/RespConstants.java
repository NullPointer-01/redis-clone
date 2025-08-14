package util;

public class RespConstants {
    public static final Character ASTERISK = '*';
    public static final Character DOLLAR = '$';
    public static final Character COLON = ':';

    public static final Character CR = '\r';
    public static final Character LF = '\n';
    public static final Character NULL = '\u0000';

    public static final String CRLF = "\r\n";
    public static final String PONG_SIMPLE_STRING = "+PONG\r\n";
    public static final String OK_SIMPLE_STRING = "+OK\r\n";

    public static final String EMPTY_BULK_STRING = "$0\r\n\r\n";
    public static final String NULL_BULK_STRING = "$-1\r\n";

    public static final String EMPTY_RESP_ARRAY = "*0\r\n";

    public static final String PX = "PX";

    // Private constructor to prevent instantiations
    private RespConstants() {}
}
