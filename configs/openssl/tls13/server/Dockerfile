# this is a dockerfile which builds OpenSSL with TLS 1.3 support,
# and start a local server and fuzz an OpenSSL server
#
# the following command builds a docker image
# it should be run from the root of tlsbunny repository
#
# docker build --file configs/openssl/tls13/server/Dockerfile --tag openssl/server/tls13 .
#
# the following command starts an OpenSSL server
#
# docker run -p 10101:10101 openssl/server/tls13
#
# the following command starts an OpenSSL server with required client authentication
#
# docker run -p 10101:10101 -e OPTIONS="-Verify 0 -CAfile certs/root_cert.pem" openssl/server/tls13
#
# good luck!
#

FROM ubuntu

MAINTAINER artem.smotrakov@gmail.com

RUN apt-get update
RUN apt-get install -y git
RUN apt-get install -y make
RUN apt-get install -y gcc

RUN mkdir -p /var/src/openssl
RUN mkdir -p /var/src/tlsbunny
RUN mkdir -p /var/openssl

ENV TLSBUNNY /var/src/tlsbunny
ENV OPENSSL_SRC /var/src/openssl
ENV OPENSSL_DIR /var/openssl

ADD . ${TLSBUNNY}
WORKDIR /var/ws

RUN git clone https://github.com/openssl/openssl ${OPENSSL_SRC}
#ADD openssl_src ${OPENSSL_SRC}

WORKDIR ${OPENSSL_SRC}
RUN bash ${TLSBUNNY}/scripts/build_openssl_tls13.sh

ENV OPENSSL /var/openssl/bin/openssl
ENV LD_LIBRARY_PATH /var/openssl/lib

EXPOSE 10101
WORKDIR ${TLSBUNNY}

CMD [ "bash", "scripts/start_openssl_tls13_server.sh" ]
