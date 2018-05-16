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

RUN apt-get update --fix-missing
RUN apt-get install -y make cmake gcc g++ pkg-config

RUN mkdir -p /var/src

WORKDIR /var/src
RUN wget https://wolfssl.com/downloads/wolfssl-3.14.0.zip
RUN unzip wolfssl-3.14.0.zip
ENV WOLFSSL_SRC=/var/src/wolfssl-3.14.0

WORKDIR /var/src/wolfssl-3.14.0

ENV CFLAGS="-fsanitize=address -fno-omit-frame-pointer"
ENV CXXFLAGS="-fsanitize=address -fno-omit-frame-pointer"
ENV LDFLAGS=-fsanitize=address

RUN CFLAGS=${CFLAGS} CXXFLAGS=${CXXFLAGS} LDFLAGS=${LDFLAGS} \
	./configure \
		--enable-tls13 \
		--enable-debug

RUN CFLAGS=${CFLAGS} CXXFLAGS=${CXXFLAGS} LDFLAGS=${LDFLAGS} make
RUN make install

EXPOSE 40101

ADD . /var/src/tlsbunny
WORKDIR /var/src/tlsbunny

CMD [ "bash", "scripts/start_wolfssl_tls13_server.sh" ]
