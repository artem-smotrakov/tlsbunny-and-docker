#!/bin/bash

mkdir -p nssdb
certutil -N -d sql:nssdb --empty-password
openssl pkcs12 -export -passout pass: \
    -out localhost.p12 -inkey certs/server_key.pem -in certs/server_cert.pem \
    -name localhost
openssl pkcs12 -export -passout pass: \
    -out client.p12 -inkey certs/client_key.pem -in certs/client_cert.pem \
    -name client
openssl pkcs12 -export -passout pass: \
    -out root.p12 -inkey certs/root_key.pem -in certs/root_cert.pem \
    -name root
pk12util -i localhost.p12 -d sql:nssdb -W ''
pk12util -i client.p12 -d sql:nssdb -W ''
pk12util -i root.p12 -d sql:nssdb -W ''

certutil -M -n client -t P -d sql:nssdb --empty-password
certutil -L -d sql:nssdb --empty-password
