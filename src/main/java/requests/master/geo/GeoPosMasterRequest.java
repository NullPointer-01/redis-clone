package requests.master.geo;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.util.ArrayList;
import java.util.List;

import static constants.Constants.*;

public class GeoPosMasterRequest extends AbstractRequest  {
    private final String geoKey;
    private final List<String> members;

    public GeoPosMasterRequest(String geoKey, List<String> members) {
        super(Command.GEOPOS);
        this.geoKey = geoKey;
        this.members = members;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        List<List<String>> coordinates = new ArrayList<>(members.size());

        for (String member : members) {
            Double score = storage.zScore(geoKey, member);
            coordinates.add(decodeGeohash(score.longValue()));
        }

        return new Response(RespSerializer.asArrayOfArrays(coordinates));
    }

    private List<String> decodeGeohash(long score) {
        long x = score;
        long y = score >> 1;

        long gridLatitudeNumber = compactInt64ToInt32(x);
        long gridLongitudeNumber = compactInt64ToInt32(y);

        return convertToCoordinates(gridLatitudeNumber, gridLongitudeNumber);
    }

    public static long compactInt64ToInt32(long v) {
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

    private List<String> convertToCoordinates(long gridLatitudeNumber, long gridLongitudeNumber) {
        double latitudeMin = MIN_LATITUDE + LATITUDE_RANGE * (gridLatitudeNumber / VAL_OF_2_POWER_26);
        double latitudeMax = MIN_LATITUDE + LATITUDE_RANGE * ((gridLatitudeNumber+1) / VAL_OF_2_POWER_26);
        double longitudeMin = MIN_LONGITUDE + LONGITUDE_RANGE * (gridLongitudeNumber / VAL_OF_2_POWER_26);
        double longitudeMax = MIN_LONGITUDE + LONGITUDE_RANGE * ((gridLongitudeNumber+1) / VAL_OF_2_POWER_26);

        double latitude = (latitudeMin + latitudeMax) / 2;
        double longitude = (longitudeMin + longitudeMax) / 2;

        return List.of(String.valueOf(latitude), String.valueOf(longitude));
    }
}
