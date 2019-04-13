#!/bin/bash

options=${options:-"--port=50101 --debug=9999 --http \
                        --x509cafile certs/root_cert.pem \
                        --x509keyfile certs/server_key.pem \
                        --x509certfile certs/server_cert.pem \
                        --disable-client-cert \
                        --priority="NORMAL:-VERS-ALL:+VERS-TLS1.3:+VERS-TLS1.2"}

gnutls-serv ${options}
