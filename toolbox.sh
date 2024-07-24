#!/usr/bin/env bash
# @name toolbox.sh
# @description
#   This toolbox enables you to install the SonarQube dev environment.

# Global variables
CURRENT_PATH="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)"
ECOCODE_DC_FILE="$CURRENT_PATH/docker-compose.yml"
ECOCODE_DOCKER_ENV="$CURRENT_PATH/.default.docker.env"
ECOCODE_JAVA_PLUGIN_VERSION=$(< "$CURRENT_PATH/pom.xml" grep "<version>"|head -n1|sed 's/<\(\/\)*version>//g'|xargs)
ECOCODE_JAVA_PLUGIN_JAR="$CURRENT_PATH/target/ecocode-java-plugin-$ECOCODE_JAVA_PLUGIN_VERSION.jar"

# Shell coloring
function colors() {
    case $1 in
        'R') echo -e '\033[0;31m' ;; # RED
        'G') echo -e '\033[0;32m' ;; # GREEN
        'B') echo -e '\033[0;34m' ;; # BLUE
        'Y') echo -e '\033[0;33m' ;; # YELLOW
        'W') echo -e '\033[0;37m' ;; # WHITE
        'N') echo -e '\033[0;0m' ;; # NOCOLOR
    esac
}

function info() {
    if [[ $TEST -gt 0 ]]; then
        echo "$*"
    else
        echo -e "$(colors 'W')$*$(colors 'N')"
    fi
    return 0
}

function debug() {
    if [[ $((VERBOSE+TEST)) -gt 0 ]]; then
        if [[ $TEST -gt 0 ]]; then
            echo "$*"
        else
            echo -e "$(colors 'B')$*$(colors 'N')"
        fi
    fi
    return 0
}

function error() {
    if [[ $TEST -gt 0 ]]; then
        >&2 echo -e "$*"
    else
        >&2 echo -e "$(colors 'R')$*$(colors 'N')"
    fi
    return 0
}

# @description Compile and package source code with maven.
# @exitcode 0 If successful.
# @exitcode 1 If an error was encountered when building source code.
# @exitcode 2 If the ecoCode plugin in target directory cannot be found.
function build() {
    info "Building source code in the target folder"
    if ! [[ -f $ECOCODE_JAVA_PLUGIN_JAR ]] || [[ $FORCE -gt 0 ]] || [[ $TEST -gt 0 ]]; then
        debug "mvn clean package -Dmaven.clean.failOnError=false -DskipTests" ; [[ $TEST -gt 0 ]] && return 0
        if ! mvn clean package -Dmaven.clean.failOnError=false -DskipTests; then
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
# @exitcode 0 If successful.
# @exitcode 1 If an error was encountered when compiling the source code.
function compile() {
    info "Compile source code"
    debug "mvn clean compile" ; [[ $TEST -gt 0 ]] && return 0
    if ! mvn clean compile; then
        return 1
    fi
    return 0
}

# @description Export environment variables from .default.docker.env file.
# @exitcode 0 If successful.
# @exitcode 1 If the environment file cannot be found.
function docker_env_source() {
    debug "source $ECOCODE_DOCKER_ENV"
    # To export variables
    set -a
    # shellcheck source=.default.docker.env
    ! source "$ECOCODE_DOCKER_ENV" 2&>/dev/null && error "Cannot find $ECOCODE_DOCKER_ENV" && return 1
    set +a
    return 0
}

# @description Build Docker services.
# @exitcode 0 If successful.
# @exitcode 1 If an error was encountered retrieving environment variables.
# @exitcode 2 If an error has been encountered when building services.
function docker_build() {
    ! docker_env_source && return 1
    [[ $FORCE -gt 0 ]] && rm -rf "$CURRENT_PATH/target/*"
    info "Build Docker services"
    debug "docker compose -f $ECOCODE_DC_FILE build" ; [[ $TEST -gt 0 ]] && return 0
    ! docker compose -f "$ECOCODE_DC_FILE" build && return 2
    return 0
}

# @description Building the ecoCode plugin and creating containers.
# @exitcode 0 If successful.
# @exitcode 1 If an error was encountered when building project code in the target folder.
# @exitcode 2 If an error was encountered retrieving environment variables.
# @exitcode 3 If an error was encountered during container creating.
function init() {
    ! build && return 1
    ! docker_env_source && return 2
    info "Creating and starting Docker containers"
    debug "docker compose -f $ECOCODE_DC_FILE up --build -d" ; [[ $TEST -gt 0 ]] && return 0
    ! docker compose -f "$ECOCODE_DC_FILE" up --build -d && return 3
    return 0
}

# @description Starting Docker containers.
# @exitcode 0 If successful.
# @exitcode 1 If an error was encountered retrieving environment variables.
# @exitcode 2 If the ecoCode plugin is not present in the target folder.
# @exitcode 3 If an error was encountered during container startup.
function start() {
    ! docker_env_source && return 1
    # Check that the plugin is present in the target folder
    if [[ $TEST -eq 0 ]] && ! [[ -f $ECOCODE_JAVA_PLUGIN_JAR ]]; then
        error "Cannot find ecoCode plugin in target directory" && return 2
    fi
    info "Starting Docker containers"
    debug "docker compose -f $ECOCODE_DC_FILE start" ; [[ $TEST -gt 0 ]] && return 0
    ! TOKEN=$ECOCODE_TOKEN docker compose -f "$ECOCODE_DC_FILE" start && return 3
    return 0
}

# @description Stopping Docker containers.
# @exitcode 0 If successful.
# @exitcode 1 If an error was encountered retrieving environment variables.
# @exitcode 2 If an error was encountered during container shutdown.
function stop() {
    ! docker_env_source && return 1
    info "Stopping Docker containers"
    debug "docker compose -f $ECOCODE_DC_FILE stop" ; [[ $TEST -gt 0 ]] && return 0
    ! docker compose -f "$ECOCODE_DC_FILE" stop && return 2
    return 0
}

# @description Stop and remove containers, networks and volumes.
# @exitcode 0 If successful.
# @exitcode 1 If an error was encountered retrieving environment variables.
# @exitcode 2 If an error was encountered during deletion.
function clean() {
    ! docker_env_source && return 1
    info "Remove Docker containers, networks and volumes"
    debug "docker compose -f $ECOCODE_DC_FILE down --volumes" ; [[ $TEST -gt 0 ]] && return 0
    ! docker compose -f "$ECOCODE_DC_FILE" down --volumes && return 2
    [[ $FORCE -gt 0 ]] && rm -rf "$CURRENT_PATH/target"
    return 0
}

# @description Display Docker container logs.
# @exitcode 0 If successful.
# @exitcode 1 If an error was encountered retrieving environment variables.
function display_logs() {
    ! docker_env_source && return 1
    info "Display Docker container logs"
    debug "docker compose -f $ECOCODE_DC_FILE logs -f" ; [[ $TEST -gt 0 ]] && return 0
    docker compose -f "$ECOCODE_DC_FILE" logs -f
    return 0
}

# @description Use maven plugin release to prepare locally next release and next SNAPSHOT.
# @exitcode 0 If successful.
# @exitcode 1 If an error is encountered when prepare the release.
# @exitcode 2 If an error is encountered when cleaning files.
function release() {
    info "Creation of 2 commits with release and next SNAPSHOT"
    debug "mvn release:prepare -B -ff -DpushChanges=false -DtagNameFormat=@{project.version}"
    if [[ $TEST -eq 0 ]]; then
        if ! mvn release:prepare -B -ff -DpushChanges=false -DtagNameFormat=@{project.version}; then
            return 1
        fi
    fi
    info "Clean temporary files"
    debug "mvn release:clean" ; [[ $TEST -gt 0 ]] && return 0
    sleep 2
    if ! mvn release:clean; then
        return 2
    fi
    return 0
}

# @description Create a push and a new branch with commits previously prepared.
# @exitcode 0 If successful.
# @exitcode 1 If the last commit tag does not match the last git tag.
function release_push() {
    info "Create a push and a new branch with commits previously prepared"
    [[ $TEST -gt 0 ]] && return 0
    local last_tag_prepare="" last_tag="" branch_name=""
    # Check that the release has been properly prepared
    last_tag_prepare=$(git log -2 --pretty=%B|grep "prepare release"|awk '{print $NF}')
    # Retrieve last tag
    last_tag=$(git tag --sort=-version:refname | head -n 1)
    # Check that the tag is correct
    if ! [[ "$last_tag_prepare" = "$last_tag" ]]; then
        error "The last commit tag does not match the last git tag"
        return 1
    fi
    # Checkout released tag and creation of branch to push (because of main protection)
    branch_name="release_${last_tag}"
    git checkout -b "${branch_name}"
    # push branch associated to new tag release
    git push --set-upstream origin "${branch_name}"
    return 0
}

# @description Display help.
# @exitcode 0 If successful.
function display_help() {
    local output=""
    output="
$(colors 'Y')Usage$(colors 'W') $(basename "$0") [OPTIONS] COMMAND
$(colors 'Y')Commands:$(colors 'N')
$(colors 'G')init$(colors 'W')                Initialize and creating containers
$(colors 'G')start$(colors 'W')               Starting Docker containers
$(colors 'G')stop$(colors 'W')                Stopping Docker containers
$(colors 'G')clean$(colors 'W')               Stop and remove containers, networks and volumes
$(colors 'G')uild$(colors 'W')                Build the ecoCode plugin
$(colors 'G')compile$(colors 'W')             Compile the ecoCode plugin
$(colors 'G')build-docker$(colors 'W')        Build Docker services
$(colors 'G')release$(colors 'W')             Create a new release
$(colors 'G')release-push$(colors 'W')        Push the new release
$(colors 'Y')Options:$(colors 'N')
$(colors 'G')--token=<TOKEN>$(colors 'W')     Creating containers with previously created token
$(colors 'G')-v, --verbose$(colors 'W')       Make the command more talkative
$(colors 'G')-l, --logs$(colors 'W')          Display Docker container logs
$(colors 'G')-f, --force$(colors 'W')         To delete the target folder or recompile the source code
$(colors 'G')-h, --help$(colors 'W')          Display help
    "
    echo -e "$output\n"|sed '1d; $d'
    return 0
}

# Check options passed as script parameters.
function check_opts() {
    read -ra opts <<< "$@"
    for opt in "${opts[@]}"; do
        case "$opt" in
            init) INIT=1 ; ARGS+=("$opt") ;;
            start) START=1 ; ARGS+=("$opt") ;;
            stop) STOP=1 ; ARGS+=("$opt") ;;
            clean) CLEAN=1 ; ARGS+=("$opt") ;;
            release) RELEASE=1 ; ARGS+=("$opt") ;;
            release-push) RELEASE_PUSH=1 ; ARGS+=("$opt") ;;
            build) BUILD=1 ; ARGS+=("$opt") ;;
            compile) COMPILE=1 ; ARGS+=("$opt") ;;
            build-docker) BUILD_DOCKER=1 ; ARGS+=("$opt") ;;
            --token=*) ECOCODE_TOKEN=$(echo "$opt"|awk -F= '{print $2}') ;;
            --verbose|-v) VERBOSE=1 ;;
            --logs|-l) DISPLAY_LOGS=1 ;;
            --force|-f) FORCE=1 ;;
            --test) TEST=1 ;;
            --fixture=*) FIXTURE=$(echo "$opt"|awk -F= '{print $2}') ;;
            --help) HELP=1 ;;
            *) ARGS+=("$opt") ;;
        esac
    done
    # Help is displayed if no option is passed as script parameter
    if [[ $((HELP+INIT+START+STOP+CLEAN+RELEASE+RELEASE_PUSH+BUILD+COMPILE+BUILD_DOCKER+DISPLAY_LOGS)) -eq 0 ]]; then
        HELP=1
    fi
    return 0
}

# Used by unit tests to execute a function.
function execute_unit_test() {
    if [[ -z "${ARGS[0]}" ]]; then
        error "No function to execute" && return 1
    fi
    # If a function is passed as the first argument, we check that it exists
    if [[ -n "${ARGS[0]}" ]] && ! [[ $(type -t "${ARGS[0]}") == function ]]; then
        error "Function with name ${ARGS[0]} does not exist" && return 2
    fi
    # Initialize fixtures
    [[ $FIXTURE -eq 1 ]] && ECOCODE_DOCKER_ENV="test_docker_env"
    # execute function
    "${ARGS[@]}"
    return $?
}

# Execute tasks based on script parameters or user actions.
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
    # Build the ecoCode plugin
    if [[ $BUILD -gt 0 ]]; then
        ! build && return 6
    fi
    # Compile the ecoCode plugin
    if [[ $COMPILE -gt 0 ]]; then
        ! compile && return 7
    fi
    # Build Docker services
    if [[ $BUILD_DOCKER -gt 0 ]]; then
        ! docker_build && return 8
    fi
    # Use maven plugin to create a new release
    if [[ $RELEASE -gt 0 ]]; then
        ! release && return 9
    fi
    # create an push an new branch with commits previously prepared
    if [[ $RELEASE_PUSH -gt 0 ]]; then
        ! release_push && return 10
    fi
    # Display Docker container logs
    if [[ $DISPLAY_LOGS -gt 0 ]]; then
        ! display_logs && return 11
    fi
    return 0
}

# Main function.
function main() {
    ARGS=()
    HELP=0 VERBOSE=0 FORCE=0 TEST=0 FIXTURE=0
    INIT=0 START=0 STOP=0 CLEAN=0
    RELEASE=0 BUILD=0 COMPILE=0 BUILD_DOCKER=0 DISPLAY_LOGS=0
    # Check options passed as script parameters and execute tasks
    ! check_opts "$@" && return 1
    # Used by unit tests to execute a function
    if [[ $TEST -gt 0 ]]; then
        execute_unit_test
        return $?
    fi
    # Execute one or more tasks according to script parameters
    ! execute_tasks && return $?
    return 0
}

main "$@"
