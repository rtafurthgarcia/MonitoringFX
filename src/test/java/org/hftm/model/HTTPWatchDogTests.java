package org.hftm.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.hftm.util.HistoryRecord;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;

public class HTTPWatchDogTests {

    private CountDownLatch waiter;

    private ServerSocket server;

    HTTPWatchDog watchDog;

	@Test
    void CheckBasicSites() throws Exception {
        watchDog = new HTTPWatchDog(1, "https://www.hftm.ch", HTTPWatchDog.RequestType.GET, "", "");
        assertEquals(HistoryRecord.ServiceStatus.UNKNOWN, watchDog.getCurrentStatus());

        watchDog.checkServiceAvailability();
        assertEquals(HistoryRecord.ServiceStatus.UP, watchDog.getCurrentStatus());

        assertEquals(2, watchDog.getMonitoringHistory().size());
    }
    
    @Test
    void CheckUnaccessibleWebsites() throws Exception {
        watchDog = new HTTPWatchDog(1, "https://does.not.exist.google.com", HTTPWatchDog.RequestType.GET, "", "");
        assertEquals(HistoryRecord.ServiceStatus.UNKNOWN, watchDog.getCurrentStatus());

        watchDog.checkServiceAvailability();
        assertEquals(HistoryRecord.ServiceStatus.DOWN, watchDog.getCurrentStatus());

        assertEquals(2, watchDog.getMonitoringHistory().size());

        watchDog.setService("https://google.com/test");
        watchDog.checkServiceAvailability();
        assertEquals(HistoryRecord.ServiceStatus.DOWN, watchDog.getCurrentStatus());

        assertEquals(4, watchDog.getMonitoringHistory().size());
    }

    void listen4once() {
        try {
            server = new ServerSocket(8080);
            System.out.println("Listening for connection on port 8080 ....");
            try (Socket socket = server.accept()) {
                Date today = new Date();
                String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + today;
                socket.getOutputStream()
                    .write(httpResponse.getBytes("UTF-8"));
            }
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    //@Test
    void CheckFakeWebsite() throws Exception {        
        new Thread(new Runnable() {
            public void run() {
                // stuff here
                listen4once();
            }
        }).start();

        waiter = new CountDownLatch(1);

        Platform.startup(() -> {
            assertDoesNotThrow(() -> {
                watchDog = new HTTPWatchDog(1, "http://127.0.0.1:8080/test", HTTPWatchDog.RequestType.GET, "", "");
                waiter.countDown();
            });
        });
        waiter.await(HTTPWatchDog.DEFAULT_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        assertEquals(HistoryRecord.ServiceStatus.UNKNOWN, watchDog.getCurrentStatus());

        waiter = new CountDownLatch(1);

        watchDog.start();  
        watchDog.currentStatusProperty().addListener((observable) -> {
            if (server.isClosed()) {
                assertEquals(HistoryRecord.ServiceStatus.DOWN, watchDog.getCurrentStatus());
                waiter.countDown();
            } else {
                assertEquals(HistoryRecord.ServiceStatus.UP, watchDog.getCurrentStatus());
                waiter.countDown();
            }
        });
        waiter.await(watchDog.getTimeout().toMillis() * 2, TimeUnit.MILLISECONDS);
    
        assertEquals(4, watchDog.getMonitoringHistory().size());
        assertEquals(0, waiter.getCount());
    }
}