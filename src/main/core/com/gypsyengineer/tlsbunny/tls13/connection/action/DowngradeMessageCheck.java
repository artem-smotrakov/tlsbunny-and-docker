package com.gypsyengineer.tlsbunny.tls13.connection.action;

import com.gypsyengineer.tlsbunny.tls13.connection.check.AbstractCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.tls13.struct.ProtocolVersion;
import com.gypsyengineer.tlsbunny.tls13.struct.ServerHello;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class DowngradeMessageCheck extends AbstractCheck {

    private static final byte[] downgrade_tls12_message = new byte[] {
            0x44, 0x4F, 0x57, 0x4E, 0x47, 0x52, 0x44, 0x01
    };

    private static final byte[] downgrade_tls11_and_below_message = new byte[] {
            0x44, 0x4F, 0x57, 0x4E, 0x47, 0x52, 0x44, 0x00
    };

    private byte[] downgrade_message = null;

    public DowngradeMessageCheck set(ProtocolVersion version) {
        if (version == null) {
            throw whatTheHell("version is null!");
        }

        if (ProtocolVersion.TLSv13.equals(version)) {
            ifNoDowngrade();
        } else if (ProtocolVersion.TLSv12.equals(version)) {
            ifTLSv12();
        } else {
            ifBelowTLSv12();
        }

        return this;
    }

    public DowngradeMessageCheck ifTLSv12() {
        downgrade_message = downgrade_tls12_message;
        return this;
    }

    public DowngradeMessageCheck ifBelowTLSv12() {
        downgrade_message = downgrade_tls11_and_below_message;
        return this;
    }

    public DowngradeMessageCheck ifNoDowngrade() {
        downgrade_message = null;
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

        if (downgrade_message == null) {
            failed = lastBytesEquals(bytes, downgrade_tls12_message)
                        || lastBytesEquals(bytes, downgrade_tls11_and_below_message);
        } else {
            failed = !lastBytesEquals(bytes, downgrade_message);
        }

        return this;
    }

    @Override
    public String name() {
        return "downgrade message in ServerHello.random";
    }

    private static boolean lastBytesEquals(byte[] bytes, byte[] message) {
        int i = bytes.length - message.length;
        int j = 0;
        while (j < message.length) {
            if (bytes[i++] != message[j++]) {
                return false;
            }
        }

        return true;
    }
}
