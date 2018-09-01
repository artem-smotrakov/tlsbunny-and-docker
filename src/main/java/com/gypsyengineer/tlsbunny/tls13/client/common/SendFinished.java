package com.gypsyengineer.tlsbunny.tls13.client.common;

import com.gypsyengineer.tlsbunny.tls13.connection.AlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;

import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.alert;
import static com.gypsyengineer.tlsbunny.tls13.struct.ContentType.handshake;
import static com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType.finished;
import static com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion.TLSv12;

public class SendFinished {

    public static void main(String[] args) throws Exception {
        SystemPropertiesConfig config = SystemPropertiesConfig.load();

        Engine.init()
                .target(config.host())
                .target(config.port())
                .run(new GeneratingRandomFinishedKey())
                .run(new GeneratingFinished())
                .run(new WrappingIntoHandshake()
                        .type(finished)
                        .run((context, message) -> context.setClientFinished(message)))
                .run(new WrappingIntoTLSPlaintexts()
                        .type(handshake)
                        .version(TLSv12))
                .send(new OutgoingData())
                .receive(new IncomingData())
                .run(new ProcessingTLSPlaintext()
                        .expect(alert))
                .run(new ProcessingAlert())
                .connect()
                .run(new AlertCheck());
    }

}
