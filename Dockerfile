# syntax=docker/dockerfile:1

ARG MAVEN_BUILDER_VERSION
ARG SONARQUBE_VERSION

FROM maven:${MAVEN_BUILDER_VERSION} AS builder

ARG ECOCODE_SRC_PATH=/usr/src/ecocode

COPY . ${ECOCODE_SRC_PATH}

WORKDIR ${ECOCODE_SRC_PATH}
RUN ${ECOCODE_SRC_PATH}/toolbox.sh build

FROM sonarqube:${SONARQUBE_VERSION}

ARG ECOCODE_SRC_PATH=/usr/src/ecocode
ARG SONARQUBE_PLUGINS_PATH=/opt/sonarqube/extensions/plugins/

COPY --from=builder ${ECOCODE_SRC_PATH}/target/ecocode-*.jar ${SONARQUBE_PLUGINS_PATH}
USER sonarqube
