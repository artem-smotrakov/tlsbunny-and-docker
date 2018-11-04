# this is a dockerfile which builds OpenSSL with TLS 1.3 support,
# and start a local server
#
# the following command builds a docker image
# it should be run from the root of tlsbunny repository
#
# docker build --file src/impl/docker/openssl/Dockerfile --tag openssl/tls13 .
#
# the following command starts an OpenSSL server
#
# docker run -p 10101:10101 openssl/tls13
#
# the following command starts an OpenSSL server with required client authentication
#
# docker run -p 10101:10101 -e OPTIONS="-Verify 0 -CAfile certs/root_cert.pem" openssl/tls13
#
# good luck!
#

FROM ubuntu

MAINTAINER artem.smotrakov@gmail.com

RUN apt-get update && apt-get install -y gcc git lcov make

RUN mkdir -p /var/src/openssl /var/src/tlsbunny /var/reports

ENV CFLAGS "-fprofile-arcs -ftest-coverage"
ENV LDFLAGS "-fsanitize=address"

RUN git clone https://github.com/openssl/openssl /var/src/openssl

ADD . /var/src/tlsbunny
WORKDIR /var/src/openssl
RUN git apply /var/src/tlsbunny/src/impl/openssl/s_server.patch
RUN ./config \
	    --debug \
	    enable-asan \
	    enable-tls1_3 \
	    enable-ssl-trace && \
	make && make install

ENV LD_LIBRARY_PATH /usr/local/lib

WORKDIR /var/src/tlsbunny

EXPOSE 10101

CMD [ "bash", "src/impl/openssl/start.sh", "/var/src/openssl" ]
