package org.hftm.model;

import java.net.UnknownHostException;
import java.time.Duration;

import org.hftm.model.HistoryRecord.ServiceStatus;
import org.xbill.DNS.*;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class DNSRecordWatchDog extends AbstractWatchDog {

    private ObjectProperty<SimpleResolver> resolver;
    private SimpleIntegerProperty recordType;


    public Integer getRecordType() {
        return recordType.get();
    }

    public void setRecordType(Integer recordType) {
        this.recordType.set(recordType);
    }

    public SimpleResolver getResolver() {
        return resolver.get();
    }

    public void setResolver(SimpleResolver resolver) {
        this.resolver.set(resolver);
    }

    public ObjectProperty<SimpleResolver> getResolverProperty() {
        return resolver;
    }

    public SimpleIntegerProperty getRecordTypeProperty() {
        return recordType;
    }

    public DNSRecordWatchDog(Integer id, String service, Integer timeout, Integer heartbeat, Integer retries) throws UnknownHostException {
        super(id, service, timeout, heartbeat, retries);
        // TODO Auto-generated constructor stub

        this.resolver = new SimpleObjectProperty<>(new SimpleResolver("1.1.1.1"));
        this.recordType = new SimpleIntegerProperty(Type.ANY);
    }

    public DNSRecordWatchDog(Integer id, String service, Integer recordType, SimpleResolver resolver) throws UnknownHostException {
        this(id, service, DEFAULT_TIMEOUT, DEFAULT_HEARTBEAT, DEFAULT_RETRIES);

        this.recordType.set(recordType);
        this.resolver.set(resolver);
        this.resolver.get().setTimeout(Duration.ofMillis(getTimeout()));
    }

    @Override
    public void checkServiceAvailability() throws TextParseException {
        boolean isReachable = false;

        for (Integer count = getRetriesProperty().get(); count > 0; count--) {
            Lookup lookup = new Lookup(Name.fromString(getService()), getRecordType(), DClass.IN);
            lookup.setResolver(getResolver());
    
            Record[] records = lookup.run();
            if (records != null) {
                isReachable = true;
                break;
            } else {
                isReachable = false;
            }
        }

        if (isReachable) {
            setCurrentStatus(ServiceStatus.UP);
        } else {
            setCurrentStatus(ServiceStatus.DOWN);
        }

    };
}
