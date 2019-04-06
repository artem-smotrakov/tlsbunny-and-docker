package com.gypsyengineer.tlsbunny.vendor.test.tls13;

import com.gypsyengineer.tlsbunny.output.InputStreamOutput;

public class AddressSanitizerWatcherOutput extends InputStreamOutput {

    @Override
    public synchronized AddressSanitizerWatcherOutput update() {
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