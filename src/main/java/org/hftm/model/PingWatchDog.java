package org.hftm.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.hftm.model.HistoryRecord.ServiceStatus;

import org.hftm.util.OsDetectionUtil;
import org.hftm.util.OsDetectionUtil.OperatingSystemType;;

public class PingWatchDog extends AbstractWatchdog {

    protected PingWatchDog(Integer id, String service, Integer timeout, Integer heartbeat, Integer retries) {
        super(id, service, timeout, heartbeat, retries);
        //TODO Auto-generated constructor stub
    } 

    protected PingWatchDog(Integer id, String service) {
        super(id, service);
    }

    /*  the classical isReachable method doesnt work -> relies on admin priviledges to access raw IP sockets to execute our ICMP request
        https://docs.oracle.com/javase/7/docs/api/java/net/InetAddress.html#isReachable%28int%29
        hence why I had to rely on the host OS toolset to ping my hosts
    */
    public void checkServiceAvailability() throws IOException, InterruptedException {
        String pingCmd;
        boolean isReachable = false;
        OperatingSystemType operatingSystemType = OsDetectionUtil.getOperatingSystemType();

        // Is gonna throw an exception if address is incorrect
        InetAddress.getAllByName(getService());

        if (operatingSystemType == OperatingSystemType.WINDOWS) {
            pingCmd = String.format("ping /n 1 /w %d %s", getTimeout(), getService());
        } else {
            pingCmd = String.format("ping -c 1 -W %d %s", getTimeout(), getService());
        }

        for (Integer count = getRetriesProperty().get(); count > 0; count--) {
            Runtime run  = Runtime.getRuntime();
            Process process = run.exec(pingCmd);

            if (process.waitFor() != 0) {
                isReachable = false;
            } else {
                isReachable = true; 
                break;
            }
        }

        if (isReachable) {
            setCurrentStatus(ServiceStatus.UP);
        } else {
            setCurrentStatus(ServiceStatus.DOWN);
        }
    }
}
