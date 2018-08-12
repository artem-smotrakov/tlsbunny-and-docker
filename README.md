[![Build Status](https://travis-ci.org/artem-smotrakov/tlsbunny.svg?branch=master)](https://travis-ci.org/artem-smotrakov/tlsbunny)
[![Coverage Status](https://coveralls.io/repos/github/artem-smotrakov/tlsbunny/badge.svg?branch=master)](https://coveralls.io/github/artem-smotrakov/tlsbunny?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/81991e8a88b64c8b99fe35b9159c2410)](https://www.codacy.com/app/artem-smotrakov/tlsbunny?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=artem-smotrakov/tlsbunny&amp;utm_campaign=Badge_Grade)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# tlsbunny

This is a framework for building negative tests and fuzzers for TLS 1.3 implementations.
The idea is to split the handshake process and data exchange to simple steps which are easy to configure and re-use.
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

## Fuzzing

tlsbunny provides several fuzzers for TLS 1.3 sturctures such as TLSPlaintext, Handshake, ClientHello and Finished.
Fuzzers based on the framework can generate fuzzed messages and feed a target application via stdin, files or network socket.
On the one hand, such a fuzzer is not going to be as fast as LibFuzzer. On the other hand, the fuzzer can be easily re-used with multiple TLS implementations written in different languages (not only C/C++). 
Traditionally, fuzzing is used for testing applications written in C/C++ to uncover memory corruption issues which most likely may have security implications.
Fuzzing can also be used for testing applications written in other languages even if those languages prevent using memory directly like Java. 
No matter which language is used, a good TLS implementation should properly handle incorrect data and react with an expected action, for example, by thowing a documented exception. An unexpected behavior in processing incorrect data may still have security implications.

## Some test results

### TLS server

|                                          | Notes           | OpenSSL (+ASan) | GnuTLS (+ASan)  | NSS (+ASan)   | h2o + picotls (+ASan) | wolfSSL (+ASan) |
| -----------------------------------------|-----------------|-----------------|-----------------|---------------|-----------------------|-----------------|
| TLSPlaintext fuzzing                     | 400  tests      |                 | Ok              | Ok            | Ok                    | Ok              |
| Handshake fuzzing                        | 4000 tests      |                 | Ok              | Ok            | Ok                    | Ok              |
| ClientHello fuzzing                      | 4000 tests      |                 | Ok              | Ok            | Ok                    | Ok              |
| Certificate fuzzing (client auth)        |                 |                 |                 |               |                       |                 |
| CertificateVerify fuzzing (client auth)  |                 |                 |                 |               |                       |                 |
| Finished fuzzing                         | 4000 tests      |                 | Ok              | Ok            | Ok                    | Ok              |
| CCS fuzzing                              | 40   tests      |                 | Ok              | Ok            | Ok                    | Ok              |
| Legacy session ID fuzzing                | 39   tests      |                 |                 |               |                       |                 |
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
