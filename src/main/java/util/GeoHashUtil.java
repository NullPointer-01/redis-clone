package util;

import java.util.List;

import static constants.Constants.*;

public class GeoHashUtil {
    public static final double EARTH_RADIUS_IN_METERS = 6372797.560856; // In metres

    public static double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double lon1r = Math.toRadians(lon1);
        double lon2r = Math.toRadians(lon2);
        double v = Math.sin((lon2r - lon1r) / 2);

        double lat1r = Math.toRadians(lat1);
        double lat2r = Math.toRadians(lat2);
        double u = Math.sin((lat2r - lat1r) / 2);
        double a = u * u + Math.cos(lat1r) * Math.cos(lat2r) * v * v;
        return 2.0 * EARTH_RADIUS_IN_METERS * Math.asin(Math.sqrt(a));
    }

    public static double computeGeoHash(double latitude, double longitude) {
        int normalized_latitude = (int) (VAL_OF_2_POWER_26 * (latitude - MIN_LATITUDE) / LATITUDE_RANGE);
        int normalized_longitude = (int) (VAL_OF_2_POWER_26 * (longitude - MIN_LONGITUDE) / LONGITUDE_RANGE);

        return interleave(normalized_latitude, normalized_longitude);
    }

    public static List<Double> decodeGeohash(long score) {
        long x = score;
        long y = score >> 1;

        long gridLatitudeNumber = compactInt64ToInt32(x);
        long gridLongitudeNumber = compactInt64ToInt32(y);

        return convertToCoordinates(gridLatitudeNumber, gridLongitudeNumber);
    }

    private static double interleave(int normalizedLatitude, int normalizedLongitude) {
        long x = spreadInt32ToInt64(normalizedLatitude);
        long y = spreadInt32ToInt64(normalizedLongitude);

        y = y << 1;

        return x | y;
    }

    public static long spreadInt32ToInt64(int v) {
        // Ensure only lower 32 bits are non-zero.
        long val = v & 0xFFFFFFFFL;

        // Bitwise operations to spread 32 bits into 64 bits with zeros in-between
        val = (val | (val << 16)) & 0x0000FFFF0000FFFFL;
        val = (val | (val << 8))  & 0x00FF00FF00FF00FFL;
        val = (val | (val << 4))  & 0x0F0F0F0F0F0F0F0FL;
        val = (val | (val << 2))  & 0x3333333333333333L;
        val = (val | (val << 1))  & 0x5555555555555555L;

        return val;
    }

    private static long compactInt64ToInt32(long v) {
        // Keep only the bits in even positions
        long val = v & 0x5555555555555555L;

        // Reverse the spreading process by shifting and masking
        val = (val | (val >> 1)) & 0x3333333333333333L;
        val = (val | (val >> 2)) & 0x0F0F0F0F0F0F0F0FL;
        val = (val | (val >> 4)) & 0x00FF00FF00FF00FFL;
        val = (val | (val >> 8)) & 0x0000FFFF0000FFFFL;
        val = (val | (val >> 16)) & 0x00000000FFFFFFFFL;

        return val;
    }

    private static List<Double> convertToCoordinates(long gridLatitudeNumber, long gridLongitudeNumber) {
        double latitudeMin = MIN_LATITUDE + LATITUDE_RANGE * (gridLatitudeNumber / VAL_OF_2_POWER_26);
        double latitudeMax = MIN_LATITUDE + LATITUDE_RANGE * ((gridLatitudeNumber+1) / VAL_OF_2_POWER_26);
        double longitudeMin = MIN_LONGITUDE + LONGITUDE_RANGE * (gridLongitudeNumber / VAL_OF_2_POWER_26);
        double longitudeMax = MIN_LONGITUDE + LONGITUDE_RANGE * ((gridLongitudeNumber+1) / VAL_OF_2_POWER_26);

        double latitude = (latitudeMin + latitudeMax) / 2;
        double longitude = (longitudeMin + longitudeMax) / 2;

        return List.of(latitude, longitude);
    }
}
