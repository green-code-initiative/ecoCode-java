# toolbox.sh

**toolbox.sh** is a utility script for installing the SonarQube dev environment.

## Overview

This toolbox enables you to install the SonarQube dev environment.

## Index

* [docker_env_source](#dockerenvsource)
* [docker_build](#dockerbuild)
* [init](#init)
* [start](#start)
* [stop](#stop)
* [clean](#clean)
* [display_logs](#displaylogs)
* [build](#build)
* [compile](#compile)
* [release](#release)
* [release_push](#releasepush)
* [display_help](#displayhelp)

### docker_env_source

Export environment variables from .default.docker.env file.

#### Exit codes

* **0**: If successful.
* **1**: If the environment file cannot be found.

### docker_build

Build Docker services.

#### Exit codes

* **0**: If successful.
* **1**: If an error was encountered retrieving environment variables.
* **2**: If an error has been encountered when building services.

### init

Building the ecoCode plugin and creating containers.

#### Exit codes

* **0**: If successful.
* **1**: If an error was encountered when building project code in the target folder.
* **2**: If an error was encountered retrieving environment variables.
* **3**: If an error was encountered during container creating.

### start

Starting Docker containers.

#### Exit codes

* **0**: If successful.
* **1**: If the ecoCode plugin is not present in the target folder.
* **2**: If an error was encountered retrieving environment variables.
* **3**: If an error was encountered during container startup.

### stop

Stopping Docker containers.

#### Exit codes

* **0**: If successful.
* **1**: If an error was encountered retrieving environment variables.
* **2**: If an error was encountered during container shutdown.

### clean

Stop and remove containers, networks and volumes.

#### Exit codes

* **0**: If successful.
* **1**: If an error was encountered retrieving environment variables.
* **2**: If an error was encountered during deletion.

### display_logs

Display Docker container logs.

#### Exit codes

* **0**: If successful.
* **1**: If an error was encountered retrieving environment variables.

### build

Compile and package source code with maven.

#### Exit codes

* **0**: If successful.
* **1**: If an error was encountered when building source code.
* **2**: If the ecoCode plugin in target directory cannot be found.

### compile

Compile source code with maven.

#### Exit codes

* **0**: If successful.
* **1**: If an error was encountered when compiling the source code.

### release

Use maven plugin release to prepare locally next release and next SNAPSHOT.

#### Exit codes

* **0**: If successful.
* **1**: If an error is encountered when prepare the release.
* **2**: If an error is encountered when cleaning files.

### release_push

Create a push and a new branch with commits previously prepared

#### Exit codes

* **0**: If successful.
* **1**: If an error is encountered when push the release.

### display_help

Display help.

#### Exit codes

* **0**: If successful.

