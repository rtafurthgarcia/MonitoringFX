package org.hftm.util;

/**
 * OsDetectionUtil
 */
public class OsDetectionUtil {
    public enum OperatingSystemType {
        WINDOWS,
        UNIX
    };

    public static OperatingSystemType getOperatingSystemType() {
        String osName = System.getProperty("os.name").toLowerCase().substring(1, 3);

        if (osName.equals("win")) {
            return OperatingSystemType.WINDOWS;
        } else {
            return OperatingSystemType.UNIX;
        }
    }
    
}