import inspect
import os
import pytest
import unittest


current_dir: str = os.path.dirname(os.path.abspath(inspect.getfile(inspect.currentframe())))
if os.environ['HOME'] == "/app":
    current_dir = "/app/tests"

script: str = os.path.abspath(f"{current_dir}/../toolbox.sh")

def test_function_not_exist(shell):
    ret = shell.run(script, "test_function")
    assert ret.returncode == 1
    assert "Function with name test_function does not exist" in ret.stderr
