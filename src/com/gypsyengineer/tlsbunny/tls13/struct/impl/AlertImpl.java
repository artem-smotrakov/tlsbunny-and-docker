package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import java.io.IOException;
import java.util.Objects;
import com.gypsyengineer.tlsbunny.tls13.struct.Alert;
import com.gypsyengineer.tlsbunny.tls13.struct.AlertDescription;
import com.gypsyengineer.tlsbunny.tls13.struct.AlertLevel;

public class AlertImpl implements Alert {

    private static final int ENCODING_LENGTH = 2;

    private AlertLevel level;
    private AlertDescription description;

    AlertImpl() {
        this(AlertLevel.fatal, AlertDescription.INTERNAL_ERROR);
    }

    public AlertImpl(AlertLevel level, AlertDescription description) {
        this.level = level;
        this.description = description;
    }

    @Override
    public int encodingLength() {
        return ENCODING_LENGTH;
    }

    @Override
    public byte[] encoding() throws IOException {
        return new byte[] { level.getCode(), description.getCode() };
    }

    @Override
    public AlertLevel getLevel() {
        return level;
    }

    @Override
    public void setLevel(AlertLevel level) {
        this.level = level;
    }

    @Override
    public AlertDescription getDescription() {
        return description;
    }

    @Override
    public void setDescription(AlertDescription description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.level);
        hash = 79 * hash + Objects.hashCode(this.description);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AlertImpl other = (AlertImpl) obj;
        if (!Objects.equals(this.level, other.level)) {
            return false;
        }
        return Objects.equals(this.description, other.description);
    }

}
