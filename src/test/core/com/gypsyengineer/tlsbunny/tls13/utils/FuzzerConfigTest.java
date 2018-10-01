package com.gypsyengineer.tlsbunny.tls13.utils;

import com.gypsyengineer.tlsbunny.utils.Config;
import com.gypsyengineer.tlsbunny.utils.SystemPropertiesConfig;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class FuzzerConfigTest {

    @Test
    public void immutableMainConfig() {
        Config mainConfig = SystemPropertiesConfig.load();
        FuzzerConfig firstConfig = new FuzzerConfig(mainConfig);
        FuzzerConfig secondConfig = new FuzzerConfig(mainConfig);

        assertEquals(firstConfig, secondConfig);

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
    }
}
