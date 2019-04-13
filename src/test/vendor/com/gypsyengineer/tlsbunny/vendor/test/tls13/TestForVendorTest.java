package com.gypsyengineer.tlsbunny.vendor.test.tls13;

import com.gypsyengineer.tlsbunny.TestUtils;
import com.gypsyengineer.tlsbunny.tls13.connection.BaseEngineFactory;
import com.gypsyengineer.tlsbunny.tls13.connection.Engine;
import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Side;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.IncomingMessages;
import com.gypsyengineer.tlsbunny.tls13.connection.action.composite.OutgoingMainServerFlight;
import com.gypsyengineer.tlsbunny.tls13.connection.action.simple.*;
import com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzerConfigs;
import com.gypsyengineer.tlsbunny.tls13.server.HttpsServer;
import com.gypsyengineer.tlsbunny.tls13.server.SingleThreadServer;
import com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfig;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import java.nio.file.Path;
import java.util.List;

import static com.gypsyengineer.tlsbunny.fuzzer.ByteFlipFuzzer.newByteFlipFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.client.HttpsClient.httpsClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzer.deepHandshakeFuzzer;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.DeepHandshakeFuzzyClient.deepHandshakeFuzzyClient;
import static com.gypsyengineer.tlsbunny.tls13.fuzzer.MultiConfigClient.multiConfigClient;
import static com.gypsyengineer.tlsbunny.tls13.server.HttpsServer.httpsServer;
import static com.gypsyengineer.tlsbunny.tls13.struct.NamedGroup.secp256r1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestForVendorTest {

    @Test
    public void test() throws Exception {
        Config mainConfig = SystemPropertiesConfig.load();
        HttpsServer server = httpsServer();
        try (server) {
            server.set(mainConfig).set(secp256r1).neverStop().start();

            Utils.waitStart(server);

            new VendorTest()
                    .set(multiConfigClient()
                            .from(deepHandshakeFuzzyClient().from(httpsClient()))
                            .configs(DeepHandshakeFuzzerConfigs.noClientAuth(mainConfig)))
                    .set(server)
                    .run();
        }

        Utils.waitStop(server);
    }

    @Test
    public void addressSanitizer() throws Exception {
        Config serverConfig = SystemPropertiesConfig.load();
        Config mainConfig = SystemPropertiesConfig.load();

        SingleThreadServer server = new SingleThreadServer()
                .set(serverConfig)
                .set(new EngineFactoryImpl()
                        .set(serverConfig));

        FuzzerConfig fuzzerConfig = new FuzzerConfig(mainConfig)
                .factory(deepHandshakeFuzzer().fuzzer(newByteFlipFuzzer()))
                .total(1);

        Path dir = TestUtils.createTempDirectory();
        VendorTest test = new VendorTest().logs(dir.toString()).label("vendor_test");
        try (server) {
            server.start();
            Utils.waitStart(server);

            test.set(multiConfigClient()
                            .from(deepHandshakeFuzzyClient().from(httpsClient()))
                            .configs(fuzzerConfig))
                    .set(server)
                    .run();

            server.stop();
            Utils.waitStop(server);
        } finally {
            List<String> files = TestUtils.findFiles(dir, "vendor_test");
            assertEquals(1, files.size());
            String filename = files.get(0);
            assertTrue(TestUtils.searchInFile(filename, "Looks like AddressSanitizer found something"));
            assertTrue(TestUtils.searchInFile(filename, "ERROR: AddressSanitizer: heap-use-after-free"));

            files = TestUtils.findFiles(dir, "oops_");
            assertEquals(1, files.size());
            filename = files.get(0);
            assertTrue(TestUtils.searchInFile(filename, "Looks like AddressSanitizer found something"));
            assertTrue(TestUtils.searchInFile(filename, "ERROR: AddressSanitizer: heap-use-after-free"));

            TestUtils.removeDirectory(dir);
        }
    }

    private static class EngineFactoryImpl extends BaseEngineFactory {

        private int counter = 0;

        public EngineFactoryImpl set(Config config) {
            this.config = config;
            return this;
        }

        @Override
        protected Engine createImpl() throws Exception {
            counter++;
            if (counter <= 1) {
                return fullHandshake();
            } else {
                return printError();
            }
        }

        private Engine fullHandshake() throws Exception {
            return Engine.init()
                    .set(structFactory)
                    .set(output)

                    .receive(new IncomingData())

                    // process ClientHello
                    .loop(context -> !context.hasFirstClientHello() && !context.hasAlert())
                    .receive(() -> new IncomingMessages(Side.server))

                    // send messages
                    .send(new OutgoingMainServerFlight()
                            .apply(config))

                    // receive Finished and application data
                    .loop(context -> !context.receivedApplicationData() && !context.hasAlert())
                    .receive(() -> new IncomingMessages(Side.server))

                    // send application data
                    .run(new PreparingHttpResponse())
                    .run(new WrappingApplicationDataIntoTLSCiphertext())
                    .send(new OutgoingData());
        }

        private Engine printError() throws Exception {
            return Engine.init()
                    .set(structFactory)
                    .set(output)

                    // receive an invalid CCS
                    .receive(new IncomingData())

                    // print a error
                    .run(new PrintError());
        }
    }

    private static class PrintError extends AbstractAction<PrintError> {

        @Override
        public Action run() {
            output.info(
                    "==9901==ERROR: AddressSanitizer: heap-use-after-free " +
                            "on address 0x60700000dfb5 at pc 0x45917b bp 0x7fff4490c700 " +
                            "sp 0x7fff4490c6f8\n" +
                            "READ of size 1 at 0x60700000dfb5 thread T0");
            return this;
        }

        @Override
        public String name() {
            return "printing ASan error";
        }
    }

}
