package org.hftm.model;

import java.io.IOException;
import java.net.InetAddress;

import org.hftm.model.HistoryRecord.ServiceStatus;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class PingWatchDog extends AbstractWatchdog {

    public PingWatchDog(IntegerProperty id, StringProperty service, IntegerProperty timeout, IntegerProperty heartbeat,
            IntegerProperty retries) {
        super(id, service, timeout, heartbeat, retries);
        
    }

    public void checkServiceAvailability() {
        try{
            // https://stackoverflow.com/questions/11506321/how-to-ping-an-ip-address
            // (...) A typical implementation will use ICMP ECHO REQUESTs if the privilege can be obtained, 
            // otherwise it will try to establish a TCP connection on port 7 (Echo) of the destination host
            InetAddress address = InetAddress.getByName(this.service.get());
            boolean reachable = address.isReachable(this.timeout.get());

            if (reachable) {
                this.currentStatus.set(ServiceStatus.UP);
            } else {
                this.currentStatus.set(ServiceStatus.DOWN);
            }
            
        } catch (IOException error){
            this.currentStatus.set(ServiceStatus.DOWN);
        }
        
    }
    
}
