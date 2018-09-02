package com.gypsyengineer.tlsbunny.tls13.connection.action;

import com.gypsyengineer.tlsbunny.tls13.connection.AbstractCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.Check;
import com.gypsyengineer.tlsbunny.tls13.struct.ServerHello;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;

public class DowngradeMessageCheck extends AbstractCheck {

    private static final byte[] downgrade_tls12_message = new byte[] {
            0x44, 0x4F, 0x57, 0x4E, 0x47, 0x52, 0x44, 0x01
    };

    private static final byte[] downgrade_tls11_and_below_message = new byte[] {
            0x44, 0x4F, 0x57, 0x4E, 0x47, 0x52, 0x44, 0x00
    };

    private byte[] downgrade_message = downgrade_tls12_message;

    public DowngradeMessageCheck ifTLSv12() {
        downgrade_message = downgrade_tls12_message;
        return this;
    }

    public DowngradeMessageCheck ifBelowTLSv12() {
        downgrade_message = downgrade_tls11_and_below_message;
        return this;
    }

    @Override
    public Check run() {
        if (context.getServerHello() == null) {
            return this;
        }

        ServerHello hello = StructFactory.getDefault().parser().parseServerHello(
                context.getServerHello().getBody());

        byte[] bytes = hello.getRandom().getBytes();
        int i = bytes.length - downgrade_message.length;
        int j = 0;
        while (j < downgrade_message.length) {
            if (bytes[i++] != downgrade_message[j++]) {
                return this;
            }
        }

        failed = false;

        return this;
    }

    @Override
    public String name() {
        return "downgrade message received in ServerHello.random";
    }
}
