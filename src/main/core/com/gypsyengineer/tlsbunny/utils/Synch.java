package com.gypsyengineer.tlsbunny.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class Synch {

    private static final boolean printToFile;
    private static final String dirName;
    static {
        printToFile = Boolean.valueOf(System.getProperty("tlsbunny.output.to.file", "false"));
        dirName = String.format("logs/%s",
                new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()));
        boolean success = new File(dirName).mkdirs();
        if (!success) {
            throw whatTheHell("could not create directories");
        }
    }

    private Writer createFileWriter(String fileName) {
        if (printToFile) {
            try {
                return new BufferedWriter(new FileWriter(fileName));
            } catch (IOException e) {
                throw whatTheHell("unexpected exception", e);
            }
        }

        return null;
    }
}
