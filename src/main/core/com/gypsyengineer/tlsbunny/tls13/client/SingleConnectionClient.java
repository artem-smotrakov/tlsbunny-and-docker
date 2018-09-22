package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Check;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;

import java.util.List;

public abstract class SingleConnectionClient extends AbstractClient {

    @Override
    public final Client connect() throws Exception {
        output.info("connect to %s:%d", config.host(), config.port());
        engine = createEngine();
        engine.connect();
        List<Check> checks = createChecks();
        for (Check check : checks) {
            engine.run(check);
        }
        return this;
    }

    protected abstract Engine createEngine() throws Exception;

    protected abstract List<Check> createChecks();

}
