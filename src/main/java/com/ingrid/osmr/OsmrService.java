package com.ingrid.osmr;

public interface OsmrService {
    RouteInfo findRoute(String src, String dst) throws OsmrException;
}
