Purpose of this project
---

To check locally all rules on java language.
To do this :

- first launch local development environment (SonarQube)
- launch sonar maven command to send sonar metrics to local SonarQube
- on local SonarQube, check if each Java class contains (or not) the rule error defined for this class

Step 1 : prepare local environment
---

To launch local environment : please follow https://github.com/green-code-initiative/ecoCode/blob/main/INSTALL.md
(especially SonarQube configuration part)

Step 1 : compile and build
---

`./tool_build.sh`

Step 2 : send Sonar metrics to local SonarQube
---

- first : change the token inside script (to give your personal SonarQube token, previously generated, please see install documention)
- secondly : launch `./tool_send_to_sonar.sh`

Step 3 : check errors
---

on local SonarQube, check if each Java class contains (or not) the rule error defined for this class
(for example : you can search for tag `eco-conception` rule on a special file)
