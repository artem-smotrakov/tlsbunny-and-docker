package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Entity;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

public class Alert implements Entity {

    private static final int ENCODING_LENGTH = 2;

    private AlertLevel level;
    private AlertDescription description;

    public Alert() {
        this(AlertLevel.FATAL, AlertDescription.INTERNAL_ERROR);
    }

    public Alert(AlertLevel level, AlertDescription description) {
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

    public AlertLevel getLevel() {
        return level;
    }

    public void setLevel(AlertLevel level) {
        this.level = level;
    }

    public AlertDescription getDescription() {
        return description;
    }

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
        final Alert other = (Alert) obj;
        if (!Objects.equals(this.level, other.level)) {
            return false;
        }
        return Objects.equals(this.description, other.description);
    }

    public static Alert parse(byte[] bytes) {
        return parse(ByteBuffer.wrap(bytes));
    }
    
    public static Alert parse(ByteBuffer buffer) {
        AlertLevel level = AlertLevel.parse(buffer);
        AlertDescription description = AlertDescription.parse(buffer);

        return new Alert(level, description);
    }

}
