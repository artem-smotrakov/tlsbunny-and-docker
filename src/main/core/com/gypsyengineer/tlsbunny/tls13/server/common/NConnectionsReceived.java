package com.gypsyengineer.tlsbunny.tls13.server.common;

public class NConnectionsReceived implements StopCondition {

    private final int maxConnections;
    private int counter = 0;

    public NConnectionsReceived(int maxConnections) {
        if (maxConnections <= 0) {
            throw new IllegalArgumentException(String.format(
                    "what the hell? maxConnections can't be negative (%d)",
                    maxConnections));
        }
        this.maxConnections = maxConnections;
    }

    @Override
    public boolean shouldRun() {
        if (counter == maxConnections) {
            return false;
        }

        counter++;

        return true;
    }
}
