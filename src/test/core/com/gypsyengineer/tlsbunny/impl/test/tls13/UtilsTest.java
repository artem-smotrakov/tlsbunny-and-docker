package com.gypsyengineer.tlsbunny.impl.test.tls13;

import com.gypsyengineer.tlsbunny.utils.Achtung;
import com.gypsyengineer.tlsbunny.utils.Output;
import org.junit.Test;

import static org.junit.Assert.fail;

public class UtilsTest {

    @Test
    public void asanMessageFound() {
        try (Output output = new Output()) {
            output.info("one");
            output.info("ERROR: AddressSanitizer: buffer overflow");
            output.achtung("warning");
            Utils.checkForASanFindings(output);
            fail("expected achtung");
        } catch (Achtung e) {
            System.out.println("expected exception: " + e);
        }
    }

    @Test
    public void asanMessageNotFound() {
        try (Output output = new Output()) {
            output.info("one");
            output.info("two");
            output.achtung("warning");
            Utils.checkForASanFindings(output);
        }
    }
}
