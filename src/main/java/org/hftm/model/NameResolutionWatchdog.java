package org.hftm.model;

import org.xbill.DNS.*;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

public class NameResolutionWatchdog extends AbstractWatchdog {

    private StringProperty resolver;
    private ObjectProperty<Type> recordType;

    public Type getRecordType() {
        return recordType.get();
    }

    public void setRecordType(Type recordType) {
        this.recordType.set(recordType);
    }

    public String getResolver() {
        return resolver.get();
    }

    public void setResolver(String resolver) {
        this.resolver.set(resolver);
    }

    public StringProperty getResolverProperty() {
        return resolver;
    }

    public ObjectProperty<Type> getRecordTypeProperty() {
        return recordType;
    }

    public NameResolutionWatchdog(Integer id, String service, Integer timeout, Integer heartbeat, Integer retries) {
        super(id, service, timeout, heartbeat, retries);
        // TODO Auto-generated constructor stub
    }

    public NameResolutionWatchdog(Integer id, String service) {
        super(id, service);
    }

    @Override
    public void checkServiceAvailability() throws Exception {

    };
}
