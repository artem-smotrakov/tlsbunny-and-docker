#!/bin/bash

openssl_src=${1}

lcov --zerocounters --directory ${openssl_src}
lcov --no-external --capture --initial --directory ${openssl_src} --output-file base.info

openssl s_server -key certs/server_key.pem -cert certs/server_cert.der \
	-certform der -accept 10101 -www -tls1_3 \
	${OPTIONS}

timestamp=$(date +%s)
cc_report="/var/reports/cc_report.${timestamp}"
mkdir ${cc_report}
lcov --no-external --capture --directory ${openssl_src} --output-file test.info
lcov --add-tracefile base.info --add-tracefile test.info --output-file total.info
lcov --remove total.info "${openssl_dir}/test/*" "${openssl_dir}/fuzz/*" --output-file total.info
genhtml --ignore-errors source total.info --legend --title openssl --output-directory=${cc_report}
