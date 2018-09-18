package com.gypsyengineer.tlsbunny.tls13.client;

import com.gypsyengineer.tlsbunny.tls13.connection.*;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.IncomingAlert;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.OutgoingChangeCipherSpec;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.OutgoingClientHello;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;

public class StartWithCCS {

    public static void main(String[] args) throws Exception {
        SystemPropertiesConfig config = SystemPropertiesConfig.load();

        Engine.init()
                .target(config.host())
                .target(config.port())
                .send(new OutgoingChangeCipherSpec())
                .send(new OutgoingClientHello())
                .receive(new IncomingAlert())
                .connect()
                .run(new AlertCheck());
    }
}
