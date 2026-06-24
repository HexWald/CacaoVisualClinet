package net.cacaovisualclient.mod.utils;

public class TimeDelay {

    private long lastTime = System.currentTimeMillis();

    public void reset() {
        lastTime = System.currentTimeMillis();
    }

    public boolean hasPassed(long time) {
        return System.currentTimeMillis() - lastTime >= time;
    }
}
