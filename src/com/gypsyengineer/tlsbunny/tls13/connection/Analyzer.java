package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.tls13.handshake.Context;
import com.gypsyengineer.tlsbunny.utils.Output;

public interface Analyzer {

    Analyzer set(Output output);
    Analyzer add(Context context);
    Analyzer run();
}
