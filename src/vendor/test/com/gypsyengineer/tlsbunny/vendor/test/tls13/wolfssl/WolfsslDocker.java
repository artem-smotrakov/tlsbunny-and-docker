package com.gypsyengineer.tlsbunny.vendor.test.tls13.wolfssl;

import com.gypsyengineer.tlsbunny.utils.SimpleOutput;
import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class WolfsslDocker {

    protected static final String remove_container_template =
            "docker container rm %s";

    protected static final String image = System.getProperty(
            "tlsbunny.wolfssl.docker.image",
            "artemsmotrakov/tlsbunny_wolfssl_tls13");

    protected final String containerName = String.format("%s_%d",
            this.getClass().getSimpleName().toLowerCase(), System.currentTimeMillis());

    protected final SimpleOutput output = new SimpleOutput(this.getClass().getSimpleName());

    protected Map<String, String> dockerEnv = Collections.synchronizedMap(new HashMap<>());

    public WolfsslDocker dockerEnv(String name, String value) {
        dockerEnv.put(name, value);
        return this;
    }

    protected boolean containerRunning() {
        try {
            List<String> command = List.of(
                    "/bin/bash",
                    "-c",
                    String.format("docker container ps | grep %s", containerName)
            );
            return Utils.exec(output, command).waitFor() == 0;
        } catch (InterruptedException | IOException e) {
            throw whatTheHell("unexpected exception", e);
        }
    }
}
