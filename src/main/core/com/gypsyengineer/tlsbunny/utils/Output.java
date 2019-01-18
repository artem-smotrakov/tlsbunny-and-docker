package com.gypsyengineer.tlsbunny.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

public class Output implements AutoCloseable {

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

    private static boolean onlyAchtung = Boolean.valueOf(
            System.getProperty("tlsbunny.output.only.achtung", "false"));

    private static final String ansi_red ;
    private static final String ansi_reset;
    static {
        boolean enableHighlighting = Boolean.valueOf(
                System.getProperty("tlsbunny.output.enable.highlighting", "true"));
        if (enableHighlighting) {
            System.out.println("Output: enable highlighting");
            ansi_red = "\u001B[31m";
            ansi_reset = "\u001B[0m";
        } else {
            System.out.println("Output: disable highlighting");
            ansi_red = "";
            ansi_reset = "";
        }
    }

    private static final String tlsbunny = "[tlsbunny] ";
    private static final int indent_step = 4;

    private final List<String> strings = new ArrayList<>();
    private String prefix = tlsbunny;
    private int index = 0;
    private String indent = "";
    private final List<OutputListener> listeners = Collections.synchronizedList(new ArrayList<>());
    private final String fileName;
    private Writer fileWriter;

    public static void printOnlyAchtung(boolean onlyAchtung) {
        synchronized (Output.class) {
            Output.onlyAchtung = onlyAchtung;
        }
    }

    public static boolean printOnlyAchtung() {
        synchronized (Output.class) {
            return Output.onlyAchtung;
        }
    }

    public static void printAll() {
        synchronized (Output.class) {
            onlyAchtung = false;
        }
    }

    public Output() {
        this("", "output");
    }

    public Output(String prefix) {
        this(prefix, "output");
    }

    public Output(String prefix, String label) {
        this.fileName = String.format("%s/%s_%s_%d.log",
                dirName, prefix, label, System.currentTimeMillis());
        prefix(prefix);
    }

    private Writer createFileWriter() {
        if (printToFile) {
            try {
                return new BufferedWriter(new FileWriter(fileName));
            } catch (IOException e) {
                throw whatTheHell("unexpected exception", e);
            }
        }

        return null;
    }

    public Output add(OutputListener listener) {
        listeners.add(listener);
        return this;
    }

    synchronized public void increaseIndent() {
        int indentLength = indent.length() + indent_step;
        indent = new String(new char[indentLength]).replace('\0', ' ');
    }

    synchronized public void decreaseIndent() {
        int indentLength = indent.length() - indent_step;

        if (indentLength < 0) {
            indentLength = 0;
        }

        indent = new String(new char[indentLength]).replace('\0', ' ');
    }

    synchronized private void printf(String format, Object... params) {
        strings.add(String.format(format, params));
    }

    synchronized public void prefix(String prefix) {
        if (prefix != null && !prefix.isEmpty()) {
            this.prefix = String.format("%s%s: ", tlsbunny, prefix);
        } else {
            this.prefix = String.format("%s: ", tlsbunny);
        }
    }

    synchronized public void info(String format, Object... values) {
        String text = String.format(format, values);
        String[] lines = text.split("\\r?\\n");
        for (OutputListener listener : listeners) {
            listener.receivedInfo(lines);
        }
        if (onlyAchtung) {
            return;
        }
        for (String line : lines) {
            printf("%s%s%s%n", prefix, indent, line);
        }
    }

    synchronized public void info(String message, Throwable e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(baos, true));
        info(String.format("%s%n%s", message, new String(baos.toByteArray())));
    }

    synchronized public void achtung(String format, Object... values) {
        String line = String.format(format, values);
        for (OutputListener listener : listeners) {
            listener.receivedAchtung(line);
        }
        printf("%s%sachtung: %s%s%n",
                prefix, ansi_red, line, ansi_reset);
    }

    synchronized public void achtung(String message, Throwable e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(baos, true));
        achtung(String.format("%s%n%s", message, new String(baos.toByteArray())));
    }

    synchronized public void add(Output output) {
        strings.addAll(output.strings);
    }

    synchronized public List<String> strings() {
        return Collections.unmodifiableList(strings);
    }

    synchronized public boolean contains(String phrase) {
        for (String string : strings) {
            if (string.contains(phrase)) {
                return true;
            }
        }

        return false;
    }

    synchronized public void flush() {
        // create a FileWriter only if we have something to print
        // to avoid creating empty files
        if (printToFile && strings.size() > 0 && fileWriter == null) {
            fileWriter = createFileWriter();
        }

        while (index < strings.size()) {
            String string = strings.get(index);
            System.out.print(string);

            // print to a file if required
            if (fileWriter != null) {
                try {
                    fileWriter.append(string);
                } catch (IOException e) {
                    throw whatTheHell("could not write to file writer", e);
                }
            }

            index++;
        }

        if (fileWriter != null) {
            try {
                fileWriter.flush();
            } catch (IOException e) {
                throw whatTheHell("could not flush to file writer", e);
            }
        }
    }

    @Override
    synchronized public void close() {
        flush();

        if (fileWriter != null) {
            try {
                fileWriter.close();
            } catch (IOException e) {
                throw whatTheHell("could not close file writer", e);
            }
        }
    }

}
