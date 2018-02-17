#!/bin/bash

./config \
	--prefix=${OPENSSL_DIR} \
	--debug \
	enable-asan enable-tls1_3 enable-ssl-trace
make
make install
