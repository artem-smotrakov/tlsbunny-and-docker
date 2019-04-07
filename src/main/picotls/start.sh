#!/bin/bash

echo "I am a picotls server which supports TLS 1.3" > message
${PICOTLS}/cli -c certs/server_cert.pem -k certs/server_key.pem -i message 0.0.0.0 20101