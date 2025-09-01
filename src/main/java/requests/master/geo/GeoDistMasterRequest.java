package requests.master.geo;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.GeoHashUtil;
import util.RespSerializer;

import java.util.List;

import static util.RespConstants.NULL_BULK_STRING;

public class GeoDistMasterRequest extends AbstractRequest {
    private final String geoKey;
    private final String member1;
    private final String member2;

    public GeoDistMasterRequest(String geoKey, String member1, String member2) {
        super(Command.GEODIST);
        this.geoKey = geoKey;
        this.member1 = member1;
        this.member2 = member2;
    }

    @Override
    public Response doExecute() {
        Storage<String, String> storage = RepositoryManager.getInstance();
        if (storage.zCard(geoKey) == 0) {
            return new Response(NULL_BULK_STRING);
        }

        Double score1 = storage.zScore(geoKey, member1);
        Double score2 = storage.zScore(geoKey, member2);

        if (score1 == null || score2 == null) {
            return new Response(NULL_BULK_STRING);
        }

        List<Double> coordinates1 = GeoHashUtil.decodeGeohash(score1.longValue());
        List<Double> coordinates2 = GeoHashUtil.decodeGeohash(score2.longValue());

        double haversineDistance = GeoHashUtil.calculateHaversineDistance(
                coordinates1.get(0),
                coordinates1.get(1),
                coordinates2.get(0),
                coordinates2.get(1));

        return new Response(RespSerializer.asBulkString(String.valueOf(haversineDistance)));
    }
}
