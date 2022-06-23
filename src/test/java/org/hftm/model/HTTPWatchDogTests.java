package org.hftm.model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class HTTPWatchDogTests {
	@Test
    void CheckBasicSites() throws Exception {
        HTTPWatchDog watchDog = new HTTPWatchDog(1, "https://www.hftm.ch", 2000, 3, 3);
        assertEquals(HistoryRecord.ServiceStatus.UNKNOWN, watchDog.getCurrentStatus());

        watchDog.checkServiceAvailability();
        assertEquals(HistoryRecord.ServiceStatus.UP, watchDog.getCurrentStatus());

        assertEquals(2, watchDog.getMonitoringHistory().size());
    }
    
    @Test
    void CheckUnaccessibleWebsites() throws Exception {
        HTTPWatchDog watchDog = new HTTPWatchDog(1, "https://does.not.exist.google.com", 2000, 3, 3);
        assertEquals(HistoryRecord.ServiceStatus.UNKNOWN, watchDog.getCurrentStatus());

        watchDog.checkServiceAvailability();
        assertEquals(HistoryRecord.ServiceStatus.DOWN, watchDog.getCurrentStatus());

        assertEquals(2, watchDog.getMonitoringHistory().size());

        watchDog.setService("https://google.com/test");
        watchDog.checkServiceAvailability();
        assertEquals(HistoryRecord.ServiceStatus.DOWN, watchDog.getCurrentStatus());

        assertEquals(4, watchDog.getMonitoringHistory().size());
    }
}