import inspect
import os

current_dir: str = "./"
if os.getenv('ENV') == "docker":
    current_dir = "/app/tests"
else:
    current_dir = os.path.dirname(os.path.abspath(inspect.getfile(inspect.currentframe())))
script: str = os.path.abspath(f"{current_dir}/../toolbox.sh")


def test_function_not_exist(bash):
    with bash() as s:
        s.auto_return_code_error = False
        assert s.run_script(script, ['test_function']) == "Function with name test_function does not exist"
        assert s.last_return_code == 1
