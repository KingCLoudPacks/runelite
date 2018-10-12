package com.loudpacks.script;

public abstract class ConditionalSleep {

    private final int timeout;
    private final int sleepTime;

    private int timeSlept = 0;

    public ConditionalSleep(int timeout, int sleepTime) {
        this.timeout = timeout;
        this.sleepTime = sleepTime;
    }

    public abstract boolean condition();

    public boolean sleep() {
        do {
            try {
                Thread.sleep(sleepTime);
                timeSlept += sleepTime;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        } while (!condition() && timeSlept < timeout);
        return true;
    }


}
