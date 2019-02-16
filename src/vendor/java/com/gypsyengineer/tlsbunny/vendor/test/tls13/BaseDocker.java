package com.gypsyengineer.tlsbunny.vendor.test.tls13;

import com.gypsyengineer.tlsbunny.output.InputStreamOutput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class BaseDocker {

    protected static final String container_report_directory = "/var/reports";

    protected static final String remove_container_template =
            "docker container rm %s";

    protected final String containerName = String.format("%s_%d",
            this.getClass().getSimpleName().toLowerCase(), System.currentTimeMillis());

    protected InputStreamOutput output = new InputStreamOutput();

    protected Map<String, String> dockerEnv = Collections.synchronizedMap(new HashMap<>());

    public BaseDocker dockerEnv(String name, String value) {
        dockerEnv.put(name, value);
        return this;
    }

    public static void createReportDirectory(String directory) {
        try {
            Files.createDirectories(Paths.get(directory));
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
