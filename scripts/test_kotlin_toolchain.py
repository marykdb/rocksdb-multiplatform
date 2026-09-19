import importlib.util
import json
from pathlib import Path
import subprocess
import tempfile
import unittest
from unittest.mock import patch

spec = importlib.util.spec_from_file_location("launcher", Path(__file__).with_name("kotlin_toolchain.py"))
launcher = importlib.util.module_from_spec(spec)
spec.loader.exec_module(launcher)


class LauncherTest(unittest.TestCase):
    def test_publish_passes_local_credentials_only_to_child_environment(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            key = "-----BEGIN PGP PRIVATE KEY BLOCK-----\nfixture-key"
            (root / "key.asc").write_text(key)
            (root / "local.properties").write_text(
                "mavenCentralUsername=fixture-user\nmavenCentralPassword=fixture-password\n"
                "signing.keyFile=key.asc\n"
            )
            with patch.object(launcher, "ROOT", root), patch.dict(launcher.os.environ, {}, clear=True), \
                 patch.object(launcher.subprocess, "run") as run:
                launcher.main(["publish", "mavenCentral"])
                environment = run.call_args.kwargs["env"]
                self.assertEqual(environment["KOTLIN_TOOLCHAIN_SIGNING_KEY"], key)
                self.assertEqual(environment["KOTLIN_TOOLCHAIN_MAVEN_CENTRAL_PASSWORD"], "fixture-password")
                self.assertNotIn("KOTLIN_TOOLCHAIN_SIGNING_KEY", launcher.os.environ)
                yaml = (root / ".toolchain/local.module-template.yaml").read_text()
                self.assertNotIn("fixture-", yaml)

    def test_paths_and_signing_are_specific_to_this_checkout_and_command(self):
        with tempfile.TemporaryDirectory(prefix="checkout with spaces ") as directory:
            root = Path(directory).resolve()
            with patch.object(launcher, "ROOT", root):
                launcher.prepare(["publish", "mavenLocal"])
                output = root / ".toolchain/local.module-template.yaml"
                local = output.read_text()
                self.assertEqual(local.count("-friend-modules="), 16)
                flag = json.loads(local.split("freeCompilerArgs: ", 1)[1].splitlines()[0])[0]
                self.assertTrue(flag.startswith(f"-friend-modules={root}/build/tasks/"))
                self.assertIn("signArtifacts: false", local)
                launcher.prepare(["publish", "mavenCentral"])
                self.assertIn("signArtifacts: true", output.read_text())
                self.assertIn("enabled: true", output.read_text())

    def test_failed_test_removes_its_database_directory(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            failure = subprocess.CalledProcessError(7, "test")
            with patch.object(launcher, "ROOT", root), patch.object(launcher, "kotlin", side_effect=failure):
                with self.assertRaises(subprocess.CalledProcessError) as caught:
                    launcher.check(["jvm"])
            self.assertEqual(caught.exception.returncode, 7)
            self.assertFalse((root / "build/test-database").exists())

    def test_failed_simulator_test_shuts_down_only_a_device_it_started(self):
        for state in ("Shutdown", "Booted"):
            with self.subTest(state=state):
                devices = {"devices": {"com.apple.CoreSimulator.SimRuntime.tvOS-26-1": [
                    {"udid": "test-device", "state": state, "isAvailable": True}
                ]}}
                def run(*args, **kwargs):
                    if "list" in args:
                        return json.dumps(devices)
                    if "spawn" in args:
                        raise subprocess.CalledProcessError(9, args)
                with patch.object(launcher, "link_tests", return_value=Path("test.kexe")), \
                     patch.object(launcher, "run", side_effect=run), \
                     patch.object(launcher.subprocess, "run") as shutdown:
                    with self.assertRaises(subprocess.CalledProcessError) as caught:
                        launcher.simulator("tvosSimulatorArm64")
                    self.assertEqual(caught.exception.returncode, 9)
                    self.assertEqual(shutdown.call_count, 1 if state == "Shutdown" else 0)


if __name__ == "__main__":
    unittest.main()
