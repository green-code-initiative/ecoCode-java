#!/usr/bin/env bash

DOCKER_IMAGE="toolbox:1.0.0"

function check_env_docker() {
    ! [[ -x "$(command -v docker)" ]] && echo "Please install docker" && return 1
    return 0
}

function docker_image_remove() {
    docker image rm "$DOCKER_IMAGE" 2> /dev/null
    return 0
}

function docker_build() {
    DOCKER_BUILDKIT=1 docker build -f toolbox.Dockerfile --target=runtime -t="$DOCKER_IMAGE" .
    return 0
}

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
