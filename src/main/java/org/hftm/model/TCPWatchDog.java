package org.hftm.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.hftm.model.HistoryRecord.ServiceStatus;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class TCPWatchDog extends AbstractWatchdog {

    private IntegerProperty port;

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public Integer getPort() {
        return port.get();
    }

    public void setPort(Integer port) {
        this.port.set(port);
    }

    public IntegerProperty getPortProperty() {
        return port;
    }

    public TCPWatchDog(Integer id, String service, Integer port) {
        super(id, service, DEFAULT_TIMEOUT, DEFAULT_HEARTBEAT, DEFAULT_RETRIES);

        this.port = new SimpleIntegerProperty(port);
    }

    @Override
    public void checkServiceAvailability() throws UnknownHostException {
        boolean isReachable = false;

        for (Integer count = getRetriesProperty().get(); count > 0; count--) {
            try {
                clientSocket = new Socket(getService(), getPort());
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

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
}
