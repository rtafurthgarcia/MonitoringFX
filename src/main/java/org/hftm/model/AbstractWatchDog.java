package org.hftm.model;

import javafx.beans.property.*;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import org.hftm.model.HistoryRecord.ServiceStatus;

public abstract class AbstractWatchDog extends ScheduledService {

    protected static final Integer DEFAULT_TIMEOUT = 2000;
    protected static final Integer DEFAULT_MAX_FAILURE = 3;
    protected static final Duration DEFAULT_PERIOD = new Duration(3000);

    private IntegerProperty id;
    private StringProperty service;
    private ObjectProperty<HistoryRecord.ServiceStatus> currentStatus;
    //private BooleanProperty running;
    private IntegerProperty timeout;
    //private IntegerProperty period;
    //private IntegerProperty maxFailures;
    private FloatProperty uptime20h;
    private FloatProperty uptime30d; 
    private LocalDateTime creationDateTime;
    private LinkedList<HistoryRecord> monitoringHistory;
    
    protected StringProperty type;

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

        setCurrentStatus(ServiceStatus.UNKNOWN);
    }

    public HistoryRecord.ServiceStatus getCurrentStatus() {
        return currentStatus.get();
    }

    public void setCurrentStatus(HistoryRecord.ServiceStatus newCurrentStatus) {
        currentStatus.set(newCurrentStatus);

        HistoryRecord newRecord = new HistoryRecord(LocalDateTime.now(), newCurrentStatus); 

        monitoringHistory.add(newRecord);
    }

    /*public Boolean getRunning() {
        return running.get();
    }

    public void setRunning(boolean newValue) {
        running.set(newValue);
    }*/

    public Integer getTimeout() {
        return timeout.get();
    }

    public void setTimeout(Integer newValue) {
        timeout.set(newValue);
    }

    /*public Integer getperiod() {
        return period.get();
    }

    public void setHeartbear(Integer newValue) {
        period.set(newValue);
    }

    public Integer getRetries() {
        return maxFailures.get();
    }

    public void setRetries(Integer newValue) {
        maxFailures.set(newValue);
    }*/

    public Float getUptime20h() {
        return uptime20h.get();
    }

    public Float getUptime30d() {
        return uptime30d.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }
    public StringProperty serviceProperty() {
        return service;
    }
    
    public ObjectProperty<HistoryRecord.ServiceStatus> currentStatusProperty() {
        return currentStatus;
    }
    
    /*public BooleanProperty runningProperty() {
        return running;
    }*/

    public IntegerProperty timeoutProperty() {
        return timeout;
    }

    /*public IntegerProperty periodProperty() {
        return period;
    }

    public IntegerProperty retriesProperty() {
        return maxFailures;
    }*/

    public FloatProperty uptime20hProperty() {
        return uptime20h;
    }

    // force all child classes to define their own type so that it can be shown in the tableviews
    protected abstract void setTypeProperty();

    public StringProperty typeProperty() {
        return type;
    }

    public FloatProperty uptime30dProperty() {
        return uptime30d;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public List<HistoryRecord> getMonitoringHistory() {
        return monitoringHistory;
    }

    protected AbstractWatchDog(Integer id, String service, Integer timeout, Duration period, Integer maxFailures) {
        super();
        super.setPeriod(period);
        super.setMaximumFailureCount(maxFailures);

        this.id = new SimpleIntegerProperty(id);
        this.service = new SimpleStringProperty(service);
        this.timeout = new SimpleIntegerProperty(timeout);
        this.monitoringHistory = new LinkedList<HistoryRecord>(); 

        creationDateTime = LocalDateTime.now();
        this.currentStatus = new SimpleObjectProperty<>();

        this.setCurrentStatus(ServiceStatus.UNKNOWN);
    } 

    protected AbstractWatchDog(Integer id, String service) {
        this(id, service, DEFAULT_TIMEOUT, DEFAULT_PERIOD, DEFAULT_MAX_FAILURE);
    }

    public abstract void checkServiceAvailability() throws Exception;

    @Override
    public String toString() {
        return getService();
    }

    @Override
    public Task createTask() {
        return new Task<Void>() {
            protected Void call() throws Exception {
                checkServiceAvailability();
                
                return null;
            }
        };
    }
}
