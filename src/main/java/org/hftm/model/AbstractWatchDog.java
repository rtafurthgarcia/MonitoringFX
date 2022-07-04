package org.hftm.model;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.hftm.model.HistoryRecord.ServiceStatus;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import java.time.Duration;

public abstract class AbstractWatchDog extends ScheduledService {

    protected static final Integer DEFAULT_MAX_FAILURE = 3;
    // Difference between javafx.util and java.time -> im just trying to make it make sense lol
    protected static final Duration DEFAULT_TIMEOUT = Duration.ofMillis(2000);
    protected static final Duration DEFAULT_PERIOD = Duration.ofMillis(15000);

    private IntegerProperty id;
    private StringProperty service;
    private ObjectProperty<HistoryRecord.ServiceStatus> currentStatus;
    private ObjectProperty<Duration> timeout;
    private DoubleProperty uptimeSinceBeginning;
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
                Double rate =  (double) upCounter / (double) (upCounter + downCounter) * 100.0;
                uptimeSinceBeginning.set(rate);
            }
        }
    }

    public Duration getTimeout() {
        return timeout.get();
    }

    public void setTimeout(Duration newValue) {
        timeout.set(newValue);
    }

    public Double getUptimeSinceBeginning() {
        return uptimeSinceBeginning.get();
    }

    public void setUptimeSinceBeginning(Float newValue) {
        this.uptimeSinceBeginning.set(newValue);
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
    
    public ObjectProperty<Duration> timeoutProperty() {
        return timeout;
    }

    public DoubleProperty uptimeSinceBeginningProperty() {
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

    protected AbstractWatchDog(Integer id, String service, Duration timeout, Duration period, Integer maxFailures) {
        super();
        super.setPeriod(javafx.util.Duration.millis(period.toMillis()));
        super.setMaximumFailureCount(maxFailures);
        super.setDelay(javafx.util.Duration.seconds(3));

        this.id = new SimpleIntegerProperty(id);
        this.service = new SimpleStringProperty(service);
        this.monitoringHistory = new LinkedList<>(); 
        this.uptimeSinceBeginning = new SimpleDoubleProperty(0.0);
        this.timeout = new SimpleObjectProperty<Duration>(timeout);


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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((service == null) ? 0 : service.hashCode());
        result = prime * result + ((timeout == null) ? 0 : timeout.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        AbstractWatchDog other = (AbstractWatchDog) obj;
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
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
