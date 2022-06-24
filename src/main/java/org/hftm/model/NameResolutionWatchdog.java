package org.hftm.model;

public class NameResolutionWatchdog extends AbstractWatchdog {

    protected NameResolutionWatchdog(Integer id, String service, Integer timeout, Integer heartbeat, Integer retries) {
        super(id, service, timeout, heartbeat, retries);
        // TODO Auto-generated constructor stub
    }

    protected NameResolutionWatchdog(Integer id, String service) {
        super(id, service);
    }

    @Override
    public void checkServiceAvailability() throws Exception {
        
    };
}
