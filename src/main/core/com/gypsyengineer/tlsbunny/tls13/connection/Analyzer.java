package com.gypsyengineer.tlsbunny.tls13.connection;

import com.gypsyengineer.tlsbunny.utils.Output;

public interface Analyzer {
    Analyzer set(Output output);
    Analyzer add(Engine... engines);
    Analyzer run();
    Engine[] engines();
}
