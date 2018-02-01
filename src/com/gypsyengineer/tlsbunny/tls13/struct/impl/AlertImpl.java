package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import com.gypsyengineer.tlsbunny.tls.Struct;
import com.gypsyengineer.tlsbunny.tls13.struct.Alert;

public class AlertImpl implements Alert {

    private static final int ENCODING_LENGTH = 2;

    private AlertLevelImpl level;
    private AlertDescriptionImpl description;

    AlertImpl() {
        this(AlertLevelImpl.FATAL, AlertDescriptionImpl.INTERNAL_ERROR);
    }

    public AlertImpl(AlertLevelImpl level, AlertDescriptionImpl description) {
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
    public AlertLevelImpl getLevel() {
        return level;
    }

    @Override
    public void setLevel(AlertLevelImpl level) {
        this.level = level;
    }

    @Override
    public AlertDescriptionImpl getDescription() {
        return description;
    }

    @Override
    public void setDescription(AlertDescriptionImpl description) {
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

    public static AlertImpl parse(byte[] bytes) {
        return parse(ByteBuffer.wrap(bytes));
    }
    
    public static AlertImpl parse(ByteBuffer buffer) {
        AlertLevelImpl level = AlertLevelImpl.parse(buffer);
        AlertDescriptionImpl description = AlertDescriptionImpl.parse(buffer);

        return new AlertImpl(level, description);
    }

}
