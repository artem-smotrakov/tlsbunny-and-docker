# this is a dockerfile which builds NSS with TLS 1.3 support,
# and start a local server
#
# the following command builds a docker image
# it should be run from the root of tlsbunny repository
#
# docker build --file src/main/docker/nss/Dockerfile --tag tlsbunny_nss_tls13 .
#
# the following command starts an NSS server
#
# docker run -p 60101:60101 tlsbunny_nss_tls13
#
# good luck!
#

FROM ubuntu

MAINTAINER artem.smotrakov@gmail.com

RUN apt-get update && apt-get install -y gcc g++ \
    make mercurial openssl zlib1g zlib1g-dev

RUN mkdir -p /var/src/
WORKDIR /var/src

RUN hg clone https://hg.mozilla.org/projects/nspr && \
    hg clone https://hg.mozilla.org/projects/nss

ENV BUILD_OPT 0
ENV USE_64 1
ENV USE_ASAN 1
ENV NSS_ENABLE_TLS_1_3=1

WORKDIR /var/src/nss

# legacy build system
# TODO should we switch to 'bash build.sh' (requires gyp and ninja packages)
RUN make nss_build_all

EXPOSE 60101

ENV PATH /var/src/dist/Linux4.4_x86_64_cc_glibc_PTH_64_OPT.OBJ/bin:${PATH}
ENV LD_LIBRARY_PATH /var/src/dist/Linux4.4_x86_64_cc_glibc_PTH_64_OPT.OBJ/lib

ADD . /var/src/tlsbunny
WORKDIR /var/src/tlsbunny

RUN bash src/main/vendor/nss/create_nss_db.sh

CMD [ "bash", "src/main/vendor/nss/start.sh" ]
