package com.realoliver123.hubclient.util;

public class Clock {
    private long endTime;
    private boolean scheduled;

    public void schedule(long milliseconds) {
        this.endTime = System.currentTimeMillis() + milliseconds;
        this.scheduled = true;
    }

    public boolean passed() {
        return System.currentTimeMillis() >= this.endTime;
    }

    public boolean isScheduled() {
        return this.scheduled;
    }

    public void reset() {
        this.scheduled = false;
        this.endTime = 0;
    }

    // Stub methods for uptime tracking (used in AbstractMacro)
    public void start(boolean reset) {
        // Implementation for stats tracking can go here later
    }

    public void stop(boolean reset) {
        // Implementation for stats tracking can go here later
    }
}