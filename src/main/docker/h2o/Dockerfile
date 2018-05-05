# this is a dockerfile which builds h2o with h2o,
# and start a local http server with TLS 1.3 support
#
# the following command builds a docker image
# it should be run from the root of tlsbunny repository
#
# $ docker build --file src/main/docker/h2o/Dockerfile --tag h2o/server/tls13 .
#
# the following command starts a local h2o server
#
# $ docker run -p 20101:20101 h2o/server/tls13
#
# good luck!
#

FROM ubuntu

MAINTAINER artem.smotrakov@gmail.com

RUN apt-get update --fix-missing
RUN apt-get install -y git
RUN apt-get install -y make cmake
RUN apt-get install -y gcc g++
RUN apt-get install -y pkg-config
RUN apt-get install -y wget
RUN apt-get install -y zlib1g zlib1g-dev

RUN mkdir -p /var/src
ADD . /var/src/tlsbunny

# build OpenSSL 1.1.1
WORKDIR /var/src
RUN wget https://www.openssl.org/source/openssl-1.1.1-pre6.tar.gz
RUN bash -c "[[ `sha256sum openssl-1.1.1-pre6.tar.gz | cut -d ' ' -f 1` = `cat /var/src/tlsbunny/src/main/docker/h2o/openssl-1.1.1-pre6.tar.gz.sha256` ]]"
RUN tar xf openssl-1.1.1-pre6.tar.gz
WORKDIR /var/src/openssl-1.1.1-pre6
RUN ./config --prefix=/usr && make && make install

# build h2o
WORKDIR /var/src
RUN git clone https://github.com/h2o/h2o
WORKDIR /var/src/h2o

ENV CFLAGS="-fsanitize=address -fno-omit-frame-pointer -g -O1"
ENV LDFLAGS="-fsanitize=address -lssl -lcrypto -L/usr/lib"

RUN cmake \
    -DCMAKE_C_FLAGS=${CFLAGS} \
    -DCMAKE_EXE_LINKER_FLAGS=${LDFLAGS} \
    -DWITH_PICOTLS=on \
    .
RUN make
RUN make install

# start h2o server
EXPOSE 30101

WORKDIR /var/src/tlsbunny

CMD [ "/usr/local/bin/h2o", "--conf", "src/main/docker/h2o/h2o.conf" ]
