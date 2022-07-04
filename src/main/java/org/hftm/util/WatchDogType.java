package org.hftm.util;

import java.util.HashMap;
import java.util.Map;

public enum WatchDogType {
    DNS("DNS Record"),
    HTTP("HTTP(s)"),
    PING("Ping"),
    TCP("TCP Socket");

    public final String label;

    private WatchDogType(String label) {
        this.label = label;
    }

    private static final Map<String, WatchDogType> BY_LABEL = new HashMap<>();

    static {
        for (WatchDogType e: values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    public static WatchDogType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override 
    public String toString() { 
        return this.label; 
    }
}