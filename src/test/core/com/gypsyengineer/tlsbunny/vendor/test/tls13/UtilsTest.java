package com.gypsyengineer.tlsbunny.vendor.test.tls13;

import com.gypsyengineer.tlsbunny.utils.Achtung;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.SimpleOutput;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.fail;

public class UtilsTest {

    private static boolean value;

    @BeforeClass
    public static void setUp() {
        value = SimpleOutput.printOnlyAchtung();
        SimpleOutput.printAll();
    }

    @Test
    public void asanMessageFound() {
        try (Output output = new SimpleOutput()) {
            output.info("one");
            output.info("ERROR: AddressSanitizer: this is a test!");
            output.achtung("warning");
            Utils.checkForASanFindings(output);
            fail("expected achtung");
        } catch (Achtung e) {
            System.out.println("expected exception: " + e);
        }
    }

    @Test
    public void asanMessageNotFound() {
        try (Output output = new SimpleOutput()) {
            output.info("one");
            output.info("two");
            output.achtung("AddressSanitizer");
            Utils.checkForASanFindings(output);
        }
    }

    @AfterClass
    public static void tearDown() {
        SimpleOutput.printOnlyAchtung(value);
    }
}
