package org.hftm.model;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import org.hftm.model.HistoryRecord.ServiceStatus;

public class HTTPWatchDog extends AbstractWatchdog {

    protected HTTPWatchDog(Integer id, String service, Integer timeout, Integer heartbeat, Integer retries) {
        super(id, service, timeout, heartbeat, retries);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void checkServiceAvailability() throws Exception {
        boolean isReachable = false;

        for (Integer count = getRetriesProperty().get(); count > 0; count--) {

            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(getTimeout())).build();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(getService())).build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() <= 299) {
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
}
