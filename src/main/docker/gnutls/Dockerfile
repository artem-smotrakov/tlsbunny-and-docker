# this Dockerfile which builds GnuTLS with TLS 1.3 support,
# and starts a local server
#
# the following command builds a docker image
#
# docker build --file src/main/docker/gnutls/Dockerfile --tag gnutls/server/tls13 .
#
# the following command start an the server server
#
# docker run -p 50101:50101 gnutls/server/tls13
#
# good luck!
#

FROM ubuntu

MAINTAINER artem.smotrakov@gmail.com

RUN apt-get update
RUN apt-get update --fix-missing
RUN apt-get install -y dash git-core autoconf libtool gettext autopoint
RUN apt-get install -y automake autogen nettle-dev libp11-kit-dev libtspi-dev libunistring-dev
RUN apt-get install -y guile-2.0 guile-2.0-libs guile-2.0-dev libtasn1-6 libtasn1-6-dev libidn2-0-dev gawk gperf
RUN apt-get install -y libunbound-dev dns-root-data bison help2man gtk-doc-tools
RUN apt-get install -y texinfo texlive texlive-generic-recommended texlive-extra-utils
RUN apt-get install -y libgmp-dev wget

ENV GNUTLS_SRC /var/src/gnutls
ENV TLSBUNNY /var/src/tlsbunny

RUN mkdir -p /var/src/gnutls
RUN mkdir -p /var/src/tlsbunny

WORKDIR /var/src
RUN wget https://ftp.gnu.org/gnu/nettle/nettle-3.4.tar.gz
RUN tar xf nettle-3.4.tar.gz
# for debug purposes
ADD nettle-3.4 /var/src/nettle-3.4

WORKDIR /var/src/nettle-3.4
RUN ./configure --disable-openssl --enable-shared
RUN make
RUN make install

RUN git clone https://gitlab.com/gnutls/gnutls.git ${GNUTLS_SRC}
# for debug purposes
# ADD gnutls ${GNUTLS_SRC}

WORKDIR ${GNUTLS_SRC}
RUN make bootstrap
RUN LD_LIBRARY_PATH=/usr/local/lib ./configure --with-included-libtasn1 --with-guile-site-dir=no
RUN LD_LIBRARY_PATH=/usr/local/lib make
RUN LD_LIBRARY_PATH=/usr/local/lib make install

EXPOSE 50101
ADD . ${TLSBUNNY}
WORKDIR ${TLSBUNNY}

ENV LD_LIBRARY_PATH /usr/local/lib:/usr/lib/x86_64-linux-gnu/

CMD [ "bash", "scripts/start_gnutls_tls13_server.sh" ]
