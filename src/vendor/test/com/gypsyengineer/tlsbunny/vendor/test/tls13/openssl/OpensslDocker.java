package com.gypsyengineer.tlsbunny.vendor.test.tls13.openssl;

import com.gypsyengineer.tlsbunny.vendor.test.tls13.Utils;
import com.gypsyengineer.tlsbunny.utils.Output;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class OpensslDocker {

    protected static final String remove_container_template =
            "docker container rm %s";

    protected static final String host_report_directory = String.format(
            "%s/openssl_report", System.getProperty("user.dir"));

    protected static final String image = System.getProperty(
            "tlsbunny.openssl.docker.image",
            "artemsmotrakov/tlsbunny_openssl_tls13");

    protected static final String container_report_directory = "/var/reports";

    protected final String containerName = String.format("%s_%d",
            this.getClass().getSimpleName().toLowerCase(), System.currentTimeMillis());
    protected final Output output = new Output(this.getClass().getSimpleName());
    protected Map<String, String> dockerEnv = Collections.synchronizedMap(new HashMap<>());

    public OpensslDocker dockerEnv(String name, String value) {
        dockerEnv.put(name, value);
        return this;
    }

    protected void createReportDirectory() {
        try {
            Files.createDirectories(Paths.get(host_report_directory));
        } catch (IOException e) {
            throw whatTheHell("could not create a directory for reports!", e);
        }
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
