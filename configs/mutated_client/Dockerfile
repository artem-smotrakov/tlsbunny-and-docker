# this dockerfile builds tlsbunny and runs a fuzzer
#
# the following command builds a docker image
# it should be run from the root of tlsbunny repository
#
# docker build --file configs/mutated_client/Dockerfile --tag tlsbunny/mutated/client .
#
# the following command runs a fuzzer
#
# tbd
#
# happy fuzzing!
#

FROM openjdk:9

MAINTAINER artem.smotrakov@gmail.com

ENV TLSBUNNY /var/src/tlsbunny
RUN mkdir -p ${TLSBUNNY}
ADD . ${TLSBUNNY}

WORKDIR  ${TLSBUNNY}
RUN mkdir classes
RUN javac -d classes -classpath src:libs/snakeyaml-1.19.jar src/com/gypsyengineer/tlsbunny/Main.java

# run with default config
CMD [ "java", "-classpath", "classes:libs/snakeyaml-1.19.jar", "com.gypsyengineer.tlsbunny.Main" ]