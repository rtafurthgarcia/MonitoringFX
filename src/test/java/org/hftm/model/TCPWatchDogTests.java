package org.hftm.model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.UnknownHostException;

import org.hftm.util.HistoryRecord;
import org.junit.jupiter.api.Test;

public class TCPWatchDogTests {
    
    @Test
    void checkIfConnectionIsPossible() throws IOException {
        TCPWatchDog watchDog = new TCPWatchDog(1, "hftm.ch", 443);
        assertEquals(HistoryRecord.ServiceStatus.UNKNOWN, watchDog.getCurrentStatus());

        watchDog.checkServiceAvailability();
        assertEquals(HistoryRecord.ServiceStatus.UP, watchDog.getCurrentStatus());
    }

    @Test
    void checkInvalidHosts() throws UnknownHostException {
        TCPWatchDog watchDog = new TCPWatchDog(1, "https://hftm.ch", 443);
        assertEquals(HistoryRecord.ServiceStatus.UNKNOWN, watchDog.getCurrentStatus());

        assertThrows(UnknownHostException.class, () -> {
            watchDog.checkServiceAvailability();
        });
    }
}
