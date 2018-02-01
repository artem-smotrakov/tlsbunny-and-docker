package com.gypsyengineer.tlsbunny.tls13.struct.impl;

import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.tls.UInt24;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;
import com.gypsyengineer.tlsbunny.tls13.struct.HandshakeType;

public class HandshakeImpl implements Handshake {

    private HandshakeType msg_type;
    private UInt24 length;
    private Bytes body;

    HandshakeImpl(HandshakeType msg_type, UInt24 length, Bytes body) {
        this.msg_type = msg_type;
        this.length = length;
        this.body = body;
    }

    public HandshakeImpl(HandshakeTypeImpl msg_type, int length, byte[] body) {
        this(msg_type, new UInt24(length), new Bytes(body));
    }

    @Override
    public int encodingLength() {
        return Utils.getEncodingLength(msg_type, length, body);
    }

    @Override
    public byte[] encoding() throws IOException {
        return Utils.encoding(msg_type, length, body);
    }

    @Override
    public void setMessageType(HandshakeType msg_type) {
        this.msg_type = msg_type;
    }

    @Override
    public void setLength(UInt24 length) {
        this.length = length;
    }

    @Override
    public void setBody(Bytes body) {
        this.body = body;
    }

    @Override
    public HandshakeType getMessageType() {
        return msg_type;
    }

    @Override
    public UInt24 getLength() {
        return length;
    }

    @Override
    public byte[] getBody() {
        return body.encoding();
    }

    @Override
    public boolean containsClientHello() {
        return HandshakeTypeImpl.client_hello.equals(msg_type);
    }

    @Override
    public boolean containsHelloRetryRequest() {
        return HandshakeTypeImpl.hello_retry_request.equals(msg_type);
    }

    @Override
    public boolean containsServerHello() {
        return HandshakeTypeImpl.server_hello.equals(msg_type);
    }

    @Override
    public boolean containsEncryptedExtensions(){
        return HandshakeTypeImpl.encrypted_extensions.equals(msg_type);
    }

    @Override
    public boolean containsCertificateRequest() {
        return HandshakeTypeImpl.certificate_request.equals(msg_type);
    }

    @Override
    public boolean containsCertificate() {
        return HandshakeTypeImpl.certificate.equals(msg_type);
    }

    @Override
    public boolean containsCertificateVerify() {
        return HandshakeTypeImpl.certificate_verify.equals(msg_type);
    }

    @Override
    public boolean containsFinished() {
        return HandshakeTypeImpl.finished.equals(msg_type);
    }

    @Override
    public boolean containsNewSessionTicket() {
        return HandshakeTypeImpl.new_session_ticket.equals(msg_type);
    }

    public static HandshakeImpl parse(byte[] bytes) {
        return parse(ByteBuffer.wrap(bytes));
    }
    
    public static HandshakeImpl parse(ByteBuffer buffer) {
        HandshakeTypeImpl msg_type = HandshakeTypeImpl.parse(buffer);
        UInt24 length = UInt24.parse(buffer);
        Bytes body = Bytes.parse(buffer, length.value);

        return new HandshakeImpl(msg_type, length, body);
    }
    
}
