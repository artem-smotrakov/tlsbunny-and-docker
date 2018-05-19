#!/bin/bash

selfserv -v -d sql:./nssdb -p 60101 -V tls1.3:tls1.3 -H 1 -n localhost
