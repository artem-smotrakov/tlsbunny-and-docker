# this is a dockerfile which builds picotls,
# and start a local TLS 1.3 server
#
# the following command builds a docker image
# it should be run from the root of tlsbunny repository
#
# $ docker build --file src/main/docker/picotls/Dockerfile --tag tlsbunny_picotls_tls13 .
#
# the following command starts a local picotls server
#
# $ docker run -p 20101:20101 tlsbunny_picotls_tls13
#
# good luck!
#

FROM ubuntu

MAINTAINER artem.smotrakov@gmail.com

RUN apt-get update && apt-get install -y cmake gcc git g++ \
    libssl-dev make openssl pkg-config

RUN mkdir -p /var/src/tlsbunny

ENV TLSBUNNY /var/src/tlsbunny
ENV PICOTLS /var/src/picotls

ENV CFLAGS "-fsanitize=address -fno-omit-frame-pointer -g -O1"
ENV LDFLAGS "-fsanitize=address"

RUN git clone https://github.com/h2o/picotls ${PICOTLS} && \
    cd ${PICOTLS} && \
    git submodule init && git submodule update && \
    cmake \
        -DCMAKE_C_FLAGS=${CFLAGS} \
        -DCMAKE_EXE_LINKER_FLAGS=${LDFLAGS} \
        . && \
    make

EXPOSE 20101

ADD . ${TLSBUNNY}
WORKDIR ${TLSBUNNY}

CMD [ "bash", "src/main/picotls/start.sh" ]
