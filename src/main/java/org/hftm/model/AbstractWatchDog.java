package org.hftm.model;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.hftm.model.HistoryRecord.ServiceStatus;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

public abstract class AbstractWatchDog extends ScheduledService {

    protected static final Integer DEFAULT_TIMEOUT = 2000;
    protected static final Integer DEFAULT_MAX_FAILURE = 3;
    protected static final Duration DEFAULT_PERIOD = new Duration(15000);

    private IntegerProperty id;
    private StringProperty service;
    private ObjectProperty<HistoryRecord.ServiceStatus> currentStatus;
    private IntegerProperty timeout;
    private FloatProperty uptimeSinceBeginning;
    private LocalDateTime creationDateTime;
    private LinkedList<HistoryRecord> monitoringHistory;
    
    private Integer upCounter;
    private Integer downCounter;

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

        monitoringHistory.add(new HistoryRecord(LocalDateTime.now(), newCurrentStatus));

        if (newCurrentStatus.equals(ServiceStatus.UP)) {
            upCounter += 1;
        } else if (newCurrentStatus.equals(ServiceStatus.DOWN)) {
            downCounter += 1;
        }

        if (upCounter > 0) {
            if (downCounter == 0) {
                uptimeSinceBeginning.set(100);
            } else {
                uptimeSinceBeginning.set((float)(downCounter / upCounter * 100.0));
            }
        }
    }

    public Integer getTimeout() {
        return timeout.get();
    }

    public void setTimeout(Integer newValue) {
        timeout.set(newValue);
    }

    public Float getUptimeSinceBeginning() {
        return uptimeSinceBeginning.get();
    }

    public void setUptimeSinceBeginning(Float newValue) {
        this.uptimeSinceBeginning.set(newValue);
    }

    public FloatProperty uptimeFloatProperty(){
        return uptimeSinceBeginning;
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
    
    public IntegerProperty timeoutProperty() {
        return timeout;
    }

    public FloatProperty uptimeSinceBeginningProperty() {
        return uptimeSinceBeginning;
    }

    // force all child classes to define their own type so that it can be shown in the tableviews
    protected abstract void setTypeProperty();

    public StringProperty typeProperty() {
        return type;
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
        super.setDelay(Duration.seconds(3));

        this.id = new SimpleIntegerProperty(id);
        this.service = new SimpleStringProperty(service);
        this.timeout = new SimpleIntegerProperty(timeout);
        this.monitoringHistory = new LinkedList<HistoryRecord>(); 
        this.uptimeSinceBeginning = new SimpleFloatProperty(0);

        upCounter = 0;
        downCounter = 0;

        creationDateTime = LocalDateTime.now();
        this.currentStatus = new SimpleObjectProperty<>();

        this.setCurrentStatus(ServiceStatus.UNKNOWN);

        this.setOnFailed((event) -> onExceptionRaised());
        this.setOnCancelled((event) -> this.setCurrentStatus(ServiceStatus.PAUSED));
    }

    private void onExceptionRaised() {
        this.setCurrentStatus(ServiceStatus.FAILED);
    } 

    protected AbstractWatchDog(Integer id, String service) {
        this(id, service, DEFAULT_TIMEOUT, DEFAULT_PERIOD, DEFAULT_MAX_FAILURE);
    }

    public abstract void checkServiceAvailability() throws Exception;

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
