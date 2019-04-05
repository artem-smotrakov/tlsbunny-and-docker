package com.gypsyengineer.tlsbunny.vendor.test.tls13;

import com.gypsyengineer.tlsbunny.utils.Achtung;
import com.gypsyengineer.tlsbunny.output.Output;
import org.junit.Test;

import static org.junit.Assert.fail;

public class UtilsTest {

    private static boolean value;

    @Test
    public void asanMessageFound() {
        try (Output output = Output.standard()) {
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
        try (Output output = Output.standard()) {
            output.info("one");
            output.info("two");
            output.achtung("AddressSanitizer");
            Utils.checkForASanFindings(output);
        }
    }

}
