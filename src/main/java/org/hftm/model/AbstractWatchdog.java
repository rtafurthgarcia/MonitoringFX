package org.hftm.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.util.List;
import org.hftm.model.HistoryRecord;

public abstract class AbstractWatchdog {

    protected IntegerProperty id;
    protected StringProperty service;
    protected ObjectProperty<HistoryRecord.ServiceStatus> currentStatus; 
    protected BooleanProperty running;
    protected IntegerProperty timeout;
    protected IntegerProperty heartbeat;
    protected IntegerProperty retries;
    protected FloatProperty uptime20h;
    protected FloatProperty uptime30d; 
    private LocalDateTime creationDateTime;
    private List<HistoryRecord> monitoringHistory;
    
    public Integer getId() {
        return this.id.get();
    }

    public void setId(Integer newId) {
        id.set(newId);
    }

    public String getService() {
        return service.get();
    }

    public void setService(String newService) {
        service.set(newService);
    }

    public HistoryRecord.ServiceStatus getCurrentStatus() {
        return currentStatus.get();
    }

    public void setCurrentStatus(HistoryRecord.ServiceStatus newCurrentStatus) {
        currentStatus.set(newCurrentStatus);

        HistoryRecord newRecord = new HistoryRecord(LocalDateTime.now(), newCurrentStatus); 

        monitoringHistory.add(newRecord);
    }

    public IntegerProperty getIdProperty() {
        return id;
    }
    public StringProperty getServiceProperty() {
        return service;
    }
    
    public ObjectProperty<HistoryRecord.ServiceStatus> getCurrentStatusProperty() {
        return currentStatus;
    }
    
    public BooleanProperty getRunningProperty() {
        return running;
    }

    public IntegerProperty getTimeoutProperty() {
        return timeout;
    }

    public IntegerProperty getHeartbeatProperty() {
        return heartbeat;
    }

    public IntegerProperty getRetriesProperty() {
        return retries;
    }

    public FloatProperty getUptime20hProperty() {
        return uptime20h;
    }


    public FloatProperty getUptime30dProperty() {
        return uptime30d;
    }

    public LocalDateTime getCreationDateTimeProperty() {
        return creationDateTime;
    }

    public List<HistoryRecord> getMonitoringHistoryProperty() {
        return monitoringHistory;
    }

    public AbstractWatchdog(IntegerProperty id, StringProperty service, IntegerProperty timeout, IntegerProperty heartbeat, IntegerProperty retries) {
        this.id = id;
        this.service = service;
        this.timeout = timeout;
        this.heartbeat = heartbeat;
        this.retries = retries;

        creationDateTime = LocalDateTime.now();
        running.set(true);
        currentStatus.set(HistoryRecord.ServiceStatus.CHECKING);
    } 

    public abstract void checkServiceAvailability();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((heartbeat == null) ? 0 : heartbeat.hashCode());
        result = prime * result + ((retries == null) ? 0 : retries.hashCode());
        result = prime * result + ((running == null) ? 0 : running.hashCode());
        result = prime * result + ((service == null) ? 0 : service.hashCode());
        result = prime * result + ((timeout == null) ? 0 : timeout.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractWatchdog other = (AbstractWatchdog) obj;
        if (heartbeat == null) {
            if (other.heartbeat != null)
                return false;
        } else if (!heartbeat.equals(other.heartbeat))
            return false;
        if (retries == null) {
            if (other.retries != null)
                return false;
        } else if (!retries.equals(other.retries))
            return false;
        if (running == null) {
            if (other.running != null)
                return false;
        } else if (!running.equals(other.running))
            return false;
        if (service == null) {
            if (other.service != null)
                return false;
        } else if (!service.equals(other.service))
            return false;
        if (timeout == null) {
            if (other.timeout != null)
                return false;
        } else if (!timeout.equals(other.timeout))
            return false;
        return true;
    }
}
