#!/bin/bash

./examples/server/server \
    -p 40101 \
    -v 4 \
    -d -i -g -b -x \
    -c certs/server-cert.pem \
    -k certs/server-key.pem