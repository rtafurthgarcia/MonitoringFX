package org.hftm.model;

import java.net.UnknownHostException;
import java.time.Duration;

import org.hftm.util.DNSRecordType;
import org.hftm.util.HistoryRecord.ServiceStatus;
import org.hftm.util.WatchDogType;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class DNSRecordWatchDog extends AbstractWatchDog {

    public static final String DEFAULT_RESOLVER = "1.1.1.1";
    private ObjectProperty<SimpleResolver> resolver;
    private ObjectProperty<DNSRecordType> recordType;

    public DNSRecordType getRecordType() {
        return recordType.get();
    }

    public void setRecordType(DNSRecordType recordType) {
        this.recordType.set(recordType);
    }

    public SimpleResolver getResolver() {
        return resolver.get();
    }

    public void setResolver(SimpleResolver resolver) {
        this.resolver.set(resolver);
    }

    public ObjectProperty<SimpleResolver> resolverProperty() {
        return resolver;
    }

    public ObjectProperty<DNSRecordType> recordTypeProperty() {
        return recordType;
    }

    public DNSRecordWatchDog(Integer id, String service, Duration timeout, Duration period, Integer maxFailures) throws UnknownHostException {
        super(id, service, timeout, period, maxFailures);

        this.resolver = new SimpleObjectProperty<>(new SimpleResolver(DEFAULT_RESOLVER));
        this.recordType = new SimpleObjectProperty<>(DNSRecordType.ANY);
        setTypeProperty();
    }

    public DNSRecordWatchDog(Integer id, String service, DNSRecordType recordType, SimpleResolver resolver) throws UnknownHostException {
        this(id, service, DEFAULT_TIMEOUT, DEFAULT_PERIOD, DEFAULT_MAX_FAILURE);

        this.recordType.set(recordType);
        this.resolver.set(resolver);
        this.resolver.get().setTimeout(getTimeout());
    }

    @Override
    public void checkServiceAvailability() throws TextParseException {
        boolean isReachable = false;

        for (Integer count = getMaximumFailureCount(); count > 0; count--) {
            Lookup lookup = new Lookup(Name.fromString(getService()), getRecordType().code, DClass.IN);
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

    }

    @Override
    protected void setTypeProperty() {
        super.type = new SimpleStringProperty(WatchDogType.DNS.toString());
    }

}
