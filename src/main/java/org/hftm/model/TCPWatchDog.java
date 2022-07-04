package org.hftm.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.hftm.util.WatchDogType;
import org.hftm.util.HistoryRecord.ServiceStatus;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TCPWatchDog extends AbstractWatchDog {

    public static final Integer DEFAULT_PORT = 80;

    private IntegerProperty port;

    public Integer getPort() {
        return port.get();
    }

    public void setPort(Integer port) {
        this.port.set(port);
    }

    public IntegerProperty portProperty() {
        return port;
    }

    public TCPWatchDog(Integer id, String service, Integer port) {
        super(id, service, DEFAULT_TIMEOUT, DEFAULT_PERIOD, DEFAULT_MAX_FAILURE);

        this.port = new SimpleIntegerProperty(port);
        setTypeProperty();
    }

    @Override
    public void checkServiceAvailability() throws UnknownHostException {
        boolean isReachable = false;

        for (Integer count = getMaximumFailureCount(); count > 0; count--) {
            try {
                Socket clientSocket = new Socket(getService(), getPort());
                clientSocket.setSoTimeout(getTimeout().toMillisPart());
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                clientSocket.close();
                out.close();
                in.close();

                isReachable = true;
                break;
            } catch (UnknownHostException e) {
                throw new UnknownHostException(e.getMessage());
            } catch (IOException e) {
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
        super.type = new SimpleStringProperty(WatchDogType.TCP.toString());
        
    }
}
