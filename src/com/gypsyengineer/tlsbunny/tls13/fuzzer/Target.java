package com.gypsyengineer.tlsbunny.tls13.fuzzer;

public enum Target {
    tls_plaintext,
    handshake,

    // handshake messages
    client_hello,
    certificate,
    certificate_verify,
    finished
}