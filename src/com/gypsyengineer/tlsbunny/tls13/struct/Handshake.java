package com.gypsyengineer.tlsbunny.tls13.struct;

import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.tls.Entity;
import com.gypsyengineer.tlsbunny.tls.UInt24;
import com.gypsyengineer.tlsbunny.utils.Utils;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Handshake implements Entity {

    private HandshakeType msg_type;
    private UInt24 length;
    private Entity body;

    public Handshake() {
        this(HandshakeType.client_hello, UInt24.ZERO, Bytes.EMPTY);
    }

    public Handshake(HandshakeType msg_type, UInt24 length, Entity body) {
        this.msg_type = msg_type;
        this.length = length;
        this.body = body;
    }

    public Handshake(HandshakeType msg_type, int length, byte[] body) {
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

    public void setMessageType(HandshakeType msg_type) {
        this.msg_type = msg_type;
    }

    public void setLength(UInt24 length) {
        this.length = length;
    }

    public void setBody(byte[] body) {
        this.body = new Bytes(body);
    }

    public HandshakeType getMessageType() {
        return msg_type;
    }

    public UInt24 getLength() {
        return length;
    }

    public byte[] getBody() throws IOException {
        return body.encoding();
    }

    public boolean containsClientHello() {
        return HandshakeType.client_hello.equals(msg_type);
    }

    public boolean containsHelloRetryRequest() {
        return HandshakeType.hello_retry_request.equals(msg_type);
    }

    public boolean containsServerHello() {
        return HandshakeType.server_hello.equals(msg_type);
    }

    public boolean containsEncryptedExtensions(){
        return HandshakeType.encrypted_extensions.equals(msg_type);
    }

    public boolean containsCertificateRequest() {
        return HandshakeType.certificate_request.equals(msg_type);
    }

    public boolean containsCertificate() {
        return HandshakeType.certificate.equals(msg_type);
    }

    public boolean containsCertificateVerify() {
        return HandshakeType.certificate_verify.equals(msg_type);
    }

    public boolean containsFinished() {
        return HandshakeType.finished.equals(msg_type);
    }

    public boolean containsNewSessionTicket() {
        return HandshakeType.new_session_ticket.equals(msg_type);
    }

    public static Handshake parse(byte[] bytes) {
        return parse(ByteBuffer.wrap(bytes));
    }
    
    public static Handshake parse(ByteBuffer buffer) {
        HandshakeType msg_type = HandshakeType.parse(buffer);
        UInt24 length = UInt24.parse(buffer);
        byte[] body = new byte[length.value];
        buffer.get(body);

        return new Handshake(msg_type, length, new Bytes(body));
    }
    
    public static Handshake wrap(HandshakeMessage message) throws IOException {
        return new Handshake(
                message.type(), 
                message.encodingLength(), 
                message.encoding());
    }

}
