#!/bin/bash

# looks like wolfssl server can exit if a wrong message was received
# so run it in a loop
while true;
do
    ./examples/server/server \
        -p 40101 \
        -v 4 \
        -d -i -g -b -x \
        -c certs/server-cert.pem \
        -k certs/server-key.pem > log 2>&1

    # print output
    cat log

    # exit if ASan found something
    if grep AddressSanitizer log > /dev/null 2>&1; then
        exit 1
    fi
done
