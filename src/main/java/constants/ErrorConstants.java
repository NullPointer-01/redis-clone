package constants;

public class ErrorConstants {
    public static final String ERROR_UNKNOWN_COMMAND = "-ERR unknown command '%s', with args beginning with: %s\r\n";
    public static final String ERROR_NOT_AN_INTEGER = "-ERR value is not an integer or out of range\r\n";
    public static final String ERROR_EXEC_WITHOUT_MULTI = "-ERR EXEC without MULTI\r\n";
    public static final String ERROR_DISCARD_WITHOUT_MULTI = "-ERR DISCARD without MULTI\r\n";
    public static final String ERROR_INVALID_STREAM_ENTRY_ID = "-ERR The ID specified in XADD is equal or smaller than the target stream top item\r\n";
    public static final String ERROR_ZERO_STREAM_ENTRY_ID = "-ERR The ID specified in XADD must be greater than 0-0\r\n";
    public static final String ERROR_INVALID_COMMAND_SUBSCRIBED_MODE = "-ERR Can't execute '%s': only (P|S)SUBSCRIBE / (P|S)UNSUBSCRIBE / PING / QUIT / RESET are allowed in this context\r\n";

    // Private constructor to prevent instantiations
    private ErrorConstants() {}
}
