#!/usr/bin/env bash
# @name toolbox.sh
# @brief **toolbox.sh** is a utility script for installing the SonarQube dev environment.
# @description
#   This toolbox enables you to install the SonarQube dev environment.

# Global variables
CURRENT_PATH="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)"
ECOCODE_DC_FILE="$CURRENT_PATH/docker-compose.yml"
ECOCODE_DOCKER_ENV="$CURRENT_PATH/.default.docker.env"
ECOCODE_JAVA_PLUGIN_VERSION=$(< "$CURRENT_PATH/pom.xml" grep "<version>"|head -n1|sed 's/<\(\/\)*version>//g'|xargs)
ECOCODE_JAVA_PLUGIN_JAR="$CURRENT_PATH/target/ecocode-java-plugin-$ECOCODE_JAVA_PLUGIN_VERSION.jar"

# Shell coloring
declare -A COLORS=(
    [RED]='\033[0;31m'
    [GREEN]='\033[0;32m'
    [YELLOW]='\033[0;33m'
    [BLUE]='\033[0;34m'
    [WHITE]='\033[0;37m'
    [NOCOLOR]='\033[0;0m'
)

# @description Display an information message.
# @noargs
# @exitcode 0 If successful.
function info() {
    echo -e "${COLORS[WHITE]}$*${COLORS[NOCOLOR]}"
    return 0
}

# @description Display an debug message.
# @noargs
# @exitcode 0 If successful.
function debug() {
    [[ $VERBOSE -gt 0 ]] && echo -e "${COLORS[BLUE]}$*${COLORS[NOCOLOR]}"
    return 0
}

# @description Display an error message.
# @noargs
# @exitcode 0 If successful.
function error() {
    echo -e "${COLORS[RED]}$*${COLORS[NOCOLOR]}"
    return 0
}

# @description Export environment variables from .default.docker.env file.
# @noargs
# @exitcode 0 If successful.
# @exitcode 1 If the environment file cannot be found.
function docker_env_source() {
    debug "source $ECOCODE_DOCKER_ENV"
    # To export variables
    set -a
    # shellcheck source=.default.docker.env
    ! source "$ECOCODE_DOCKER_ENV" && error "Cannot find $ECOCODE_DOCKER_ENV" && return 1
    set +a
    return 0
}

# @description Build Docker services.
# @noargs
# @exitcode 0 If successful.
# @exitcode 1 If an error was encountered retrieving environment variables.
# @exitcode 2 If an error has been encountered when building services.
function docker_build() {
    ! docker_env_source && return 1
    [[ $FORCE -gt 0 ]] && rm -rf "$CURRENT_PATH/target"
    info "Build Docker services"
    debug "docker compose -f $ECOCODE_DC_FILE build"
    ! docker compose -f "$ECOCODE_DC_FILE" build && return 2
    return 0
}

# @description Display Docker service logs.
# @noargs
# @exitcode 0 If successful.
# @exitcode 1 If an error was encountered retrieving environment variables.
function docker_logs() {
    ! docker_env_source && return 1
    info "Display Docker container logs"
    debug "docker compose -f $ECOCODE_DC_FILE logs -f"
    docker compose -f "$ECOCODE_DC_FILE" logs -f
    return 0
}

# @description Building the ecoCode plugin and creating containers.
# @noargs
# @exitcode 0 If successful.
# @exitcode 1 If an error was encountered when building project code in the target folder.
# @exitcode 2 If an error was encountered retrieving environment variables.
# @exitcode 3 If an error was encountered during container creating.
function init() {
    ! build && return 1
    ! docker_env_source && return 2
    info "Creating and starting Docker containers"
    debug "docker compose -f $ECOCODE_DC_FILE up --build -d"
    ! docker compose -f "$ECOCODE_DC_FILE" up --build -d && return 3
    return 0
}

# @description Starting Docker containers.
# @noargs
# @exitcode 0 If successful.
# @exitcode 1 If the ecoCode plugin is not present in the target folder.
# @exitcode 2 If an error was encountered retrieving environment variables.
# @exitcode 3 If an error was encountered during container startup.
function start() {
    # Check that the plugin is present in the target folder
    if ! [[ -f $ECOCODE_JAVA_PLUGIN_JAR ]]; then
        error "Cannot find ecoCode plugin in target directory" && return 1
    fi
    ! docker_env_source && return 2
    info "Starting Docker containers"
    debug "docker compose -f $ECOCODE_DC_FILE start"
    ! TOKEN=$ECOCODE_TOKEN docker compose -f "$ECOCODE_DC_FILE" start && return 3
    return 0
}

# @description Stopping Docker containers.
# @noargs
# @exitcode 0 If successful.
# @exitcode 1 If an error was encountered retrieving environment variables.
# @exitcode 2 If an error was encountered during container shutdown.
function stop() {
    ! docker_env_source && return 1
    info "Stopping Docker containers"
    debug "docker compose -f $ECOCODE_DC_FILE stop"
    ! docker compose -f "$ECOCODE_DC_FILE" stop && return 2
    return 0
    return 0
}

# @description Stop and remove containers, networks and volumes.
# @noargs
# @exitcode 0 If successful.
# @exitcode 1 If an error was encountered retrieving environment variables.
# @exitcode 2 If an error was encountered during deletion.
function clean() {
    ! docker_env_source && return 1
    info "Remove Docker containers, networks and volumes"
    debug "docker compose -f $ECOCODE_DC_FILE down --volumes"
    ! docker compose -f "$ECOCODE_DC_FILE" down --volumes && return 2

    [[ $FORCE -gt 0 ]] && rm -rf "$CURRENT_PATH/target"
    return 0
}

# @description Use maven plugin release to prepare locally next release and next SNAPSHOT.
# @noargs
# @exitcode 0 If successful.
# @exitcode 1 If an error is encountered when prepare the release.
# @exitcode 2 If an error is encountered when cleaning files.
function release() {
    # creation of 2 commits with release and next SNAPSHOT
    if ! mvn release:prepare -B -ff -DpushChanges=false -DtagNameFormat=@{project.version}; then
        return 1
    fi
    sleep 2
    # Clean temporary files
    if ! mvn release:clean; then
        return 2
    fi
    return 0
}

# @description Compile and package source code with maven.
# @noargs
# @exitcode 0 If successful.
# @exitcode 1 If an error was encountered when building source code.
# @exitcode 2 If the ecoCode plugin in target directory cannot be found.
function build() {
    info "Building source code in the target folder"
    if ! [[ -f $ECOCODE_JAVA_PLUGIN_JAR ]] || [[ $FORCE -gt 0 ]]; then
        debug "mvn clean package -DskipTests"
        if ! mvn clean package -DskipTests; then
            return 1
        fi
    fi
    # Check that the plugin is present in the target folder
    if ! [[ -f $ECOCODE_JAVA_PLUGIN_JAR ]]; then
        error "Cannot find ecoCode plugin in target directory" && return 2
    fi
    return 0
}

# @description Compile source code with maven.
# @noargs
# @exitcode 0 If successful.
# @exitcode 1 If an error was encountered when compiling the source code.
function compile() {
    info "Compile source code"
    debug "mvn clean compile"
    if ! mvn clean compile; then
        return 1
    fi
    return 0
}

# @description Display Docker container logs.
# @noargs
# @exitcode 0 If successful.
function display_logs() {
    ! docker_logs && return 1
    return 0
}

# @description Check options passed as script parameters.
# @noargs
# @exitcode 0 If successful.
function check_opts() {
    read -ra opts <<< "$@"
    for opt in "${opts[@]}"; do
        case "$opt" in
            init) INIT=1 ;;
            start) START=1 ;;
            stop) STOP=1 ;;
            clean) CLEAN=1 ;;
            release) RELEASE=1 ;;
            build) BUILD=1 ;;
            compile) COMPILE=1 ;;
            build-docker) BUILD_DOCKER=1 ;;
            --token=*) ECOCODE_TOKEN=$(echo "$opt"|awk -F= '{print $2}') ;;
            --logs) DISPLAY_LOGS=1 ;;
            --verbose) VERBOSE=1 ;;
            --force) FORCE=1 ;;
            --help) HELP=1 ;;
            *) ARGS+=("$opt") ;;
        esac
    done
    # Help is displayed if no option is passed as script parameter
    if [[ $((HELP+INIT+START+STOP+CLEAN+RELEASE+BUILD+COMPILE+BUILD_DOCKER+DISPLAY_LOGS)) -eq 0 ]]; then
        HELP=1
    fi
    return 0
}

# @description Used by unit tests to execute a function.
# @noargs
# @exitcode 0 If successful.
# @exitcode >0 If an error has been encountered while executing a function
function execute_function() {
    if ! [[ $(type -t "${ARGS[0]}") == function ]]; then
        error "Function with name ${ARGS[0]} does not exist" && return 1
    fi
    eval "${ARGS[@]}"
    return $?
}

# @description Execute tasks based on script parameters or user actions.
# @noargs
# @exitcode 0 If successful.
# @exitcode 1 If an error has been encountered displaying help.
# @exitcode 2 If an error is encountered when building the ecoCode plugin.
# @exitcode 3 If an error is encountered when compiling the ecoCode plugin.
# @exitcode 4 If an error is encountered when building Docker services.
# @exitcode 5 If an error was encountered while initialize docker compose.
# @exitcode 6 If an error is encountered when starting Docker containers.
# @exitcode 7 If an error is encountered when stopping Docker containers.
# @exitcode 8 If an error is encountered when cleaning Docker containers.
# @exitcode 9 If an error is encountered when displaying Docker logs.
function execute_tasks() {
    # Display help
    if [[ $HELP -gt 0 ]]; then
        ! display_help && return 1
        return 0
    fi
    # Building the ecoCode plugin and creating Docker containers
    if [[ $INIT -gt 0 ]]; then
        ! init && return 2
    fi
    # Starting Docker containers
    if [[ $START -gt 0 ]]; then
        ! start && return 3
    fi
    # Stopping Docker containers
    if [[ $STOP -gt 0 ]]; then
        ! stop && return 4
    fi
    # Stop and remove containers, networks and volumes
    if [[ $CLEAN -gt 0 ]]; then
        ! clean && return 5
    fi
    # Use maven plugin to create a new release
    if [[ $RELEASE -gt 0 ]]; then
        ! release && return 6
    fi
    # create an push an new branch with commits previously prepared
    if [[ $RELEASE_PUSH -gt 0 ]]; then
        ! release_push && return 7
    fi
    # Build the ecoCode plugin
    if [[ $BUILD -gt 0 ]]; then
        ! build && return 8
    fi
    # Compile the ecoCode plugin
    if [[ $COMPILE -gt 0 ]]; then
        ! compile && return 9
    fi
    # Build Docker services
    if [[ $BUILD_DOCKER -gt 0 ]]; then
        ! docker_build && return 10
    fi
    # Display Docker container logs
    if [[ $DISPLAY_LOGS -gt 0 ]]; then
        ! display_logs && return 11
    fi

    return 0
}

# @description Display help.
# @noargs
# @exitcode 0 If successful.
function display_help() {
    local output=""
    output="
${COLORS[YELLOW]}Usage${COLORS[WHITE]} $(basename "$0") [OPTIONS] COMMAND
${COLORS[YELLOW]}Commands:${COLORS[NOCOLOR]}
${COLORS[GREEN]}init${COLORS[WHITE]}                Initialize and creating containers
${COLORS[GREEN]}start${COLORS[WHITE]}               Starting Docker containers
${COLORS[GREEN]}stop${COLORS[WHITE]}                Stopping Docker containers
${COLORS[GREEN]}clean${COLORS[WHITE]}               Stop and remove containers, networks and volumes
${COLORS[GREEN]}release${COLORS[WHITE]}             Create a new release
${COLORS[GREEN]}build${COLORS[WHITE]}               Build the ecoCode plugin
${COLORS[GREEN]}compile${COLORS[WHITE]}             Compile the ecoCode plugin
${COLORS[GREEN]}build-docker${COLORS[WHITE]}        Build Docker services
${COLORS[YELLOW]}Options:${COLORS[NOCOLOR]}
${COLORS[GREEN]}-l, --logs${COLORS[WHITE]}          Display Docker container logs
${COLORS[GREEN]}-f, --force${COLORS[WHITE]}         To delete the target folder or recompile the source code
${COLORS[GREEN]}--token=<TOKEN>${COLORS[WHITE]}     Creating containers with previously created token
${COLORS[GREEN]}-h, --help${COLORS[WHITE]}          Display help
${COLORS[GREEN]}-v, --verbose${COLORS[WHITE]}       Make the command more talkative
    "
    echo -e "$output\n"|sed '1d; $d'
    return 0
}

# @description Main function.
# @noargs
# @exitcode 0 If successful.
# @exitcode 1 If the options check failed.
# @exitcode 2 If task execution failed.
function main() {
    ARGS=()
    HELP=0 VERBOSE=0 FORCE=0
    INIT=0 START=0 STOP=0 CLEAN=0
    RELEASE=0 BUILD=0 COMPILE=0 BUILD_DOCKER=0 DISPLAY_LOGS=0
    # Check options passed as script parameters and execute tasks
    ! check_opts "$@" && return 1
    # Used by unit tests to execute a function
    if [[ -n "${ARGS[0]}" ]]; then
        execute_function
        return $?
    fi
    # Execute one or more tasks according to script parameters
    ! execute_tasks && return 2
    return 0
}

main "$@"
