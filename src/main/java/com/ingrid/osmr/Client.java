package com.ingrid.osmr;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class Client {

    private final HttpClient client;

    public Client() {
        client = HttpClient.newHttpClient();
    }

    public RouteInfo findRoute(String src, String dst) throws OsmrException {
        var url = createUrl(src, dst);
        var request = HttpRequest.newBuilder(URI.create(url)).build();
        try {
            var response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() != HttpStatus.OK.value()) {
                throw new OsmrException(String.format("received %d for request %s : ", response.statusCode(), url));
            }
            var content = new JSONObject(response.body());
            var route = content.getJSONArray("routes").getJSONObject(0);
            return new RouteInfo(dst, route.getDouble("duration"), route.getDouble("distance"));
        } catch (IOException e) {
            throw new OsmrException(e.getMessage());
        } catch (InterruptedException e) {
            throw new OsmrException(e.getMessage());
        } catch (JSONException e) {
            throw new OsmrException(e.getMessage());
        }
    }

    private String createUrl(String src, String dst) {
        var url = new StringBuilder("https://router.project-osrm.org/route/v1/driving/");
        url.append(src + ";" + dst);
        url.append("?overview=false");
        return url.toString();
    }
}
