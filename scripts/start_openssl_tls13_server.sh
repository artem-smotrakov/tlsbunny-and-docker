#!/bin/bash

OPENSSL=${OPENSSL:-"openssl"}

${OPENSSL} s_server -key certs/server_key.pem -cert certs/server_cert.der \
	-certform der -accept 10101 -www -tls1_3 -debug -tlsextdebug
