package org.hftm.util;

import java.util.HashMap;
import java.util.Map;

public enum DurationType {
    MILISECONDS("Miliseconds"), 
    SECONDS("Seconds"),
    MINUTES("Minutes"),
    HOURS("Hours");

    public final String label;

    private DurationType(String label) {
        this.label = label;
    }

    private static final Map<String, DurationType> BY_LABEL = new HashMap<>();

    static {
        for (DurationType e: values()) {
            BY_LABEL.put(e.label, e);
        }
    }

    public static DurationType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }

    @Override 
    public String toString() { 
        return this.label; 
    }
}