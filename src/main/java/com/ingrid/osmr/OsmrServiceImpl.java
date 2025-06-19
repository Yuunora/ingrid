package com.ingrid.osmr;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ingrid.App;

@Service
public class OsmrServiceImpl implements OsmrService {
    private static Logger logger = LogManager.getLogger(App.class);

    private String osmrUrl;

    public OsmrServiceImpl() {
        try {
            var properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
            osmrUrl = properties.getProperty("osmr.url");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

    }

    @Override
    public RouteInfo findRoute(String src, String dst) throws OsmrException {
        var client = new RestTemplate();
        final var url = createUrl(src, dst);
        try {
            ResponseEntity<String> resp = client.getForEntity(URI.create(url), String.class);

            if (resp.getStatusCode() != HttpStatus.OK) {
                throw new OsmrException(String.format("received {} for request {} : ", resp.getStatusCode(), url));
            }
            final var content = new JSONObject(resp.getBody());
            final var route = content.getJSONArray("routes").getJSONObject(0);
            return new RouteInfo(dst, route.getDouble("duration"),
                    route.getDouble("distance"));
        } catch (RestClientException | JSONException e) {
            throw new OsmrException(e.getMessage());
        }
    }

    private String createUrl(String src, String dst) {
        final var url = new StringBuilder(osmrUrl);
        url.append(src + ";" + dst);
        url.append("?overview=false");
        return url.toString();
    }
}
