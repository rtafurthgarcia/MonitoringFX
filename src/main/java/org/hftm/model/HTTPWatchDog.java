package org.hftm.model;

import java.lang.module.ModuleDescriptor.Builder;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import org.hftm.model.HistoryRecord.ServiceStatus;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HTTPWatchDog extends AbstractWatchdog {

    /**public enum RequestType {
        GET,
        POST,
        PUT,
        DELETE,
    }**/

    private StringProperty requestType;
    private StringProperty headers;
    private StringProperty body;

    public String getRequestType() {
        return requestType.get();
    }

    public void setRequestType(String requestType) {
        this.requestType.set(requestType);

        generateBuilder();
    }

    public String getHeaders() {
        return headers.get();
    }

    public void setHeaders(String headers) {
        this.headers.set(headers);

        generateBuilder();
    }

    public String getBody() {
        return body.get();
    }

    public void setBody(String body) {
        this.body.set(body);

        generateBuilder();
    }

    public StringProperty getHeadersProperty() {
        return headers;
    }

    public StringProperty getBodyProperty() {
        return body;
    }

    public StringProperty getRequestTypeProperty() {
        return requestType;
    }

    private HttpClient client;
    private HttpRequest.Builder builder;

    private void generateBuilder() {
        builder = HttpRequest.newBuilder();
        builder = builder.uri(URI.create(getService()));
        if (! getHeaders().equals("")) {
            builder = builder.headers(getHeaders());
        }
        builder.method(getRequestType(), BodyPublishers.ofString(getBody()));
    }

    protected HTTPWatchDog(Integer id, String service, Integer timeout, Integer heartbeat, Integer retries) {
        super(id, service, timeout, heartbeat, retries);
        // TODO Auto-generated constructor stub

        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(getTimeout()))
                .followRedirects(Redirect.NORMAL)
                .build();
        
        this.body = new SimpleStringProperty("");
        this.headers = new SimpleStringProperty("");
        this.requestType = new SimpleStringProperty("GET");

        generateBuilder();
    }

    protected HTTPWatchDog(Integer id, String service, String type, String headers, String body) {
        super(id, service);

        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(getTimeout()))
                .followRedirects(Redirect.NORMAL)
                .build();

        this.body = new SimpleStringProperty(body);
        this.headers = new SimpleStringProperty(headers);
        this.requestType = new SimpleStringProperty(type);

        generateBuilder();
    }

    @Override
    public void checkServiceAvailability() throws Exception {
        boolean isReachable = false;

        for (Integer count = getRetriesProperty().get(); count > 0; count--) {
            HttpRequest request = builder.build();

            try {
                HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                if (response.statusCode() >= 200 && response.statusCode() <= 299) {
                    isReachable = true;
                    break;
                } else {
                    isReachable = false;
                }   
            } catch (ConnectException e) {
                // when the host doesnt exist for example or when it cannot resolve
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
