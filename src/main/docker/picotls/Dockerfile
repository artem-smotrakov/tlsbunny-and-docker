# this is a dockerfile which builds picotls,
# and start a local server which supports TLS 1.3
#
# the following command builds a docker image
# it should be run from the root of tlsbunny repository
#
# docker build --file src/main/docker/picotls/Dockerfile --tag picotls/server/tls13 .
#
# the following command starts a picotls server
#
# docker run -p 20101:20101 picotls/server/tls13
#
# good luck!
#

FROM ubuntu

MAINTAINER artem.smotrakov@gmail.com

RUN apt-get update --fix-missing
RUN apt-get install -y git make cmake gcc g++ pkg-config openssl libssl-dev

RUN mkdir -p /var/src/tlsbunny

ENV TLSBUNNY /var/src/tlsbunny
ENV PICOTLS /var/src/picotls

RUN git clone https://github.com/h2o/picotls ${PICOTLS}

WORKDIR ${PICOTLS}
RUN git submodule init
RUN git submodule update
RUN cmake . && make

EXPOSE 20101

ADD . ${TLSBUNNY}
WORKDIR ${TLSBUNNY}

CMD [ "bash", "scripts/start_picotls_tls13_server.sh" ]
