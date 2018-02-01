package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.tls13.struct.Finished;
import java.nio.ByteBuffer;

public class FinishedImpl implements Finished {

    private Bytes verify_data;

    FinishedImpl(Bytes verify_data) {
        this.verify_data = verify_data;
    }

    @Override
    public byte[] getVerifyData() {
        return verify_data.encoding();
    }

    @Override
    public void setVerifyData(byte[] verify_data) {
        this.verify_data = new Bytes(verify_data);
    }

    @Override
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
    public HandshakeTypeImpl type() {
        return HandshakeTypeImpl.finished;
    }

    public static FinishedImpl parse(byte[] bytes, int hashLen) {
        return parse(ByteBuffer.wrap(bytes), hashLen);
    }
    
    public static FinishedImpl parse(ByteBuffer buffer, int hashLen) {
        byte[] verify_data = new byte[hashLen];
        buffer.get(verify_data);
        return new FinishedImpl(new Bytes(verify_data));
    }

}
