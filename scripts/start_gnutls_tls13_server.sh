#!/bin/bash

gnutls-serv --port=20101 --debug=9999 --http \
    --x509cafile certs/root_cert.pem \
    --x509keyfile certs/server_key.pem \
    --x509certfile certs/server_cert.pem \
    ${OPTIONS}
