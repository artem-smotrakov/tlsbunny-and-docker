package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Struct;

public interface Alert extends Struct {

    AlertDescription getDescription();
    AlertLevel getLevel();
    void setDescription(AlertDescription description);
    void setLevel(AlertLevel level);
}
