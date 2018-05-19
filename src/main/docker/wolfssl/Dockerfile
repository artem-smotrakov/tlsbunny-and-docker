# this is a dockerfile which builds wolfssl,
# and starts a local TLS 1.3 server
#
# the following command builds a docker image
# it should be run from the root of tlsbunny repository
#
# $ docker build --file src/main/docker/wolfssl/Dockerfile --tag wolfssl/server/tls13 .
#
# the following command starts a local picotls server
#
# $ docker run -p 40101:40101 wolfssl/server/tls13
#
# good luck!
#

FROM ubuntu

MAINTAINER artem.smotrakov@gmail.com

RUN apt-get update && apt-get install -y cmake gcc g++ make pkg-config

RUN mkdir -p /var/src

ENV CFLAGS="-fsanitize=address -fno-omit-frame-pointer"
ENV CXXFLAGS="-fsanitize=address -fno-omit-frame-pointer"
ENV LDFLAGS="-fsanitize=address"

WORKDIR /var/src
RUN wget https://wolfssl.com/downloads/wolfssl-3.14.0.zip && \
    bash -c "[[ `sha256sum openssl-1.1.1-pre6.tar.gz | cut -d ' ' -f 1` = `cat /var/src/tlsbunny/src/main/docker/wolfssl/wolfssl-3.14.0.zip.sha256` ]]" && \
    unzip wolfssl-3.14.0.zip && \
    cd /var/src/wolfssl-3.14.0 && \
    CFLAGS=${CFLAGS} CXXFLAGS=${CXXFLAGS} LDFLAGS=${LDFLAGS} \
	    ./configure \
		    --enable-tls13 \
		    --enable-debug && \
    CFLAGS=${CFLAGS} CXXFLAGS=${CXXFLAGS} LDFLAGS=${LDFLAGS} make && make install

EXPOSE 40101

ADD . /var/src/tlsbunny
WORKDIR /var/src/tlsbunny

CMD [ "bash", "scripts/start_wolfssl_tls13_server.sh" ]
