#!/bin/bash

openssl_src=${1}
options=${options:-"-key certs/server_key.pem -cert certs/server_cert.der -certform der -accept 10101 -www -tls1_3"}
mode=${mode:-"server"}
port=${port:-"10101"}
enable_coverage=${enable_coverage:-"no"}

if [ "${enable_coverage}" == "yes" ]; then
    lcov --zerocounters --directory ${openssl_src}
    lcov --no-external --capture --initial --directory ${openssl_src} --output-file base.info
fi

if [ "${mode}" == "client" ]; then
    touch stop.file
    echo "note: remove $(pwd)/stop.file to stop the client loop"
    while [ -f stop.file ]; do
        echo -e "GET / HTTP/1.0\r\n\r\n" | openssl s_client ${options} -connect 127.0.0.1:${port}
    done
else
    openssl s_server ${options}
fi

if [ "${enable_coverage}" == "yes" ]; then
    timestamp=$(date +%s)
    cc_report="/var/reports/cc_report.${timestamp}"
    mkdir ${cc_report}
    lcov --no-external --capture --directory ${openssl_src} --output-file test.info
    lcov --add-tracefile base.info --add-tracefile test.info --output-file total.info
    lcov --remove total.info "${openssl_dir}/test/*" "${openssl_dir}/fuzz/*" --output-file total.info
    genhtml --ignore-errors source total.info --legend --title openssl --output-directory=${cc_report}
fi
