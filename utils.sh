#!/usr/bin/env bash
# @name Utils
# @brief **utils.sh**.

DOCKER_IMAGE="toolbox:1.0.0"

# @description Check if docker is installed.
# @noargs
# @exitcode 0 If docker is installed.
# @exitcode 1 If docker is not installed.
function check_env_docker() {
    ! [[ -x "$(command -v docker)" ]] && echo "Please install docker" && return 1
    return 0
}

# @description Removing the utils docker image.
# @noargs
# @exitcode 0 If successful.
function docker_image_remove() {
    docker image rm "$DOCKER_IMAGE" 2> /dev/null
    return 0
}

# @description Building the utils docker image.
# @noargs
# @exitcode 0 If successful.
function docker_build() {
    DOCKER_BUILDKIT=1 docker build -f toolbox.Dockerfile --target=runtime -t="$DOCKER_IMAGE" .
    return 0
}

# @description Execute a command in the docker container.
# @noargs
# @exitcode 0 If successful.
function docker_run() {
    docker run --rm -it \
        -e ENV=docker \
        -p 8000:8000 \
        -v "$(pwd)/toolbox.sh:/app/toolbox.sh" \
        -v "$(pwd)/utils_bash.sh:/app/utils_bash.sh" \
        -v "$(pwd)/mkdocs.yml:/app/mkdocs.yml" \
        -v "$(pwd)/docs:/app/docs" \
        -v "$(pwd)/tests:/app/tests" \
        $DOCKER_IMAGE /bin/bash
    return 0
}

# @description Main function.
# @noargs
# @exitcode 0 If successful.
# @exitcode 1 If Docker is not installed.
# @exitcode 2 If an error is encountered when removing the Docker image.
# @exitcode 3 If an error is encountered when building the Docker image.
# @exitcode 4 If an error occurs in the Docker container.
function main() {
    FORCE=0
    [[ "$1" = "--force" ]] && FORCE=1
    ! check_env_docker && return 1
    if [[ $FORCE -gt 0 ]]; then
        ! docker_image_remove && return 2
    fi
    ! docker_build && return 3
    ! docker_run && return 4
    return 0
}

main "$@"
