import inspect
import os
import pytest


current_dir: str = os.path.dirname(os.path.abspath(inspect.getfile(inspect.currentframe())))
if os.environ['HOME'] == "/app":
    current_dir = "/app/tests"

project_path: str = os.path.abspath(f"{current_dir}/..")
script: str = os.path.abspath(f"{current_dir}/../toolbox.sh")


def test_function_empty(shell):
    ret = shell.run(script, "--test")
    assert ret.stderr.rstrip() == "No function to execute"
    assert ret.returncode == 1


def test_function_not_exist(shell):
    ret = shell.run(script, "test_function", "--test")
    assert ret.stderr.rstrip() == "Function with name test_function does not exist"
    assert ret.returncode == 2


@pytest.mark.parametrize("color_key, color_value", [
    ("R","\x1b[0;31m"),
    ("G", "\x1b[0;32m"),
    ("B", "\x1b[0;34m"),
    ("Y", "\x1b[0;33m"),
    ("W", "\x1b[0;37m"),
    ("N", "\x1b[0;0m")
])
def test_colors(shell, color_key, color_value):
    ret = shell.run(script, "colors", color_key, "--test")
    assert ret.stdout.rstrip() == color_value
    assert ret.returncode == 0


def test_info(shell):
    ret = shell.run(script, "info", "msg_info", "--test")
    assert ret.stdout.rstrip() == "msg_info"
    assert ret.returncode == 0


def test_debug(shell):
    ret = shell.run(script, "debug", "msg_debug", "--test")
    assert ret.stdout.rstrip() == "msg_debug"
    assert ret.returncode == 0


def test_error(shell):
    ret = shell.run(script, "error", "msg_error", "--test")
    assert ret.stderr.rstrip() == "msg_error"
    assert ret.returncode == 0


def test_build(shell):
    ret = shell.run(script, "build", "--test")
    assert len(ret.stdout.splitlines()) == 2
    assert ret.stdout.splitlines()[0] == "Building source code in the target folder"
    assert ret.stdout.splitlines()[1] == "mvn clean package -Dmaven.clean.failOnError=false -DskipTests"
    assert ret.returncode == 0


def test_compile(shell):
    ret = shell.run(script, "compile", "--test")
    assert len(ret.stdout.splitlines()) == 2
    assert ret.stdout.splitlines()[0] == "Compile source code"
    assert ret.stdout.splitlines()[1] == "mvn clean compile"
    assert ret.returncode == 0


def test_docker_env_source_not_exist(shell):
    ret = shell.run(script, "docker_env_source", "--test", "--fixture=1")
    assert len(ret.stdout.splitlines()) == 1
    assert ret.stdout.splitlines()[0] == "source test_docker_env"
    assert ret.stderr.rstrip() == "Cannot find test_docker_env"
    assert ret.returncode == 1


def test_docker_env_source(shell):
    ret = shell.run(script, "docker_env_source", "--test")
    assert len(ret.stdout.splitlines()) == 1
    assert ret.stdout.splitlines()[0] == f"source {project_path}/.default.docker.env"
    assert ret.returncode == 0


def test_docker_build_env_source_not_exist(shell):
    ret = shell.run(script, "docker_build", "--test", "--fixture=1")
    assert len(ret.stdout.splitlines()) == 1
    assert ret.stdout.splitlines()[0] == "source test_docker_env"
    assert ret.stderr.rstrip() == "Cannot find test_docker_env"
    assert ret.returncode == 1


def test_docker_build(shell):
    ret = shell.run(script, "docker_build", "--test")
    assert len(ret.stdout.splitlines()) == 3
    assert ret.stdout.splitlines()[0] == f"source {project_path}/.default.docker.env"
    assert ret.stdout.splitlines()[1] == "Build Docker services"
    assert ret.stdout.splitlines()[2] == f"docker compose -f {project_path}/docker-compose.yml build"
    assert ret.returncode == 0


def test_init_env_source_not_exist(shell):
    ret = shell.run(script, "init", "--test", "--fixture=1")
    assert len(ret.stdout.splitlines()) == 3
    assert ret.stdout.splitlines()[0] == "Building source code in the target folder"
    assert ret.stdout.splitlines()[1] == "mvn clean package -Dmaven.clean.failOnError=false -DskipTests"
    assert ret.stdout.splitlines()[2] == "source test_docker_env"
    assert ret.stderr.rstrip() == "Cannot find test_docker_env"
    assert ret.returncode == 2


def test_init(shell):
    ret = shell.run(script, "init", "--test")
    assert len(ret.stdout.splitlines()) == 5
    assert ret.stdout.splitlines()[0] == "Building source code in the target folder"
    assert ret.stdout.splitlines()[1] == "mvn clean package -Dmaven.clean.failOnError=false -DskipTests"
    assert ret.stdout.splitlines()[2] == f"source {project_path}/.default.docker.env"
    assert ret.stdout.splitlines()[3] == "Creating and starting Docker containers"
    assert ret.stdout.splitlines()[4] == f"docker compose -f {project_path}/docker-compose.yml up --build -d"
    assert ret.returncode == 0


def test_start_env_source_not_exist(shell):
    ret = shell.run(script, "start", "--test", "--fixture=1")
    assert len(ret.stdout.splitlines()) == 1
    assert ret.stdout.splitlines()[0] == "source test_docker_env"
    assert ret.stderr.rstrip() == "Cannot find test_docker_env"
    assert ret.returncode == 1


def test_start(shell):
    ret = shell.run(script, "start", "--test")
    assert len(ret.stdout.splitlines()) == 3
    assert ret.stdout.splitlines()[0] == f"source {project_path}/.default.docker.env"
    assert ret.stdout.splitlines()[1] == "Starting Docker containers"
    assert ret.stdout.splitlines()[2] == f"docker compose -f {project_path}/docker-compose.yml start"
    assert ret.returncode == 0


def test_stop_env_source_not_exist(shell):
    ret = shell.run(script, "stop", "--test", "--fixture=1")
    assert len(ret.stdout.splitlines()) == 1
    assert ret.stdout.splitlines()[0] == "source test_docker_env"
    assert ret.stderr.rstrip() == "Cannot find test_docker_env"
    assert ret.returncode == 1


def test_stop(shell):
    ret = shell.run(script, "stop", "--test")
    assert len(ret.stdout.splitlines()) == 3
    assert ret.stdout.splitlines()[0] == f"source {project_path}/.default.docker.env"
    assert ret.stdout.splitlines()[1] == "Stopping Docker containers"
    assert ret.stdout.splitlines()[2] == f"docker compose -f {project_path}/docker-compose.yml stop"
    assert ret.returncode == 0


def test_clean_env_source_not_exist(shell):
    ret = shell.run(script, "clean", "--test", "--fixture=1")
    assert len(ret.stdout.splitlines()) == 1
    assert ret.stdout.splitlines()[0] == "source test_docker_env"
    assert ret.stderr.rstrip() == "Cannot find test_docker_env"
    assert ret.returncode == 1


def test_clean(shell):
    ret = shell.run(script, "clean", "--test")
    assert len(ret.stdout.splitlines()) == 3
    assert ret.stdout.splitlines()[0] == f"source {project_path}/.default.docker.env"
    assert ret.stdout.splitlines()[1] == "Remove Docker containers, networks and volumes"
    assert ret.stdout.splitlines()[2] == f"docker compose -f {project_path}/docker-compose.yml down --volumes"
    assert ret.returncode == 0


def test_display_logs_env_source_not_exist(shell):
    ret = shell.run(script, "display_logs", "--test", "--fixture=1")
    assert len(ret.stdout.splitlines()) == 1
    assert ret.stdout.splitlines()[0] == "source test_docker_env"
    assert ret.stderr.rstrip() == "Cannot find test_docker_env"
    assert ret.returncode == 1


def test_display_logs(shell):
    ret = shell.run(script, "display_logs", "--test")
    assert len(ret.stdout.splitlines()) == 3
    assert ret.stdout.splitlines()[0] == f"source {project_path}/.default.docker.env"
    assert ret.stdout.splitlines()[1] == "Display Docker container logs"
    assert ret.stdout.splitlines()[2] == f"docker compose -f {project_path}/docker-compose.yml logs -f"
    assert ret.returncode == 0


def test_release(shell):
    ret = shell.run(script, "release", "--test")
    assert len(ret.stdout.splitlines()) == 4
    assert ret.stdout.splitlines()[0] == "Creation of 2 commits with release and next SNAPSHOT"
    assert ret.stdout.splitlines()[1] == "mvn release:prepare -B -ff -DpushChanges=false -DtagNameFormat=@{project.version}"
    assert ret.stdout.splitlines()[2] == "Clean temporary files"
    assert ret.stdout.splitlines()[3] == "mvn release:clean"
    assert ret.returncode == 0


def test_release_push(shell):
    ret = shell.run(script, "release_push", "--test")
    assert len(ret.stdout.splitlines()) == 1
    assert ret.stdout.splitlines()[0] == "Create a push and a new branch with commits previously prepared"
    assert ret.returncode == 0


def test_display_help(shell):
    ret = shell.run(script, "display_help", "--test")
    assert len(ret.stdout.splitlines()) == 18
    assert ret.returncode == 0
