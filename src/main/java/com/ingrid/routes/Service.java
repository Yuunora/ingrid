package com.ingrid.routes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ingrid.osmr.Client;
import com.ingrid.osmr.OsmrException;
import com.ingrid.osmr.RouteInfo;

@RestController
public class Service {

    @Autowired
    private final Client osmrClient;

    private static Logger logger = LogManager.getLogger(Service.class);

    public Service(Client osmrClient) {
        this.osmrClient = osmrClient;
    }

    @GetMapping("/routes")
    public Routes getRoutes(@RequestParam(name = "src") String src,
            @RequestParam(name = "dst") List<String> dists) {

        if (!isCoordinate(src)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parameter: src");
        }

        var routes = new ArrayList<RouteInfo>();
        for (String dist : dists) {
            if (!isCoordinate(dist)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parameter: dst");
            }
            try {
                var route = osmrClient.findRoute(src, dist);
                routes.add(route);
            } catch (OsmrException e) {
                logger.error(e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }

        // Sort the results by duration then distance
        Collections.sort(routes, Comparator.comparing((RouteInfo r) -> r.duration).thenComparing(r -> r.distance));
        return new Routes(src, routes);
    }

    private Boolean isCoordinate(String src) {
        try {
            var values = src.split(",");
            if (values.length != 2) {
                return false;
            }
            Float.parseFloat(values[0]);
            Float.parseFloat(values[1]);
        } catch (NumberFormatException e) {
            logger.error(String.format("Incorrect format for %s", src));
            return false;
        }

        return true;
    }
}
