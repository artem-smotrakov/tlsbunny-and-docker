package com.gypsyengineer.tlsbunny.tls13.test.client;

import com.gypsyengineer.tlsbunny.tls13.connection.*;

public class StartWithCCS {

    public static void main(String[] args) throws Exception {
        CommonConfig config = new CommonConfig();

        Engine.init()
                .target(config.host())
                .target(config.port())
                .send(new OutgoingChangeCipherSpec())
                .send(new OutgoingClientHello())
                .require(new IncomingAlert())
                .connect()
                .run(new AlertCheck());
    }
}
