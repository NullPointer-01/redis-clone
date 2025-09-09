package constants;

public class Constants {
    public static final String HYPHEN = "-";
    public static final String ASTERISK = "*";
    public static final String PLUS = "+";

    public static final String NULL_STRING = "null";

    public static final String ZERO = "0";
    public static final String MAX_LONG_VALUE = String.valueOf(Long.MAX_VALUE);
    public static final String ZERO_STREAM_ENTRY_ID = "0-0";

    public static final String PONG = "PONG";
    public static final String SUBSCRIBE = "subscribe";
    public static final String UNSUBSCRIBE = "unsubscribe";
    public static final String MESSAGE = "message";

    public static final Double MIN_LATITUDE = -85.05112878;
    public static final Double MAX_LATITUDE = 85.05112878;
    public static final Double MIN_LONGITUDE = -180.0;
    public static final Double MAX_LONGITUDE = 180.0;

    public static final Double LATITUDE_RANGE = MAX_LATITUDE - MIN_LATITUDE;
    public static final Double LONGITUDE_RANGE = MAX_LONGITUDE - MIN_LONGITUDE;

    public static final Double VAL_OF_2_POWER_26 = Math.pow(2, 26);
}
