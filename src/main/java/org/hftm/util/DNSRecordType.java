package org.hftm.util;

import java.util.HashMap;
import java.util.Map;

public enum DNSRecordType {
    A(1),
    NS(2),
    MD(3),
    MF(4),
    CNAME(5),
    SOA(6),
    PTR(12),
    MX(15),
    TXT(16),
    SIG(24),
    KEY(25),
    AAAA(28),
    SSHFP(44),
    DNSKEY(48),
    ANY(255);

    public final Integer code;

    private DNSRecordType(Integer code) {
        this.code = code;
    }

    private static final Map<Integer, DNSRecordType> BY_LABEL = new HashMap<>();

    static {
        for (DNSRecordType e: values()) {
            BY_LABEL.put(e.code, e);
        }
    }

    public static DNSRecordType valueOfLabel(Integer code) {
        return BY_LABEL.get(code);
    }

    @Override 
    public String toString() { 
        return name(); 
    }

    /*public Integer valueOf() {
        return this.valueOf();
    }*/
}
