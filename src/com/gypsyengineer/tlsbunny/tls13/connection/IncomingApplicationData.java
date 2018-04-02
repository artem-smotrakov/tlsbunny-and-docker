package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.struct.ContentType;

public class IncomingApplicationData extends AbstractAction {

    @Override
    public String name() {
        return "application data";
    }

    @Override
    public Action run() throws Exception {
        byte[] data = processEncrypted(
                context.applicationDataDecryptor, ContentType.application_data);
        output.info("received data (%d bytes)%n%s", data.length, new String(data));

        return this;
    }

}
