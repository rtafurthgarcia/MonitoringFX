package org.hftm.model;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
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

public class HTTPWatchDog extends AbstractWatchDog {

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

    public StringProperty headersProperty() {
        return headers;
    }

    public StringProperty bodyProperty() {
        return body;
    }

    public ObjectProperty<RequestType> requestTypeProperty() {
        return requestType;
    }

    ChangeListener<String> propertyChangedListener = (observable, oldValue, newValue) -> generateBuilder();
    ChangeListener<RequestType> requestTypeChangedListener = (observable, oldValue, newValue) -> generateBuilder();

    private HttpClient client;
    private HttpRequest.Builder builder;

    private void generateBuilder() {
        builder = HttpRequest.newBuilder();
        builder = builder.uri(URI.create(getService()));
        builder = builder.timeout(getTimeout());
        if (! getHeaders().isEmpty()) {
            builder = builder.headers(getHeaders());
        }
        builder.method(getRequestType().name(), BodyPublishers.ofString(getBody()));
    }

    public HTTPWatchDog(Integer id, String service, Duration timeout, Duration period, Integer maxFailures) throws UnknownHostException {
        super(id, service, timeout, period, maxFailures);

        client = HttpClient.newBuilder()
                //.connectTimeout(java.time.Duration.ofMillis(getTimeout()))
                .followRedirects(Redirect.NORMAL)
                .build();
        
        this.body = new SimpleStringProperty("");
        this.headers = new SimpleStringProperty("");
        this.requestType = new SimpleObjectProperty<>(RequestType.GET);

        this.serviceProperty().addListener(propertyChangedListener);
        this.body.addListener(propertyChangedListener);
        this.headers.addListener(propertyChangedListener);
        this.requestType.addListener(requestTypeChangedListener);

        setTypeProperty();
    }

    public HTTPWatchDog(Integer id, String service, RequestType type, String headers, String body) throws UnknownHostException {
        this(id, service, DEFAULT_TIMEOUT, DEFAULT_PERIOD, DEFAULT_MAX_FAILURE);

        this.body.set(body);
        this.headers.set(headers);
        this.requestType.set(type);

        // have to trigger it manually in case all values are default -> changelistener is never triggered if values do not change!!!
        generateBuilder();
    }

    @Override
    public void checkServiceAvailability() throws IOException, InterruptedException {
        boolean isReachable = false;

        for (Integer count = getMaximumFailureCount(); count > 0; count--) {
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
            } catch (HttpTimeoutException e) {
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
        super.type = new SimpleStringProperty("HTTP(S)");
    } 
}
