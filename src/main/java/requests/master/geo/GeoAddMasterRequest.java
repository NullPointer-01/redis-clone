package requests.master.geo;

import ds.Location;
import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.RespSerializer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static constants.Constants.*;
import static constants.ErrorConstants.ERROR_INVALID_COORDINATES;

public class GeoAddMasterRequest extends AbstractRequest {
    private Location invalidLocation;

    private final String geoKey;
    private final Map<String, Location> locations;

    public GeoAddMasterRequest(String geoKey, List<String> args) {
        super(Command.GEOADD);
        this.geoKey = geoKey;
        this.locations = getLocations(args);
    }

    private Map<String, Location> getLocations(List<String> args) {
        Map<String, Location> locations = new LinkedHashMap<>();

        for (int i = 0; i < args.size(); i+=3) {
            double latitude = Double.parseDouble(args.get(i));
            double longitude = Double.parseDouble(args.get(i + 1));
            String name = args.get(i+2);

            if (!validateCoordinates(latitude, longitude)) {
                invalidLocation = new Location(name, latitude, longitude);
                locations.clear();
                break;
            }

            Location loc = new Location(name, latitude, longitude);
            locations.put(geoKey, loc);
        }

        return locations;
    }

    @Override
    public Response doExecute() {
        if (invalidLocation != null) {
            return new Response(String.format(ERROR_INVALID_COORDINATES, invalidLocation.getLatitude(), invalidLocation.getLongitude()));
        }

        Storage<String, String> storage = RepositoryManager.getInstance();
        int count = 0;

        for (Map.Entry<String, Location> entry : locations.entrySet()) {
            Location loc = entry.getValue();
            double score = computeGeoHash(loc.getLatitude(), loc.getLongitude());

            if (storage.zAdd(entry.getKey(), loc.getName(), score)) {
                count++;
            }
        }

        return new Response(RespSerializer.asInteger(count));
    }

    private boolean validateCoordinates(double latitude, double longitude) {
        return (latitude >= MIN_LATITUDE && latitude <= MAX_LATITUDE)
                && (longitude >= MIN_LONGITUDE && longitude <= MAX_LONGITUDE);
    }

    private double computeGeoHash(double latitude, double longitude) {
        int normalized_latitude = (int) (VAL_OF_2_POWER_26 * (latitude - MIN_LATITUDE) / LATITUDE_RANGE);
        int normalized_longitude = (int) (VAL_OF_2_POWER_26 * (longitude - MIN_LONGITUDE) / LONGITUDE_RANGE);

        return interleave(normalized_latitude, normalized_longitude);
    }

    private double interleave(int normalizedLatitude, int normalizedLongitude) {
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
}
