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

RUN apt-get update && apt-get install -y \
    dash git-core autoconf libtool gettext autopoint \
    automake autogen nettle-dev libp11-kit-dev libtspi-dev libunistring-dev \
    guile-2.0 guile-2.0-libs guile-2.0-dev libtasn1-6 libtasn1-6-dev libidn2-0-dev gawk gperf \
    libunbound-dev dns-root-data bison help2man gtk-doc-tools \
    texinfo texlive texlive-generic-recommended texlive-extra-utils \
    libgmp-dev wget

ENV GNUTLS_SRC /var/src/gnutls
ENV TLSBUNNY /var/src/tlsbunny

RUN mkdir -p /var/src/gnutls && mkdir -p /var/src/tlsbunny

# TODO check downloaded archive
WORKDIR /var/src
RUN wget https://ftp.gnu.org/gnu/nettle/nettle-3.4.tar.gz && tar xf nettle-3.4.tar.gz

ENV CFLAGS="-fsanitize=address -fno-omit-frame-pointer -g -O1"
ENV CXXFLAGS="-fsanitize=address -fno-omit-frame-pointer"
ENV LDFLAGS="-fsanitize=address"

WORKDIR /var/src/nettle-3.4
RUN ./configure --disable-openssl --enable-shared && make && make install

RUN git clone https://gitlab.com/gnutls/gnutls.git ${GNUTLS_SRC} && \
    cd ${GNUTLS_SRC} && make bootstrap && \
    LD_LIBRARY_PATH=/usr/local/lib \
        ./configure \
            --with-included-libtasn1 \
            --with-guile-site-dir=no \
            --disable-guile && \
    LD_LIBRARY_PATH=/usr/local/lib make && \
    LD_LIBRARY_PATH=/usr/local/lib make install

EXPOSE 50101
ADD . ${TLSBUNNY}
WORKDIR ${TLSBUNNY}

ENV LD_LIBRARY_PATH /usr/local/lib:/usr/lib/x86_64-linux-gnu/

CMD [ "bash", "scripts/start_gnutls_tls13_server.sh" ]
