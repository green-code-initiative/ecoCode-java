#!/usr/bin/env sh

# "sonar.login" variable : private TOKEN generated in your local SonarQube during installation
# (input paramater of this script)
mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184:sonar -Dsonar.login=$1
