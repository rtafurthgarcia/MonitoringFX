package org.hftm.model;

import javafx.beans.property.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import org.hftm.model.HistoryRecord.ServiceStatus;

public abstract class AbstractWatchdog {

    private IntegerProperty id;
    private StringProperty service;
    private ObjectProperty<HistoryRecord.ServiceStatus> currentStatus;
    private BooleanProperty running;
    private IntegerProperty timeout;
    private IntegerProperty heartbeat;
    private IntegerProperty retries;
    private FloatProperty uptime20h;
    private FloatProperty uptime30d; 
    private LocalDateTime creationDateTime;
    private LinkedList<HistoryRecord> monitoringHistory;
    
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

        setCurrentStatus(ServiceStatus.CHECKING);
    }

    public HistoryRecord.ServiceStatus getCurrentStatus() {
        return currentStatus.get();
    }

    public void setCurrentStatus(HistoryRecord.ServiceStatus newCurrentStatus) {
        currentStatus.set(newCurrentStatus);

        HistoryRecord newRecord = new HistoryRecord(LocalDateTime.now(), newCurrentStatus); 

        monitoringHistory.add(newRecord);
    }

    public Boolean getRunning() {
        return running.get();
    }

    public void setRunning(boolean newValue) {
        running.set(newValue);
    }

    public Integer getTimeout() {
        return timeout.get();
    }

    public void setTimeout(Integer newValue) {
        timeout.set(newValue);
    }

    public Integer getHeartbeat() {
        return heartbeat.get();
    }

    public void setHeartbear(Integer newValue) {
        heartbeat.set(newValue);
    }

    public Integer getRetries() {
        return retries.get();
    }

    public void setRetries(Integer newValue) {
        retries.set(newValue);
    }

    public Float getUptime20h() {
        return uptime20h.get();
    }

    public Float getUptime30d() {
        return uptime30d.get();
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

    public List<HistoryRecord> getMonitoringHistory() {
        return monitoringHistory;
    }

    protected AbstractWatchdog(Integer id, String service, Integer timeout, Integer heartbeat, Integer retries) {
        this.id = new SimpleIntegerProperty(id);
        this.service = new SimpleStringProperty(service);
        this.timeout = new SimpleIntegerProperty(timeout);
        this.heartbeat = new SimpleIntegerProperty(heartbeat);
        this.retries = new SimpleIntegerProperty(retries);
        this.monitoringHistory = new LinkedList<HistoryRecord>(); 

        creationDateTime = LocalDateTime.now();
        this.running = new SimpleBooleanProperty(true);
        this.currentStatus = new SimpleObjectProperty<>();

        this.setCurrentStatus(ServiceStatus.CHECKING);
    } 

    public abstract void checkServiceAvailability() throws Exception;

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
