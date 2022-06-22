package org.hftm.model;

import java.io.IOException;
import java.net.InetAddress;

import org.hftm.model.HistoryRecord.ServiceStatus;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class PingWatchDog extends AbstractWatchdog {

    protected PingWatchDog(Integer id, String service, Integer timeout, Integer heartbeat, Integer retries) {
        super(id, service, timeout, heartbeat, retries);
        //TODO Auto-generated constructor stub
    }

    public void checkServiceAvailability() {
        try{
            // https://stackoverflow.com/questions/11506321/how-to-ping-an-ip-address
            // (...) A typical implementation will use ICMP ECHO REQUESTs if the privilege can be obtained, 
            // otherwise it will try to establish a TCP connection on port 7 (Echo) of the destination host
            InetAddress address = InetAddress.getByName(this.getServiceProperty().get());
            boolean reachable = address.isReachable(this.getTimeoutProperty().get());

            if (reachable) {
                setCurrentStatus(ServiceStatus.UP);
            } else {
                setCurrentStatus(ServiceStatus.DOWN);
            }
            
        } catch (IOException error){
            setCurrentStatus(ServiceStatus.DOWN);
        }
        
    }
    
}
