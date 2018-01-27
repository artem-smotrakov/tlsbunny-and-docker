package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Bytes;
import java.nio.ByteBuffer;

public class Finished implements HandshakeMessage {

    private Bytes verify_data;

    public Finished(Bytes verify_data) {
        this.verify_data = verify_data;
    }
    
    public Finished(byte[] verify_data) {
        this(new Bytes(verify_data));
    }

    public byte[] getVerifyData() {
        return verify_data.encoding();
    }

    public void setVerifyData(byte[] verify_data) {
        this.verify_data = new Bytes(verify_data);
    }

    public void setVerifyData(Bytes verify_data) {
        this.verify_data = verify_data;
    }

    @Override
    public int encodingLength() {
        return verify_data.encodingLength();
    }

    @Override
    public byte[] encoding() {
        return verify_data.encoding();
    }

    @Override
    public HandshakeType type() {
        return HandshakeType.finished;
    }

    public static Finished parse(byte[] bytes, int hashLen) {
        return parse(ByteBuffer.wrap(bytes), hashLen);
    }
    
    public static Finished parse(ByteBuffer buffer, int hashLen) {
        byte[] verify_data = new byte[hashLen];
        buffer.get(verify_data);
        return new Finished(new Bytes(verify_data));
    }

}
