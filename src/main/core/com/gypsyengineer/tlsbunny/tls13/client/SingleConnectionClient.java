package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.connection.Engine;

public abstract class SingleConnectionClient extends AbstractClient {

    @Override
    public final Client connect() throws Exception {
        output.info("connect to %s:%d", config.host(), config.port());
        recentEngine = createEngine();
        recentEngine.connect();
        recentEngine.run(checks);
        return this;
    }

    protected abstract Engine createEngine() throws Exception;
}
