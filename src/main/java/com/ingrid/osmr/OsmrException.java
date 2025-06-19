package com.ingrid.osmr;

public class OsmrException extends Throwable {

    private final String message;

    public OsmrException(String msg) {
        message = String.format("Error requesting data from OSMR : {}", msg);
    }

    public String getMessage() {
        return message;
    }
}
