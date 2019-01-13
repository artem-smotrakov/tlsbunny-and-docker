package com.gypsyengineer.tlsbunny.tls13.utils;

import com.gypsyengineer.tlsbunny.tls13.connection.check.AlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import static com.gypsyengineer.tlsbunny.TestUtils.expectUnsupported;
import static com.gypsyengineer.tlsbunny.tls13.utils.FuzzerConfigUpdater.fuzzerConfigUpdater;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class FuzzerConfigTest {

    @Test
    public void main() {
        Config mainConfig = SystemPropertiesConfig.load();
        Config mainConfig2 = SystemPropertiesConfig.load();
        assertEquals(mainConfig, mainConfig2);

        FuzzerConfig firstConfig = new FuzzerConfig(mainConfig);
        FuzzerConfig secondConfig = new FuzzerConfig(mainConfig);
        secondConfig.set(mainConfig2);

        assertEquals(firstConfig, secondConfig);
        assertEquals(firstConfig.hashCode(), secondConfig.hashCode());
        assertEquals(firstConfig, firstConfig);
        assertNotEquals(firstConfig, "wrong");

        firstConfig.startTest(1);
        firstConfig.endTest(10);
        secondConfig.startTest(5);
        secondConfig.endTest(15);

        assertEquals(1, firstConfig.startTest());
        assertEquals(10, firstConfig.endTest());

        firstConfig.startTest(2);
        firstConfig.endTest(12);

        assertEquals(5, secondConfig.startTest());
        assertEquals(15, secondConfig.endTest());

        assertNotEquals(firstConfig, secondConfig);

        assertTrue(firstConfig.minRatio() == mainConfig.minRatio());
        assertTrue(firstConfig.maxRatio() == mainConfig.maxRatio());
        assertTrue(firstConfig.threads() == mainConfig.threads());
        assertTrue(firstConfig.parts() == mainConfig.parts());
        assertTrue(firstConfig.readTimeout() == mainConfig.readTimeout());

        assertEquals(
                firstConfig.clientCertificate(),
                SystemPropertiesConfig.default_client_certificate);
        assertEquals(
                firstConfig.clientKey(),
                SystemPropertiesConfig.default_client_key);
        assertEquals(
                firstConfig.serverCertificate(),
                SystemPropertiesConfig.default_server_certificate);
        assertEquals(
                firstConfig.serverKey(),
                SystemPropertiesConfig.default_server_key);

        assertTrue(firstConfig.targetFilter().isEmpty());

        assertEquals(firstConfig.serverKey("test").serverKey(), "test");
        assertEquals(firstConfig.serverCertificate("test").serverCertificate(), "test");
        assertEquals(firstConfig.clientKey("test").clientKey(), "test");
        assertEquals(firstConfig.clientCertificate("test").clientCertificate(), "test");

        assertTrue(firstConfig.minRatio(0.11).minRatio() == 0.11);
        assertTrue(firstConfig.maxRatio(0.11).maxRatio() == 0.11);
    }

    @Test
    public void assign() {
        Config mainConfig = SystemPropertiesConfig.load();
        FuzzerConfig[] configs = {
                new FuzzerConfig(mainConfig),
                new FuzzerConfig(mainConfig),
                new FuzzerConfig(mainConfig),
                new FuzzerConfig(mainConfig),
                new FuzzerConfig(mainConfig),
        };

        for (Config config : configs) {
            assertNotEquals(42, config.port());
            assertNotEquals("test", config.host());
            assertNotEquals(0.111, config.minRatio());
            assertNotEquals(0.222, config.maxRatio());
            assertNotEquals(1000, config.readTimeout());
            assertNotEquals("a/b/cert.pem", config.serverCertificate());
            assertNotEquals("a/b/key.pem", config.serverKey());
            assertNotEquals("c/d/cert.pem", config.clientCertificate());
            assertNotEquals("c/d/key.pem", config.clientKey());
            assertNotEquals(123, config.startTest());
            assertNotEquals(234, config.endTest());
            assertNotEquals(42, config.parts());
        }

        FuzzerConfigUpdater updater = fuzzerConfigUpdater(configs);
        updater.port(42);
        updater.host("test");
        updater.minRatio(0.111);
        updater.maxRatio(0.222);
        updater.readTimeout(1000);
        updater.serverCertificate("a/b/cert.pem");
        updater.serverKey("a/b/key.pem");
        updater.clientCertificate("c/d/cert.pem");
        updater.clientKey("c/d/key.pem");
        updater.startTest(123);
        updater.endTest(234);
        updater.parts(42);

        for (Config config : configs) {
            assertEquals(42, config.port());
            assertEquals("test", config.host());
            assertTrue(0.111 == config.minRatio());
            assertTrue(0.222 == config.maxRatio());
            assertEquals(1000, config.readTimeout());
            assertEquals("a/b/cert.pem", config.serverCertificate());
            assertEquals("a/b/key.pem", config.serverKey());
            assertEquals("c/d/cert.pem", config.clientCertificate());
            assertEquals("c/d/key.pem", config.clientKey());
            assertEquals(123, config.startTest());
            assertEquals(234, config.endTest());
            assertEquals(42, config.parts());
        }
    }

    @Test
    public void unsupported() throws Exception {
        FuzzerConfigUpdater updater = fuzzerConfigUpdater();
        expectUnsupported(() -> updater.port());
        expectUnsupported(() -> updater.parts());
        expectUnsupported(() -> updater.host());
        expectUnsupported(() -> updater.minRatio());
        expectUnsupported(() -> updater.maxRatio());
        expectUnsupported(() -> updater.serverKey());
        expectUnsupported(() -> updater.serverCertificate());
        expectUnsupported(() -> updater.clientKey());
        expectUnsupported(() -> updater.clientCertificate());
        expectUnsupported(() -> updater.startTest());
        expectUnsupported(() -> updater.endTest());
        expectUnsupported(() -> updater.readTimeout());
    }

}
