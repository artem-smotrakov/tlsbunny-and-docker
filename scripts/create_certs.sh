#!/bin/bash

openssl ecparam -out root_key.pem -name secp256r1 -genkey
openssl req -new -key root_key.pem -out root_req.pem -sha256 -subj /CN=Root
openssl x509 -req -days 3650 -in root_req.pem -signkey root_key.pem -out root_cert.der -sha256 -outform der
openssl pkcs8 -topk8 -nocrypt \
    -in root_key.pem -inform pem -out root_key.pkcs8 -outform der
openssl x509 -in root_cert.der -inform der -out root_cert.pem -outform pem

openssl ecparam -out server_key.pem -name secp256r1 -genkey
openssl req -new -key server_key.pem -out server_req.pem -sha256 -subj /CN=Server
openssl x509 -req -days 3650 -sha256 \
    -in server_req.pem -out server_cert.der -outform der \
    -CA root_cert.pem -CAkey root_key.pem -set_serial 2
openssl pkcs8 -topk8 -nocrypt \
    -in server_key.pem -inform pem -out server_key.pkcs8 -outform der
openssl x509 -in server_cert.der -inform der -out server_cert.pem -outform pem

openssl ecparam -out client_key.pem -name secp256r1 -genkey
openssl req -new -key client_key.pem -out client_req.pem -sha256 -subj /CN=Client
openssl x509 -req -days 3650 -sha256 \
    -in client_req.pem -out client_cert.der -outform der \
    -CA root_cert.pem -CAkey root_key.pem -set_serial 2
openssl pkcs8 -topk8 -nocrypt \
    -in client_key.pem -inform pem -out client_key.pkcs8 -outform der
openssl x509 -in client_cert.der -inform der -out client_cert.pem -outform pem
