EcoCode-java
===========

_ecoCode_ is a collective project aiming to reduce environmental footprint of software at the code level. The goal of
the project is to provide a list of static code analyzers to highlight code structures that may have a negative
ecological impact: energy and resources over-consumption, "fatware", shortening terminals' lifespan, etc.

_ecoCode_ is based on evolving catalogs
of [good practices](https://github.com/green-code-initiative/ecoCode/blob/main/docs/rules), for various technologies.
This
SonarQube plugin then implements these catalogs as rules for scanning your Java projects.

> ⚠️ This is still a very early stage project. Any feedback or contribution will be highly appreciated. Please
> refer to the contribution section.

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg)](https://github.com/green-code-initiative/ecoCode-common/blob/main/doc/CODE_OF_CONDUCT.md)

🌿 SonarQube Plugins
-------------------

This plugin is part of the ecoCode project.\
You can find a list of all our other plugins in
the [ecoCode repository](https://github.com/green-code-initiative/ecoCode#-sonarqube-plugins)

🚀 Getting Started
------------------

You can give a try with a one command docker :

```sh
docker run -ti --rm \
       -p 9000:9000 \
       --name sonarqube-ecocode-java ghcr.io/green-code-initiative/sonarqube-ecocode-java:latest
```

or (with logs and data locally stored) :

```sh
docker run -ti --rm \
       -v sq_ecocode_logs:/opt/sonarqube/logs \
       -v sq_ecocode_data:/opt/sonarqube/data \
       -p 9000:9000 \
       --name sonarqube-ecocode-java ghcr.io/green-code-initiative/sonarqube-ecocode-java:latest
```

... and configure local SonarQube (security config and quality profile : see [configuration](https://github.com/green-code-initiative/ecoCode-common/blob/main/doc/INSTALL.md#configuration-sonarqube) for more details).

To install other `ecocode` plugins, you can also :

- download each plugin separatly and copy the plugin (jar file) to `$SONAR_INSTALL_DIR/extensions/plugins` and restart SonarQube.
- install different ecocode plugins with Marketplace (inside admin panel of SonarQube)

Then you can use Java test project repository to test the environment : see README.md of [Java test project](https://github.com/green-code-initiative/ecoCode-java-test-project)

Finally, you can directly use a [all-in-one docker-compose](https://github.com/green-code-initiative/ecoCode-common/blob/main/doc/INSTALL.md#start-sonarqube-if-first-time)

🛒 Distribution
------------------

Ready to use binaries are available [from GitHub](https://github.com/green-code-initiative/ecoCode-java/releases).

🧩 Compatibility
-----------------

| Plugin version | SonarQube version   | Java version |
|----------------|---------------------|--------------|
| 1.5.+          | 9.4.+ LTS to 10.5.1 | 11 / 17      |

> Compatibility table of versions lower than 1.4.+ are available from the
> main [ecoCode repository](https://github.com/green-code-initiative/ecoCode#-plugins-version-compatibility).

🤝 Contribution
---------------

check [ecoCode repository](https://github.com/green-code-initiative/ecoCode#-contribution)

🤓 Main contributors
--------------------

check [ecoCode repository](https://github.com/green-code-initiative/ecoCode#-main-contributors)

Links
-----

- https://docs.sonarqube.org/latest/analysis/overview/
