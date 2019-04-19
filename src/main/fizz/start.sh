#!/bin/bash

options=${options:-"server -accept 30101 -loop -http -cert certs/server_cert.pem -key certs/server_key.pem"}

ASAN_OPTIONS="detect_leaks=0" /var/src/fizz/build_/bin/fizz ${options}
