package util;

public class RespConstants {
    public static final Character APOSTROPHE = '\'';
    public static final Character ASTERISK = '*';
    public static final Character DOLLAR = '$';
    public static final Character COLON = ':';

    public static final Character CR = '\r';
    public static final Character LF = '\n';

    public static final String CRLF = "\r\n";
    public static final String PONG_SIMPLE_STRING = "+PONG\r\n";
    public static final String OK_SIMPLE_STRING = "+OK\r\n";
    public static final String QUEUED_SIMPLE_STRING= "+QUEUED\r\n";
    public static final String FULL_RESYNC= "+FULLRESYNC";

    public static final String TYPE_NONE_SIMPLE_STRING= "+none\r\n";
    public static final String TYPE_STRING_SIMPLE_STRING= "+string\r\n";
    public static final String TYPE_STREAM_SIMPLE_STRING= "+stream\r\n";

    public static final String EMPTY_BULK_STRING = "$0\r\n\r\n";
    public static final String NULL_BULK_STRING = "$-1\r\n";

    public static final String EMPTY_RESP_ARRAY = "*0\r\n";

    public static final String ERROR_UNKNOWN_COMMAND = "-ERR unknown command %s, with args beginning with: %s\r\n";
    public static final String ERROR_NOT_AN_INTEGER = "-ERR value is not an integer or out of range\r\n";
    public static final String ERROR_EXEC_WITHOUT_MULTI = "-ERR EXEC without MULTI\r\n";
    public static final String ERROR_DISCARD_WITHOUT_MULTI = "-ERR DISCARD without MULTI\r\n";

    // Private constructor to prevent instantiations
    private RespConstants() {}
}
