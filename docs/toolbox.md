# toolbox.sh

**toolbox.sh** is a utility script for installing the SonarQube dev environment.

## Overview

This toolbox enables you to install the SonarQube dev environment.

## Index

* [info](#info)
* [debug](#debug)
* [error](#error)
* [docker_env_source](#dockerenvsource)
* [docker_build](#dockerbuild)
* [docker_logs](#dockerlogs)
* [init](#init)
* [start](#start)
* [stop](#stop)
* [clean](#clean)
* [release](#release)
* [build](#build)
* [compile](#compile)
* [display_logs](#displaylogs)
* [check_opts](#checkopts)
* [execute_function](#executefunction)
* [execute_tasks](#executetasks)
* [display_help](#displayhelp)
* [main](#main)

### info

Display an information message.

_Function has no arguments._

#### Exit codes

* **0**: If successful.

### debug

Display an debug message.

_Function has no arguments._

#### Exit codes

* **0**: If successful.

### error

Display an error message.

_Function has no arguments._

#### Exit codes

* **0**: If successful.

### docker_env_source

Export environment variables from .default.docker.env file.

_Function has no arguments._

#### Exit codes

* **0**: If successful.
* **1**: If the environment file cannot be found.

### docker_build

Build Docker services.

_Function has no arguments._

#### Exit codes

* **0**: If successful.
* **1**: If an error was encountered retrieving environment variables.
* **2**: If an error has been encountered when building services.

### docker_logs

Display Docker service logs.

_Function has no arguments._

#### Exit codes

* **0**: If successful.
* **1**: If an error was encountered retrieving environment variables.

### init

Building the ecoCode plugin and creating containers.

_Function has no arguments._

#### Exit codes

* **0**: If successful.
* **1**: If an error was encountered when building project code in the target folder.
* **2**: If an error was encountered retrieving environment variables.
* **3**: If an error was encountered during container creating.

### start

Starting Docker containers.

_Function has no arguments._

#### Exit codes

* **0**: If successful.
* **1**: If the ecoCode plugin is not present in the target folder.
* **2**: If an error was encountered retrieving environment variables.
* **3**: If an error was encountered during container startup.

### stop

Stopping Docker containers.

_Function has no arguments._

#### Exit codes

* **0**: If successful.
* **1**: If an error was encountered retrieving environment variables.
* **2**: If an error was encountered during container shutdown.

### clean

Stop and remove containers, networks and volumes.

_Function has no arguments._

#### Exit codes

* **0**: If successful.
* **1**: If an error was encountered retrieving environment variables.
* **2**: If an error was encountered during deletion.

### release

Use maven plugin release to prepare locally next release and next SNAPSHOT.

_Function has no arguments._

#### Exit codes

* **0**: If successful.
* **1**: If an error is encountered when prepare the release.
* **2**: If an error is encountered when cleaning files.

### build

Compile and package source code with maven.

_Function has no arguments._

#### Exit codes

* **0**: If successful.
* **1**: If an error was encountered when building source code.
* **2**: If the ecoCode plugin in target directory cannot be found.

### compile

Compile source code with maven.

_Function has no arguments._

#### Exit codes

* **0**: If successful.
* **1**: If an error was encountered when compiling the source code.

### display_logs

Display Docker container logs.

_Function has no arguments._

#### Exit codes

* **0**: If successful.

### check_opts

Check options passed as script parameters.

_Function has no arguments._

#### Exit codes

* **0**: If successful.

### execute_function

Used by unit tests to execute a function.

_Function has no arguments._

#### Exit codes

* **0**: If successful.
* **>0**: If an error has been encountered while executing a function

### execute_tasks

Execute tasks based on script parameters or user actions.

_Function has no arguments._

#### Exit codes

* **0**: If successful.
* **1**: If an error has been encountered displaying help.
* **2**: If an error is encountered when building the ecoCode plugin.
* **3**: If an error is encountered when compiling the ecoCode plugin.
* **4**: If an error is encountered when building Docker services.
* **5**: If an error was encountered while initialize docker compose.
* **6**: If an error is encountered when starting Docker containers.
* **7**: If an error is encountered when stopping Docker containers.
* **8**: If an error is encountered when cleaning Docker containers.
* **9**: If an error is encountered when displaying Docker logs.

### display_help

Display help.

_Function has no arguments._

#### Exit codes

* **0**: If successful.

### main

Main function.

_Function has no arguments._

#### Exit codes

* **0**: If successful.
* **1**: If the options check failed.
* **2**: If task execution failed.

