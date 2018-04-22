package com.gypsyengineer.tlsbunny.tls13.connection.action.simple;

import com.gypsyengineer.tlsbunny.tls13.connection.action.AbstractAction;
import com.gypsyengineer.tlsbunny.tls13.connection.action.Action;
import com.gypsyengineer.tlsbunny.tls13.struct.Handshake;

public class UpdatingContext extends AbstractAction {

    public enum Element {
        first_client_hello,
        hello_retry_request,
        second_client_hello,
        server_hello,
        encrypted_extensions,
        server_certificate_request,
        server_certificate,
        server_certificate_verify,
        server_finished,
        end_of_early_data,
        client_certificate,
        client_certificate_verify,
        client_finished
    }

    private final Element element;

    public UpdatingContext(Element item) {
        this.element = item;
    }

    @Override
    public String name() {
        return String.format("updating context (%s)", element);
    }

    @Override
    public Action run() throws Exception {
        Handshake handshake;

        in.mark();
        try {
            handshake = context.factory.parser().parseHandshake(in);
        } finally {
            in.reset();
        }

        switch (element) {
            case first_client_hello:
                context.setFirstClientHello(handshake);
                break;
            case hello_retry_request:
                context.setHelloRetryRequest(handshake);
                break;
            case second_client_hello:
                context.setSecondClientHello(handshake);
                break;
            case server_hello:
                context.setServerHello(handshake);
                break;
            case encrypted_extensions:
                context.setEncryptedExtensions(handshake);
                break;
            case server_certificate_request:
                context.setServerCertificateRequest(handshake);
                break;
            case server_certificate:
                context.setServerCertificate(handshake);
                break;
            case server_certificate_verify:
                context.setServerCertificateVerify(handshake);
                break;
            case server_finished:
                context.setServerFinished(handshake);
                break;
            case end_of_early_data:
                context.setEndOfEarlyData(handshake);
                break;
            case client_certificate:
                context.setClientCertificate(handshake);
                break;
            case client_certificate_verify:
                context.setClientCertificateVerify(handshake);
                break;
            case client_finished:
                context.setClientFinished(handshake);
                break;
            default:
                throw new IllegalArgumentException();
        }

        return this;
    }

}
