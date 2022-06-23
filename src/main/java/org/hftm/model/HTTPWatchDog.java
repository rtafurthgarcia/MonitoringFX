package org.hftm.model;



public class HTTPWatchDog extends AbstractWatchdog {

    protected HTTPWatchDog(Integer id, String service, Integer timeout, Integer heartbeat, Integer retries) {
        super(id, service, timeout, heartbeat, retries);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void checkServiceAvailability() {
        // TODO Auto-generated method stub
        
    }
    
}
