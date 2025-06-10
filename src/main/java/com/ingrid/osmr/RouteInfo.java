package com.ingrid.osmr;

public class RouteInfo {
    public final String destination;
    public final double duration;
    public final double distance;

    public RouteInfo(String destination, double duration, double distance) {
        this.destination = destination;
        this.duration = duration;
        this.distance = distance;
    }
}
