package org.hftm.model;

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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;

public class HTTPWatchDog extends AbstractWatchdog {

    public enum RequestType {
        GET,
        PUT,
        POST,
        DELETE,
        CONNECT,
        HEAD,
        OPTIONS
    }

    private ObjectProperty<RequestType> requestType;
    private StringProperty headers;
    private StringProperty body;

    public RequestType getRequestType() {
        return requestType.get();
    }

    public void setRequestType(RequestType requestType) {
        this.requestType.set(requestType);
    }

    public String getHeaders() {
        return headers.get();
    }

    public void setHeaders(String headers) {
        this.headers.set(headers);
    }

    public String getBody() {
        return body.get();
    }

    public void setBody(String body) {
        this.body.set(body);
    }

    public StringProperty getHeadersProperty() {
        return headers;
    }

    public StringProperty getBodyProperty() {
        return body;
    }

    public ObjectProperty<RequestType> getRequestTypeProperty() {
        return requestType;
    }

    ChangeListener<String> propertyChangedListener = (observable, oldValue, newValue) -> generateBuilder();
    ChangeListener<RequestType> requestTypeChangedListener = (observable, oldValue, newValue) -> generateBuilder();

    private HttpClient client;
    private HttpRequest.Builder builder;

    private void generateBuilder() {
        builder = HttpRequest.newBuilder();
        builder = builder.uri(URI.create(getService()));
        if (! getHeaders().isEmpty()) {
            builder = builder.headers(getHeaders());
        }
        builder.method(getRequestType().name(), BodyPublishers.ofString(getBody()));
    }

    protected HTTPWatchDog(Integer id, String service, Integer timeout, Integer heartbeat, Integer retries) {
        super(id, service, timeout, heartbeat, retries);

        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(getTimeout()))
                .followRedirects(Redirect.NORMAL)
                .build();
        
        this.body = new SimpleStringProperty("");
        this.headers = new SimpleStringProperty("");
        this.requestType = new SimpleObjectProperty<>(RequestType.GET);

        this.getServiceProperty().addListener(propertyChangedListener);
        this.body.addListener(propertyChangedListener);
        this.headers.addListener(propertyChangedListener);
        this.requestType.addListener(requestTypeChangedListener);
    }

    protected HTTPWatchDog(Integer id, String service, RequestType type, String headers, String body) {
        this(id, service, DEFAULT_TIMEOUT, DEFAULT_HEARTBEAT, DEFAULT_RETRIES);

        this.body.set(body);
        this.headers.set(headers);
        this.requestType.set(type);

        // have to trigger it manually in case all values are default -> changelistener is never triggered if values do not change!!!
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
