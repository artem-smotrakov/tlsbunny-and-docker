#!/bin/bash

options=${options:-"--port=50101 --debug=9999 --http \
                        --x509cafile certs/root_cert.pem \
                        --x509keyfile certs/server_key.pem \
                        --x509certfile certs/server_cert.pem \
                        --disable-client-cert \
                        --priority=NORMAL:-VERS-ALL:+VERS-TLS1.3:+VERS-TLS1.2"}

# looks like gnutls server can exit if a wrong message was received
# so run it in a loop

touch stop.file
echo "note: remove $(pwd)/stop.file to stop the server loop"
while [ -f stop.file ];
do
    gnutls-serv ${options} 2>&1
done
