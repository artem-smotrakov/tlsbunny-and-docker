# tlsbunny

This is a framework for building negative tests and fuzzers for TLS 1.3 implementations.
The idea is to decompose handshake process and data exchange to simple steps which are easy to configure.
The framework provides a set of basic steps which can be used in TLS 1.3 communication, for example:

- generating a ClientHello message
- wrapping a handshake message into a Handshake structure
- wrapping a handshake message into a TLSPlaintext structure
- key exchange and generating symmetric keys
- receiving incoming data
- parsing a TLSCiphertext message
- decrypting a TLSCiphertext message and so on

These basic blocks allow to control and test each step in TLS 1.3 connection.

The framework also provides an engine which runs specified actions. The engine supports adding checks and analyzers which run after a connection finishes.

Here is an example on HTTPS connection using TLS 1.3:

```java
CommonConfig config = CommonConfig.load();

Engine.init()
        .target(config.host())
        .target(config.port())
        .send(new OutgoingClientHello())
        .send(new OutgoingChangeCipherSpec())
        .require(new IncomingServerHello())
        .require(new IncomingChangeCipherSpec())
        .require(new IncomingEncryptedExtensions())
        .require(new IncomingCertificate())
        .require(new IncomingCertificateVerify())
        .require(new IncomingFinished())
        .send(new OutgoingFinished())
        .allow(new IncomingNewSessionTicket())
        .send(new OutgoingHttpGetRequest())
        .require(new IncomingApplicationData())
        .connect()
        .run(new NoAlertCheck());
```

## Supported features

- TLS 1.3 (draft 26) handshake as a client
- Client authentication
- Key exchange with ECDHE using secp256r1 curve
- ecdsa_secp256r1_sha256 signatures
- AES-GCM cipher with 128-bit key

## Some test results

|                             | OpenSSL (+ASan) | GnuTLS  | NSS    | h2o + picotls (+ASan) | wolfSSL (+ASan) |
| ----------------------------|-----------------|---------|--------|-----------------------|-----------------|
| TLSPlaintext fuzzing        | 200  tests      |         |        |                       |                 |
| Handshake fuzzing           | 2000 tests      |         |        |                       |                 |
| ClientHello fuzzing         | 2000 tests      |         |        |                       |                 |
| Certificate fuzzing         |                 |         |        |                       |                 |
| CertificateVerify fuzzing   |                 |         |        |                       |                 |
| Finished fuzzing            | 2000 tests      |         |        |                       |                 |
| CCS fuzzing                 | 20   tests      |         |        |                       |                 |
| Double ClientHello          |                 |         |        |                       |                 |
| Invalid CCS                 |                 |         |        |                       |                 |
| CCS after handshake is done |                 |         |        |                       |                 |
| Multiple CCS                |                 |         |        |                       |                 |
| Start with CCS              |                 |         |        |                       |                 |
| Weak ECDHE key              |                 |         |        |                       |                 |

## Alternatives

- [tlsfuzzer](https://github.com/tomato42/tlsfuzzer): SSL and TLS protocol test suite and fuzzer (python)
- [TLS-Attacker](https://github.com/RUB-NDS/TLS-Attacker): TLS-Attacker is a Java-based framework for analyzing TLS libraries. It is developed by the Ruhr University Bochum and the Hackmanit GmbH.
