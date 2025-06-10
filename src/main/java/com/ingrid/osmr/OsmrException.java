package com.ingrid.osmr;

public class OsmrException extends Throwable {

    private final String message;

    public OsmrException(String error) {
        message = String.format("Error requesting data from OSMR : %s", error);
    }

    public String getMessage() {
        return message;
    }
}
