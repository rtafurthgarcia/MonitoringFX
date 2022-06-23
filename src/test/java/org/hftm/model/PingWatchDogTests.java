package org.hftm.model;

import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * PingWatchDogTests
 */
public class PingWatchDogTests {

    @Test
    void checkGeneralServices() throws Exception {
        PingWatchDog watchDog = new PingWatchDog(1, "127.0.0.1", 3000, 3, 3);
        assertEquals(HistoryRecord.ServiceStatus.UNKNOWN, watchDog.getCurrentStatus());

        watchDog.checkServiceAvailability();
        assertEquals(HistoryRecord.ServiceStatus.UP, watchDog.getCurrentStatus());

        assertEquals(2, watchDog.getMonitoringHistory().size());

        watchDog.setService("1.1.1.1");
        assertEquals(HistoryRecord.ServiceStatus.UNKNOWN, watchDog.getCurrentStatus());

        watchDog.checkServiceAvailability();
        assertEquals(HistoryRecord.ServiceStatus.UP, watchDog.getCurrentStatus());

        assertEquals(4, watchDog.getMonitoringHistory().size());

        watchDog.setService("hftm.ch");
        assertEquals(HistoryRecord.ServiceStatus.UNKNOWN, watchDog.getCurrentStatus());

        watchDog.checkServiceAvailability();
        assertEquals(HistoryRecord.ServiceStatus.UP, watchDog.getCurrentStatus());

        assertEquals(6, watchDog.getMonitoringHistory().size());
    };

    @Test
    void checkImpossibleAddress() {
        PingWatchDog watchDog = new PingWatchDog(1, "999.999.999.999", 3000, 3, 3);
        assertEquals(HistoryRecord.ServiceStatus.UNKNOWN, watchDog.getCurrentStatus());

        assertThrows(UnknownHostException.class, () -> {
            watchDog.checkServiceAvailability();
        });

        assertEquals(1, watchDog.getMonitoringHistory().size());

        watchDog.setService("does.not.resolve");
        assertEquals(HistoryRecord.ServiceStatus.UNKNOWN, watchDog.getCurrentStatus());

        assertThrows(UnknownHostException.class, () -> {
            watchDog.checkServiceAvailability();
        });

        assertEquals(2, watchDog.getMonitoringHistory().size());

    };
}