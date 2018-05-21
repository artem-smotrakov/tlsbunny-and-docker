#!/bin/bash

for name in "$@"
do
    echo "check container: ${name}"
    docker logs ${name} 2>&1 | grep AddressSanitizer && exit 1 || echo ok
done
