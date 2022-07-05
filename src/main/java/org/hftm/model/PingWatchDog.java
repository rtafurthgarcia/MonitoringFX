package org.hftm.model;

import java.io.IOException;
import java.net.InetAddress;

import org.hftm.util.OsDetectionUtil;
import org.hftm.util.WatchDogType;
import org.hftm.util.HistoryRecord.ServiceStatus;
import org.hftm.util.OsDetectionUtil.OperatingSystemType;

import javafx.beans.property.SimpleStringProperty;
import java.time.Duration;

public class PingWatchDog extends AbstractWatchDog {

    public PingWatchDog(Integer id, String service, Duration timeout, Duration period, Integer maxFailures) {
        super(id, service, timeout, period, maxFailures);
        setTypeProperty();
    }

    public PingWatchDog(Integer id, String service) {
        this(id, service, DEFAULT_TIMEOUT, DEFAULT_PERIOD, DEFAULT_MAX_FAILURE);
    }

    /*
     * the classical isReachable method doesnt work -> relies on admin priviledges
     * to access raw IP sockets to execute our ICMP request
     * https://docs.oracle.com/javase/7/docs/api/java/net/InetAddress.html#
     * isReachable%28int%29
     * hence why I had to rely on the host OS toolset to ping my hosts
     */
    public void checkServiceAvailability() throws IOException, InterruptedException {
        String pingCmd;
        boolean isReachable = false;
        OperatingSystemType operatingSystemType = OsDetectionUtil.getOperatingSystemType();

        // Is gonna throw an exception if address is incorrect
        InetAddress.getAllByName(getService());

        if (operatingSystemType == OperatingSystemType.WINDOWS) {
            pingCmd = String.format("ping -n 1 -w %d %s", getTimeout().toMillis(), getService());
        } else {
            pingCmd = String.format("ping -c 1 -W %d %s", getTimeout().toMillis(), getService());
        }

        for (Integer count = getMaximumFailureCount(); count > 0; count--) {
            ProcessBuilder processBuilder = new ProcessBuilder(pingCmd.split(" "));
            Process process = processBuilder.start();

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

    @Override
    protected void setTypeProperty() {
        super.type = new SimpleStringProperty(WatchDogType.PING.toString());

    }
}
