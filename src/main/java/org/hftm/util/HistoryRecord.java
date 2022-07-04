package org.hftm.util;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class HistoryRecord {
    public enum ServiceStatus {
        UP("Up"), 
        DOWN("Down"),
        PAUSED("Paused"),
        UNKNOWN("Unknown"),
        FAILED("Failed");

        public final String label;

        private ServiceStatus(String label) {
            this.label = label;
        }

        private static final Map<String, ServiceStatus> BY_LABEL = new HashMap<>();

        static {
            for (ServiceStatus e: values()) {
                BY_LABEL.put(e.label, e);
            }
        }

        public static ServiceStatus valueOfLabel(String label) {
            return BY_LABEL.get(label);
        }

        @Override 
        public String toString() { 
            return this.label; 
        }
    }

    private LocalDateTime timestamp;
    private ServiceStatus status;
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    public HistoryRecord(LocalDateTime timestamp, ServiceStatus status) {
        this.timestamp = timestamp;
        this.status = status;
    }
}
