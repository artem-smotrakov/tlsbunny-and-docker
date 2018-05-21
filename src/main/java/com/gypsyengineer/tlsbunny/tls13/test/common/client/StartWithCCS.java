package com.gypsyengineer.tlsbunny.tls13.test.common.client;

import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.IncomingAlert;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.OutgoingChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.OutgoingClientHello;
import com.gypsyengineer.tlsbunny.tls13.test.SystemPropertiesConfig;

public class StartWithCCS {

    public static void main(String[] args) throws Exception {
        SystemPropertiesConfig config = SystemPropertiesConfig.load();

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
