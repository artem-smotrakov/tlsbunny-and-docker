package com.gypsyengineer.tlsbunny.vendor.test.tls13;

import com.gypsyengineer.tlsbunny.output.InputStreamOutput;

public class AddressSanitizerWatcherOutput extends InputStreamOutput {

    synchronized public AddressSanitizerWatcherOutput update() {
        if (initialized()) {
            String text = read();
            if (text.contains("ERROR: AddressSanitizer:")) {
                important(text);
            } else {
                info(text);
            }
        }

        return this;
    }

}