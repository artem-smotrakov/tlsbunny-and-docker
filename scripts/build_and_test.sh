#!/bin/sh

git fetch --unshallow --quiet

./gradlew clean

./gradlew \
    --info \
    -Ptlsbunny.test.exclude=com/gypsyengineer/tlsbunny/vendor/test/tls13/** \
    -Dtlsbunny.output.standard.level=achtung \
    -Dtlsbunny.sync.output=limited \
    -Dtlsbunny.output.enable.highlighting=false \
        test

./gradlew \
    --info \
    -Ptlsbunny.test.include=com/gypsyengineer/tlsbunny/vendor/test/tls13/** \
    -Dtlsbunny.output.standard.level=achtung \
    -Dtlsbunny.sync.output=limited \
    -Dtlsbunny.output.enable.highlighting=false \
        test

./gradlew jacocoTestReport

sonar-scanner