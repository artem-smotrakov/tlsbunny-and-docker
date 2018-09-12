package com.gypsyengineer.tlsbunny.tls13.server.common;

public class NonStop implements StopCondition {

    @Override
    public boolean shouldRun() {
        return true;
    }
}
