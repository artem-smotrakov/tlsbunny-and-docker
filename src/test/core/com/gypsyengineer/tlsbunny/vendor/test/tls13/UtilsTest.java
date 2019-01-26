package com.gypsyengineer.tlsbunny.vendor.test.tls13;

import com.gypsyengineer.tlsbunny.utils.Achtung;
import com.gypsyengineer.tlsbunny.utils.Output;
import com.gypsyengineer.tlsbunny.utils.OutputStorage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.fail;

public class UtilsTest {

    private static boolean value;

    @Test
    public void asanMessageFound() {
        try (Output output = new OutputStorage()) {
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
        try (Output output = new OutputStorage()) {
            output.info("one");
            output.info("two");
            output.achtung("AddressSanitizer");
            Utils.checkForASanFindings(output);
        }
    }

}
