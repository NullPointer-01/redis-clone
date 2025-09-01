package requests.master.geo;

import repository.RepositoryManager;
import repository.Storage;
import requests.AbstractRequest;
import requests.model.Command;
import requests.model.Response;
import util.GeoHashUtil;
import util.RespSerializer;

import java.util.ArrayList;
import java.util.List;

public class GeoPosMasterRequest extends AbstractRequest {
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
        List<List<String>> coordinatesList = new ArrayList<>(members.size());

        for (String member : members) {
            Double score = storage.zScore(geoKey, member);
            List<Double> coordinates = GeoHashUtil.decodeGeohash(score.longValue());

            coordinatesList.add(List.of(String.valueOf(coordinates.get(0)), String.valueOf(coordinates.get(1))));
        }

        return new Response(RespSerializer.asArrayOfArrays(coordinatesList));
    }
}
