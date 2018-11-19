#!/bin/bash

# looks like wolfssl server can exit if a wrong message was received
# so run it in a loop

touch stop.file
echo "note: remove $(pwd)/stop.file to stop the server loop"
while [ -f stop.file ];
do
    ./examples/server/server \
        -p 40101 \
        -v 4 \
        -d -i -g -b -x \
        -c certs/server-cert.pem \
        -k certs/server-key.pem 2>&1
done
