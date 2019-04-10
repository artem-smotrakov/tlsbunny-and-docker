#!/bin/sh

git fetch --unshallow --quiet

./gradlew \
    --info \
    -Dtlsbunny.output.standard.level=achtung \
    -Dtlsbunny.sync.output=limited \
    -Dtlsbunny.output.enable.highlighting=false \
        clean test jacocoTestReport

sonar-scanner