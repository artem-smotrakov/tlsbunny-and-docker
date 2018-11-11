package com.gypsyengineer.tlsbunny;

import com.gypsyengineer.tlsbunny.fuzzer.AbstractFlipFuzzer;
import com.gypsyengineer.tlsbunny.fuzzer.Fuzzer;
import com.gypsyengineer.tlsbunny.tls.Random;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.connection.Analyzer;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.struct.*;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.WhatTheHell;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class TestUtils {

    public interface TestAction {
        void run() throws Exception;
    }

    public static void expectIllegalState(TestAction test) throws Exception {
        try {
            test.run();
            fail("expected an exception");
        } catch (IllegalStateException e) {
            // good
        }
    }

    public static void expectWhatTheHell(TestAction test) throws Exception {
        try {
            test.run();
            fail("expected an exception");
        } catch (WhatTheHell e) {
            // good
        }
    }

    public static void expectUnsupported(TestAction test) throws Exception {
        try {
            test.run();
            fail("expected an exception");
        } catch (UnsupportedOperationException e) {
            // good
        }
    }

    public static ClientHello createClientHello() {
        StructFactory factory = StructFactory.getDefault();
        return factory.createClientHello(
                ProtocolVersion.TLSv13,
                Random.create(),
                new byte[32],
                List.of(CipherSuite.TLS_AES_128_GCM_SHA256),
                List.of(CompressionMethod.None),
                List.of(factory.createExtension(
                        ExtensionType.supported_versions,
                        new byte[64])));
    }

    public static Extension createExtension() {
        return StructFactory.getDefault().createExtension(
                ExtensionType.supported_versions, new byte[42]);
    }

    // check if methods without parameters throw UnsupportedOperationException
    // TODO: test all methods
    public static void expectUnsupportedMethods(
            Object object, List<String> excluded) throws Exception {

        for (Method method : object.getClass().getDeclaredMethods()) {
            if (method.getName().startsWith("$")) {
                continue;
            }

            if (method.isSynthetic()) {
                continue;
            }

            if (method.getParameterCount() > 0) {
                continue;
            }

            try {
                System.out.printf("call %s()%n", method.getName());
                method.invoke(object);
                if (excluded.contains(method.getName())) {
                    continue;
                }
                fail("expected UnsupportedOperationException");
            } catch (UnsupportedOperationException e) {
                if (!excluded.contains(method.getName())) {
                    fail("unexpected UnsupportedOperationException");
                }
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof UnsupportedOperationException == false) {
                    fail("unexpected cause");
                }
            }
        }
    }

    public static void expectUnsupportedMethods(
            Object object, String... excluded) throws Exception {

        expectUnsupportedMethods(object, List.of(excluded));
    }

    public static class FakeFlipFuzzer extends AbstractFlipFuzzer {

        @Override
        protected byte[] fuzzImpl(byte[] array) {
            // do nothing
            return array;
        }
    }

    public static class FakeVectorFuzzer implements Fuzzer<Vector<Byte>> {

        private long test = 0;

        @Override
        public boolean canFuzz() {
            return true;
        }

        @Override
        public Vector fuzz(Vector object) {
            return object;
        }

        @Override
        public void moveOn() {
            test++;
        }

        @Override
        public long currentTest() {
            return test;
        }

        @Override
        public void currentTest(long test) {
            this.test = test;
        }

        @Override
        public FakeVectorFuzzer set(Output output) {
            return this;
        }

        @Override
        public Output output() {
            return new Output();
        }
    }

    public static class FakeCompressionMethodFuzzer implements Fuzzer<Vector<CompressionMethod>> {

        private long test = 0;

        @Override
        public boolean canFuzz() {
            return true;
        }

        @Override
        public Vector<CompressionMethod> fuzz(Vector<CompressionMethod> object) {
            return object;
        }

        @Override
        public void moveOn() {
            test++;
        }

        @Override
        public long currentTest() {
            return test;
        }

        @Override
        public void currentTest(long test) {
            this.test = test;
        }

        @Override
        public FakeCompressionMethodFuzzer set(Output output) {
            return this;
        }

        @Override
        public Output output() {
            return new Output();
        }
    }

    public static class FakeCipherSuitesFuzzer implements Fuzzer<Vector<CipherSuite>> {

        private long test = 0;

        @Override
        public boolean canFuzz() {
            return true;
        }

        @Override
        public Vector<CipherSuite> fuzz(Vector<CipherSuite> object) {
            return object;
        }

        @Override
        public void moveOn() {
            test++;
        }

        @Override
        public long currentTest() {
            return test;
        }

        @Override
        public void currentTest(long test) {
            this.test = test;
        }

        @Override
        public FakeCipherSuitesFuzzer set(Output output) {
            return this;
        }

        @Override
        public Output output() {
            return new Output();
        }
    }

    public static class FakeExtensionVectorFuzzer implements Fuzzer<Vector<Extension>> {

        private long test = 0;

        @Override
        public boolean canFuzz() {
            return true;
        }

        @Override
        public Vector<Extension> fuzz(Vector<Extension> object) {
            return object;
        }

        @Override
        public void moveOn() {
            test++;
        }

        @Override
        public long currentTest() {
            return test;
        }

        @Override
        public void currentTest(long test) {
            this.test = test;
        }

        @Override
        public FakeExtensionVectorFuzzer set(Output output) {
            return this;
        }

        @Override
        public Output output() {
            return new Output();
        }
    }

    // the fuzzer just sets all bytes of encoding to zeroes
    public static class ZeroFuzzer extends AbstractFlipFuzzer {

        @Override
        protected byte[] fuzzImpl(byte[] array) {
            return new byte[array.length];
        }

    }

    public static class FakeTestAnalyzer implements Analyzer {

        private Output output;
        private final List<Engine> engines = new ArrayList<>();

        @Override
        public Analyzer set(Output output) {
            this.output = output;
            return this;
        }

        @Override
        public Analyzer add(Engine... engines) {
            this.engines.addAll(List.of(engines));
            return this;
        }

        @Override
        public Analyzer run() {
            output.info("run analyzer");
            return this;
        }

        @Override
        public Engine[] engines() {
            return engines.toArray(new Engine[0]);
        }
    }
}
