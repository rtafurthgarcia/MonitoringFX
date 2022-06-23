package org.hftm.model;

import java.time.LocalDateTime;

public class HistoryRecord {
    public enum ServiceStatus {
        UP, 
        DOWN,
        PAUSED,
        UNKNOWN
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
