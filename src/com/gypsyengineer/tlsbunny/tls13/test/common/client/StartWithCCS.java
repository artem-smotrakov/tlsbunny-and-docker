package com.gypsyengineer.tlsbunny.tls13.test.common.client;

import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.IncomingAlert;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.OutgoingChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.OutgoingClientHello;
import com.gypsyengineer.tlsbunny.tls13.test.CommonConfig;

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
