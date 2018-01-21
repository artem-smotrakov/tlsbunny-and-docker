package com.gypsyengineer.tlsbunny.tls13;

import com.gypsyengineer.tlsbunny.tls.Bytes;
import com.gypsyengineer.tlsbunny.tls.Entity;
import com.gypsyengineer.tlsbunny.tls.UInt24;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Handshake implements Entity {

    private HandshakeType msg_type;
    private UInt24 length;
    private Entity body;

    public Handshake() {
        this(HandshakeType.CLIENT_HELLO, UInt24.ZERO, Bytes.EMPTY);
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
        return msg_type.encodingLength()
                + length.encodingLength()
                + body.encodingLength();
    }

    @Override
    public byte[] encoding() throws IOException {
        return ByteBuffer.allocate(encodingLength())
                .put(msg_type.encoding())
                .put(length.encoding())
                .put(body.encoding())
                .array();
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
        return HandshakeType.CLIENT_HELLO.equals(msg_type);
    }

    public boolean containsHelloRetryRequest() {
        return HandshakeType.HELLO_RETRY_REQUEST.equals(msg_type);
    }

    public boolean containsServerHello() {
        return HandshakeType.SERVER_HELLO.equals(msg_type);
    }

    public boolean containsEncryptedExtensions(){
        return HandshakeType.ENCRYPTED_EXTENSIONS.equals(msg_type);
    }

    public boolean containsCertificateRequest() {
        return HandshakeType.CERTIFICATE_REQUEST.equals(msg_type);
    }

    public boolean containsCertificate() {
        return HandshakeType.CERTIFICATE.equals(msg_type);
    }

    public boolean containsCertificateVerify() {
        return HandshakeType.CERTIFICATE_VERIFY.equals(msg_type);
    }

    public boolean containsFinished() {
        return HandshakeType.FINISHED.equals(msg_type);
    }

    public boolean containsNewSessionTicket() {
        return HandshakeType.NEW_SESSION_TICKET.equals(msg_type);
    }

    public static Handshake parse(ByteBuffer buffer) {
        HandshakeType msg_type = HandshakeType.parse(buffer);
        UInt24 length = UInt24.parse(buffer);
        byte[] body = new byte[length.value];
        buffer.get(body);

        return new Handshake(msg_type, length, new Bytes(body));
    }
    
    
    public static Handshake wrap(HandshakeMessage message) throws IOException {
        return new Handshake(message.type(), 
                message.encodingLength(), message.encoding());
    }

}
