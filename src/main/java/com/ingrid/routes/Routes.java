package com.ingrid.routes;

import java.util.List;

import com.ingrid.osmr.RouteInfo;

public class Routes {
    class Route {
        public final String destination;
        public final double duration;
        public final double distance;

        public Route(String dst, double dur, double dist) {
            destination = dst;
            duration = dur;
            distance = dist;
        }
    }

    public final String source;
    public final List<Route> routes;

    public Routes(String src, List<RouteInfo> infos) {
        source = src;
        routes = infos.stream().map(info -> new Route(info.destination, info.duration, info.distance)).toList();
    }
}
