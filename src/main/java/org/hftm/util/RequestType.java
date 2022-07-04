package org.hftm.util;

import java.util.HashMap;
import java.util.Map;

public enum RequestType {
    GET("GET"),
    PUT("PUT"),
    POST("POST"),
    DELETE("DELETE"),
    CONNECT("CONNECT"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS");

    public final String label;

    private RequestType(String label) {
        this.label = label;
    }

    private static final Map<String, RequestType> BY_LABEL = new HashMap<>();

    static {
        for (RequestType e: values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    public static RequestType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override 
    public String toString() { 
        return this.label; 
    }
}