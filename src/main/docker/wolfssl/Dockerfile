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

RUN apt-get update && apt-get install -y \
    autoconf cmake gcc git g++ libtool make pkg-config

RUN mkdir -p /var/src
WORKDIR /var/src
ADD . /var/src/tlsbunny

ENV CFLAGS "-fsanitize=address -fno-omit-frame-pointer"
ENV CXXFLAGS "-fsanitize=address -fno-omit-frame-pointer"
ENV LDFLAGS "-fsanitize=address"


RUN git clone https://github.com/wolfSSL/wolfssl && \
    cd wolfssl && \
    bash autogen.sh && \
    CFLAGS=${CFLAGS} CXXFLAGS=${CXXFLAGS} LDFLAGS=${LDFLAGS} \
	    ./configure \
		    --enable-tls13 \
		    --enable-tls13-draft26 \
		    --enable-debug && \
    CFLAGS=${CFLAGS} CXXFLAGS=${CXXFLAGS} LDFLAGS=${LDFLAGS} make && \
    make install

EXPOSE 40101

ENV TLSBUNNY /var/src/tlsbunny
ENV ASAN_OPTIONS "detect_leaks=0"
WORKDIR /var/src/wolfssl

CMD [ "bash", "/var/src/tlsbunny/scripts/start_wolfssl_tls13_server.sh" ]
