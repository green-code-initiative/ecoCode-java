#!/usr/bin/env sh

# "sonar.login" variable : private TOKEN generated in your local SonarQube during installation
mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184:sonar -Dsonar.login=sqa_919e0287178896de96aa019e300e85a93c9acc2d
