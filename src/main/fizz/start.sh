#!/bin/bash

options=${options:-"server -accept 30101 -cert certs/server_cert.pem -key certs/server_key.pem"}

fizz ${options}
