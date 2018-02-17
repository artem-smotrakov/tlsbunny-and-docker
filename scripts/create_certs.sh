#!/bin/bash

openssl ecparam -out server_key.pem -name secp256r1 -genkey
openssl req -new -key server_key.pem -out server_req.pem -sha256 -subj /CN=Server
openssl x509 -req -days 3650 -in server_req.pem -signkey server_key.pem -out server_cert.der -sha256 -outform der
openssl pkcs8 -topk8 -nocrypt -inform pem -outform der -in server_key.pem -out server_key.pkcs8

openssl ecparam -out client_key.pem -name secp256r1 -genkey
openssl req -new -key client_key.pem -out client_req.pem -sha256 -subj /CN=Client
openssl x509 -req -days 3650 -in client_req.pem -signkey client_key.pem -out client_cert.der -sha256 -outform der
openssl pkcs8 -topk8 -nocrypt -inform pem -outform der -in client_key.pem -out client_key.pkcs8
