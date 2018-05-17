# this is a dockerfile which builds NSS with TLS 1.3 support,
# and start a local server
#
# the following command builds a docker image
# it should be run from the root of tlsbunny repository
#
# docker build --file src/main/docker/nss/Dockerfile --tag nss/server/tls13 .
#
# the following command starts an NSS server
#
# docker run -p 60101:60101 nss/server/tls13
#
# good luck!
#

FROM ubuntu

MAINTAINER artem.smotrakov@gmail.com

RUN apt-get update
RUN apt-get install -y mercurial make gcc

RUN mkdir -p /var/src/

WORKDIR /var/src

RUN hg clone https://hg.mozilla.org/projects/nspr
RUN hg clone https://hg.mozilla.org/projects/nss

ENV BUILD_OPT 0
ENV USE_64 1
ENV USE_ASAN 1

WORKDIR /var/src/nss
RUN make nss_build_all

EXPOSE 60101

ENV LD_LIBRARY_PATH /usr/local/lib

ADD . /var/src/tlsbunny
WORKDIR /var/src/tlsbunny

CMD [ "bash", "scripts/start_nss_tls13_server.sh" ]
