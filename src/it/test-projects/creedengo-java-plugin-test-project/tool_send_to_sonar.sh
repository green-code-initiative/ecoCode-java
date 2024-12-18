#!/usr/bin/env sh

# "sonar.login" kept only for SONARQUBE < 10
mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.11.0.3922:sonar -Dsonar.host.url=http://localhost:$1 -Dsonar.login=$2 -Dsonar.token=$2
