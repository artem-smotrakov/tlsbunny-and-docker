package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.utils.Output;

public interface Analyzer {

    Analyzer NOTHING = null;

    Analyzer set(Output output);
    Analyzer add(String label, Context context);
    Analyzer add(String label, Output output);
    Analyzer run();
}
