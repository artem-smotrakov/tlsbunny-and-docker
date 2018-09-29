#!/bin/bash

openssl_src=${1}

lcov --zerocounters --directory ${openssl_src}
lcov --no-external --capture --initial --directory ${openssl_src} --output-file base.info

#export ASAN_OPTIONS="detect_leaks=0"
openssl s_server -key certs/server_key.pem -cert certs/server_cert.der \
	-certform der -accept 10101 -www -tls1_3 -debug -tlsextdebug \
	${OPTIONS}

mkdir cc_report
lcov --no-external --capture --directory ${openssl_src} --output-file test.info
lcov --add-tracefile base.info --add-tracefile test.info --output-file total.info
genhtml --ignore-errors source total.info --legend --title openssl --output-directory=cc_report
