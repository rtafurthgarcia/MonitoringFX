package org.hftm.model;

import static org.junit.jupiter.api.Assertions.*;

import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class NameResolutionWatchDogTests {
    @Test
    void checkValidRecords() throws UnknownHostException, TextParseException {
        NameResolutionWatchDog watchDog = new NameResolutionWatchDog(1, "hftm.ch", Type.MX, new SimpleResolver("1.1.1.1"));
        assertEquals(HistoryRecord.ServiceStatus.UNKNOWN, watchDog.getCurrentStatus());

        watchDog.checkServiceAvailability();
        assertEquals(HistoryRecord.ServiceStatus.UP, watchDog.getCurrentStatus());

        assertEquals(2, watchDog.getMonitoringHistory().size());

        watchDog.setRecordType(Type.A);

        watchDog.checkServiceAvailability();
        assertEquals(HistoryRecord.ServiceStatus.UP, watchDog.getCurrentStatus());

        assertEquals(3, watchDog.getMonitoringHistory().size());
    }

    @Test
    void checkWithFuckedUpResolver() throws UnknownHostException, TextParseException {
        NameResolutionWatchDog watchDog = new NameResolutionWatchDog(1, "hftm.ch", Type.MX, new SimpleResolver("2.2.2.2"));
        assertEquals(HistoryRecord.ServiceStatus.UNKNOWN, watchDog.getCurrentStatus());

        watchDog.checkServiceAvailability();
        assertEquals(HistoryRecord.ServiceStatus.DOWN, watchDog.getCurrentStatus());

        assertEquals(2, watchDog.getMonitoringHistory().size());

        assertThrows(UnknownHostException.class, () -> {
            watchDog.setResolver(new SimpleResolver("does.not.exist"));
        });

        assertEquals(2, watchDog.getMonitoringHistory().size());
    }

    @Test
    void checkInvalidRecords() throws UnknownHostException, TextParseException {
        NameResolutionWatchDog watchDog = new NameResolutionWatchDog(1, "does.not.exist", Type.MX, new SimpleResolver("1.1.1.1"));
        assertEquals(HistoryRecord.ServiceStatus.UNKNOWN, watchDog.getCurrentStatus());

        watchDog.checkServiceAvailability();
        assertEquals(HistoryRecord.ServiceStatus.DOWN, watchDog.getCurrentStatus());

        assertEquals(2, watchDog.getMonitoringHistory().size());
    }
}