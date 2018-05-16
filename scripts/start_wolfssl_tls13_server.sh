#!/bin/bash

${WOLFSSL_SRC}/examples/server/server \
    -p 40101 \
    -v 4 \
    -d -i -g \
    -c certs/server_cert.pem \
    -k ../../tlsbunny/certs/server_key.pem
