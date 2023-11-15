package com.example.gpsapplication;

import java.time.Instant;


public class Place {
    private final String address;
    private Long timeSpent;
    private Instant startTime;

    public Place(String a) {
        timeSpent = 0L;
        address = a;
        startTime = Instant.now();
    }

    public void setStartTime() {
        startTime = Instant.now();
    }

    public void updateTime() {
        timeSpent += Instant.now().toEpochMilli() - startTime.toEpochMilli();
    }

    public String getAddress() {
        return address;
    }

    public Long getTimeSpent() {
        if (timeSpent < 2) {
            return 0L;
        } else {
            return timeSpent;
        }

    }
}
