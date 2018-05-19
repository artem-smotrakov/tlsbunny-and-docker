#!/bin/bash

mkdir -p nssdb
certutil -N -d sql:nssdb --empty-password
openssl pkcs12 -export -passout pass: \
    -out localhost.p12 -inkey certs/server_key.pem -in certs/server_cert.pem \
    -name localhost
pk12util -i localhost.p12 -d sql:nssdb -W ''
