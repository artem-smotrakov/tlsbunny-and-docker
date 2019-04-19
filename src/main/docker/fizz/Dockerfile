# this Dockerfile which builds fizz, and starts a local server
#
# the following command builds a docker image
#
# docker build --file src/main/docker/fizz/Dockerfile --tag tlsbunny_fizz_tls13 .
#
# the following command start an the server server
#
# docker run -p 30101:30101 tlsbunny_fizz_tls13
#
# good luck!
#

FROM ubuntu

MAINTAINER artem.smotrakov@gmail.com

RUN apt-get update && apt-get install -y \
        git \
        g++ \
        cmake \
        libboost-all-dev \
        libevent-dev \
        libdouble-conversion-dev \
        libgoogle-glog-dev \
        libgflags-dev \
        libiberty-dev \
        liblz4-dev \
        liblzma-dev \
        libsnappy-dev \
        make \
        zlib1g-dev \
        binutils-dev \
        libjemalloc-dev \
        libssl-dev \
        pkg-config \
        libsodium-dev

ENV TLSBUNNY /var/src/tlsbunny

RUN mkdir -p /var/src ${TLSBUNNY}

WORKDIR /var/src

RUN git clone https://github.com/facebook/folly && \
    mkdir folly/build_ && cd folly/build_ && \
    cmake -DFOLLY_ASAN_ENABLED=ON .. && \
    make && \
    make install

ENV CFLAGS "-fsanitize=address -fno-omit-frame-pointer -g -O1"
ENV CXXFLAGS "-fsanitize=address -fno-omit-frame-pointer"
ENV LDFLAGS "-fsanitize=address"

RUN git clone https://github.com/facebookincubator/fizz && \
    mkdir fizz/build_ && cd fizz/build_ && \
    cmake ../fizz && \
    make && \
    make install

ADD . ${TLSBUNNY}
WORKDIR ${TLSBUNNY}

EXPOSE 50101

ENV LD_LIBRARY_PATH /usr/local/lib:/usr/lib/x86_64-linux-gnu/

CMD [ "bash", "src/main/fizz/start.sh" ]
