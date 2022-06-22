package org.hftm.model;

import org.hftm.model.PingWatchDog;
import org.hftm.model.HistoryRecord;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * PingWatchDogTests
 */
public class PingWatchDogTests {

    @Test
    void checkIfLocalHostWorks() {
        PingWatchDog watchDog = new PingWatchDog(1, "127.0.0.1", 3000, 3, 3);
        assertEquals(HistoryRecord.ServiceStatus.CHECKING, watchDog.getCurrentStatus());

        watchDog.checkServiceAvailability();
        assertEquals(HistoryRecord.ServiceStatus.UP, watchDog.getCurrentStatus());

        assertEquals(2, watchDog.getMonitoringHistory().size());
    };
}