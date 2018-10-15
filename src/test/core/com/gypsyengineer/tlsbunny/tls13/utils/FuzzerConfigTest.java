package com.gypsyengineer.tlsbunny.tls13.utils;

import com.gypsyengineer.tlsbunny.tls13.connection.check.AlertCheck;
import com.gypsyengineer.tlsbunny.tls13.connection.check.Check;
import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
                SystemPropertiesConfig.DEFAULT_CLIENT_CERTIFICATE);
        assertEquals(
                firstConfig.clientKey(),
                SystemPropertiesConfig.DEFAULT_CLIENT_KEY);
        assertEquals(
                firstConfig.serverCertificate(),
                SystemPropertiesConfig.DEFAULT_SERVER_CERTIFICATE);
        assertEquals(
                firstConfig.serverKey(),
                SystemPropertiesConfig.DEFAULT_SERVER_KEY);

        assertTrue(firstConfig.targetFilter().isEmpty());

        assertArrayEquals(
                firstConfig.set(new AlertCheck()).checks(),
                new Check[] { new AlertCheck()} );

        assertEquals(firstConfig.serverKey("test").serverKey(), "test");
        assertEquals(firstConfig.serverCertificate("test").serverCertificate(), "test");
        assertEquals(firstConfig.clientKey("test").clientKey(), "test");
        assertEquals(firstConfig.clientCertificate("test").clientCertificate(), "test");

        assertTrue(firstConfig.minRatio(0.11).minRatio() == 0.11);
        assertTrue(firstConfig.maxRatio(0.11).maxRatio() == 0.11);
    }
}
