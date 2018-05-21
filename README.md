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

- [TLS 1.3 draft 26](https://tools.ietf.org/html/draft-ietf-tls-tls13-26) 
- Handshake as a client
- Client authentication
- Key exchange with ECDHE using secp256r1 curve
- ecdsa_secp256r1_sha256 signatures
- AES-GCM cipher with 128-bit key

## Docker images

The repository provides several Dockerfile for building and running TLS 1.3 servers for testing:

- [OpenSSL](src/main/docker/openssl/Dockerfile): start `openssl s_server` on port 10101
- [picotls](src/main/docker/picotls/Dockerfile): start `cli` on port 20101
- [H2O + picotls](src/main/docker/h2o/Dockerfile): start `h2o` on port 30101
- [wolfSSL](src/main/docker/wolfssl/Dockerfile): start `examples/server/server` on port 40101
- [GnuTLS](src/main/docker/gnutls/Dockerfile): start `gnutls-serv` on port 50101
- [NSS](src/main/docker/nss/Dockerfile): start `selfserv` on port 60101

The libs above are built with enabled AddressSanitizer and debug/verbose output.

## Some test results

### TLS server

|                                          | Notes           | OpenSSL (+ASan) | GnuTLS (+ASan)  | NSS (+ASan)   | h2o + picotls (+ASan) | wolfSSL (+ASan) |
| -----------------------------------------|-----------------|-----------------|-----------------|---------------|-----------------------|-----------------|
| TLSPlaintext fuzzing                     | 200  tests      |                 | Ok              | Ok            | Ok                    | Ok              |
| Handshake fuzzing                        | 2000 tests      |                 | Ok              | Ok            | Ok                    | Ok              |
| ClientHello fuzzing                      | 2000 tests      |                 | Ok              | Ok            | Ok                    | Ok              |
| Certificate fuzzing (client auth)        |                 |                 |                 |               |                       |                 |
| CertificateVerify fuzzing (client auth)  |                 |                 |                 |               |                       |                 |
| Finished fuzzing                         | 2000 tests      |                 | Ok              | Ok            | Ok                    | Ok              |
| CCS fuzzing                              | 20   tests      |                 | Ok              | Ok            | Ok                    | Ok              |
| Double ClientHello                       |                 |                 |                 |               |                       |                 |
| Invalid CCS                              |                 |                 |                 |               |                       |                 |
| CCS after handshake is done              |                 |                 |                 |               |                       |                 |
| Multiple CCS                             |                 |                 |                 |               |                       |                 |
| Start with CCS                           |                 |                 |                 |               |                       |                 |
| Weak ECDHE key                           |                 |                 |                 |               |                       |                 |

### TLS client

|                             | OpenSSL (+ASan) | GnuTLS (+ASan)  | NSS (+ASan)   | h2o + picotls (+ASan) | wolfSSL (+ASan) |
| ----------------------------|-----------------|-----------------|---------------|-----------------------|-----------------|
| TLSPlaintext fuzzing        |                 |                 |               |                       |                 |
| Handshake fuzzing           |                 |                 |               |                       |                 |
| ServerHello fuzzing         |                 |                 |               |                       |                 |
| EncryptedExtensions fuzzing |                 |                 |               |                       |                 |
| Certificate fuzzing         |                 |                 |               |                       |                 |
| CertificateVerify fuzzing   |                 |                 |               |                       |                 |
| Finished fuzzing            |                 |                 |               |                       |                 |
| CCS fuzzing                 |                 |                 |               |                       |                 |
| Double ServerHello          |                 |                 |               |                       |                 |
| Invalid CCS                 |                 |                 |               |                       |                 |
| CCS after handshake is done |                 |                 |               |                       |                 |
| Multiple CCS                |                 |                 |               |                       |                 |
| Start with CCS              |                 |                 |               |                       |                 |
| Weak ECDHE key              |                 |                 |               |                       |                 |

## Similar projects

- [tlsfuzzer](https://github.com/tomato42/tlsfuzzer): SSL and TLS protocol test suite and fuzzer (python)
- [TLS-Attacker](https://github.com/RUB-NDS/TLS-Attacker): TLS-Attacker is a Java-based framework for analyzing TLS libraries. It is developed by the Ruhr University Bochum and the Hackmanit GmbH.
