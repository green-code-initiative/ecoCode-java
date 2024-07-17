#!/usr/bin/env bash
# @name utils_bash.sh
# @brief **utils_bash.sh** is a utility script for the toolbox.sh.
# @description
#   This utility script enables you to perform the following actions:
#
#       * Run unit tests with **pytest**
#       * Linter the code with the **shellcheck** utility
#       * Generating the API documentation with the **shdoc** utility
#       * Generating a site from markdown files with **mkdocs**

# Global variables
CURRENT_PATH="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)"
DOC_PATH="$CURRENT_PATH/docs"

# Shell coloring
declare -A COLORS=(
    [RED]='\033[0;31m'
    [GREEN]='\033[0;32m'
    [YELLOW]='\033[0;33m'
    [BLUE]='\033[0;34m'
    [WHITE]='\033[0;37m'
    [NOCOLOR]='\033[0;0m'
)

# Display an information message.
function info() {
    echo -e "${COLORS[WHITE]}$*${COLORS[NOCOLOR]}"
    return 0
}

# Display an debug message.
function debug() {
    [[ $VERBOSE -gt 0 ]] && echo -e "${COLORS[BLUE]}$*${COLORS[NOCOLOR]}"
    return 0
}

# Display an error message.
function error() {
    echo -e "${COLORS[RED]}$*${COLORS[NOCOLOR]}"
    return 0
}

# @description Run unit tests.
# @exitcode 0 If successful.
function unit_tests() {
    info "Run unit tests"
    pytest tests/test_*.py
    return 0
}

# @description Linter the application's bash code.
# @exitcode 0 If successful.
function lint() {
    info "Linting bash code"
    shellcheck -e SC1083 -x toolbox.sh
    return 0
}

# @description Generate API documentation in markdown format.
# @exitcode 0 If successful.
function generate_doc() {
    info "Generating the toolbox API documentation"
    shdoc < "$CURRENT_PATH/toolbox.sh" > "$DOC_PATH/toolbox.md"
    shdoc < "$CURRENT_PATH/utils_bash.sh" > "$DOC_PATH/utils_bash.md"
    return 0
}

# @description Start the mkdocs server to browse the API documentation in a browser.
# @exitcode 0 If successful.
function mkdocs_server_start() {
    info "Start the mkdocs server"
    mkdocs serve -a 0.0.0.0:8000
    return 0
}

# @description Display help.
# @exitcode 0 If successful.
function display_help() {
    local output=""
    output="
${COLORS[YELLOW]}Usage${COLORS[WHITE]} $(basename "$0") [OPTIONS] COMMAND
${COLORS[YELLOW]}Commands:${COLORS[NOCOLOR]}
${COLORS[GREEN]}test${COLORS[WHITE]}                Run unit tests
${COLORS[GREEN]}lint${COLORS[WHITE]}                Linter the application's bash code
${COLORS[GREEN]}doc${COLORS[WHITE]}                 Generate API documentation
${COLORS[GREEN]}mkdocs${COLORS[WHITE]}              Start the mkdocs server
${COLORS[YELLOW]}Options:${COLORS[NOCOLOR]}
${COLORS[GREEN]}-h, --help${COLORS[WHITE]}          Display help
${COLORS[GREEN]}-v, --verbose${COLORS[WHITE]}       Make the command more talkative
    "
    echo -e "$output\n"|sed '1d; $d'
    return 0
}

# Check options passed as script parameters.
function check_opts() {
    read -ra opts <<< "$@"
    for opt in "${opts[@]}"; do
        case "$opt" in
            test) UNIT_TEST=1 ;;
            lint) LINT=1 ;;
            doc) GENERATE_DOC=1 ;;
            mkdocs) MKDOCS=1 ;;
            --verbose) VERBOSE=1 ;;
            --help) HELP=1 ;;
            *) ARGS+=("$opt") ;;
        esac
    done
    # Help is displayed if no option is passed as script parameter
    if [[ $((HELP+UNIT_TEST+LINT+GENERATE_DOC+MKDOCS)) -eq 0 ]]; then
        HELP=1
    fi
    return 0
}

# Execute tasks based on script parameters or user actions.
function execute_tasks() {
    # Display help
    if [[ $HELP -gt 0 ]]; then
        ! display_help && return 1
        return 0
    fi
    # Run unit tests
    if [[ $UNIT_TEST -gt 0 ]]; then
        ! unit_tests && return 2
    fi
    # Linter the application's bash code
    if [[ $LINT -gt 0 ]]; then
        ! lint && return 3
    fi
    # Generate API documentation in markdown format
    if [[ $GENERATE_DOC -gt 0 ]]; then
        ! generate_doc && return 4
    fi
    # Start the mkdocs server to browse the API documentation in a browser
    if [[ $MKDOCS -gt 0 ]]; then
        ! mkdocs_server_start && return 5
    fi
    return 0
}

# Main function.
function main() {
    ARGS=()
    HELP=0 VERBOSE=0
    UNIT_TEST=0 LINT=0 GENERATE_DOC=0 MKDOCS=0
    # Check options passed as script parameters and execute tasks
    ! check_opts "$@" && return 1
    # Execute one or more tasks according to script parameters
    ! execute_tasks && return 2
    return 0
}

main "$@"
