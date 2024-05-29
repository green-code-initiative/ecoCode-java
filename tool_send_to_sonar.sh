#!/usr/bin/env sh

# "sonar.token" variable (or sonar.login before SONARQUBE 9.9) : private TOKEN generated in your local SonarQube during installation
# (input paramater of this script)
mvn clean org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184:sonar -Dsonar.token=$1
# mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184:sonar -Dsonar.token=$1 -Dsonar.host.url=https://sonar-staging.gcp.cicd.solocal.com/

# command if you have a SONARQUBE < 9.9 (sonar.token existing for SONARQUBE >= 10.0)
# mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184:sonar -Dsonar.login=$1
