package com.ingrid.routes;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ingrid.osmr.OsmrException;
import com.ingrid.osmr.OsmrService;
import com.ingrid.osmr.RouteInfo;

@RestController
public class Service {

    @Autowired
    private final OsmrService osmrService;

    private static Logger logger = LogManager.getLogger(Service.class);

    public Service(OsmrService osmrService) {
        this.osmrService = osmrService;
    }

    @GetMapping("/routes")
    public Routes getRoutes(@RequestParam(name = "src") String src,
            @RequestParam(name = "dst") Set<String> dists) {

        if (!isCoordinate(src)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parameter: src");
        }

        var routes = new TreeSet<RouteInfo>(
                Comparator.comparing((RouteInfo r) -> r.duration).thenComparing(r -> r.distance));
        for (String dist : dists) {
            if (!isCoordinate(dist)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid parameter: dst");
            }
            try {
                final var route = osmrService.findRoute(src, dist);
                routes.add(route);
            } catch (OsmrException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }

        return new Routes(src, routes.stream().toList());
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
            logger.error("Incorrect format for {}", src);
            return false;
        }

        return true;
    }
}
