package com.ingrid.osmr;

import java.util.Objects;

public class RouteInfo {
    public final String destination;
    public final double duration;
    public final double distance;

    public RouteInfo(String destination, double duration, double distance) {
        this.destination = destination;
        this.duration = duration;
        this.distance = distance;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(destination) +
                Objects.hashCode(duration) << 4 +
                        Objects.hashCode(distance) << 8;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof RouteInfo)) {
            return false;
        }
        var other = (RouteInfo) o;
        return Objects.equals(destination, other.destination) &&
                distance == other.distance && duration == other.distance;
    }
}
