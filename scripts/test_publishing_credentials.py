from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest
from unittest.mock import patch

import publishing_credentials as credentials


class PublishingCredentialsTest(unittest.TestCase):
    def test_java_properties_escaping_and_continuations(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "local.properties"
            path.write_text(
                "# ignored\\\n! ignored\n"
                " user\\ name : user\\=name\n"
                "password=leading\\ space\\:value=tail  \n"
                "windows=C:\\\\keys\\\\private.asc\n"
                "unicode=\\u00e9\\uD83D\\uDE80\n"
                "continued=one\\\n   two\n"
                "slashes=two\\\\\n"
                "commentValue=\\\n#literal\n"
                "empty=\nuser\\ name=replaced\n",
                encoding="ascii",
            )
            self.assertEqual(credentials.read_properties(path), {
                "user name": "replaced", "password": "leading space:value=tail  ",
                "windows": "C:\\keys\\private.asc", "unicode": "é🚀", "continued": "onetwo",
                "slashes": "two\\", "commentValue": "#literal", "empty": "",
            })

    def test_properties_and_relative_armored_key(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            key = credentials.ARMOR_HEADER + "\nfixture\n-----END PGP PRIVATE KEY BLOCK-----"
            (root / "key.asc").write_text(key)
            (root / "local.properties").write_text(
                "mavenCentralUsername=local-user\nmavenCentralPassword=local-password\n"
                "signing.password=local-passphrase\nsigning.keyFile=key.asc\n"
            )
            original = {"UNCHANGED": "value", "KOTLIN_TOOLCHAIN_MAVEN_CENTRAL_USERNAME": "ci-user"}
            result = credentials.publishing_environment(root, ["publish", "mavenCentral"], original)
            self.assertEqual(result["KOTLIN_TOOLCHAIN_MAVEN_CENTRAL_USERNAME"], "ci-user")
            self.assertEqual(result["KOTLIN_TOOLCHAIN_MAVEN_CENTRAL_PASSWORD"], "local-password")
            self.assertEqual(result[credentials.PASSPHRASE], "local-passphrase")
            self.assertEqual(result[credentials.SIGNING_KEY], key)
            self.assertNotIn(credentials.SIGNING_KEY, original)

    def test_explicit_environment_skips_local_key_export(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            (root / "local.properties").write_text(
                "signing.secretKeyRingFile=missing.gpg\nsigning.password=local\n"
                "mavenCentralUsername=local\nmavenCentralPassword=local\n"
            )
            explicit = {credentials.SIGNING_KEY: "ci-key", credentials.PASSPHRASE: "",
                        "KOTLIN_TOOLCHAIN_MAVEN_CENTRAL_USERNAME": "ci-user",
                        "KOTLIN_TOOLCHAIN_MAVEN_CENTRAL_PASSWORD": "ci-password"}
            with patch.object(credentials, "export_keyring") as export:
                self.assertEqual(credentials.publishing_environment(root, ["publish", "mavenCentral"], explicit), explicit)
                export.assert_not_called()

    def test_unrelated_commands_do_not_read_credentials(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            (root / "local.properties").write_text("bad=\\uSECR")
            for arguments in (["test", "-p", "jvm"], ["publish", "mavenLocal"], ["publish", "--help"]):
                with self.subTest(arguments=arguments):
                    self.assertEqual(credentials.publishing_environment(root, arguments, {}), {})
            with self.assertRaisesRegex(RuntimeError, "Invalid Unicode escape"):
                credentials.publishing_environment(root, ["publish", "mavenCentral"], {})

    def test_gpg_failure_is_redacted_and_temporary_home_is_removed(self):
        failure = subprocess.CompletedProcess([], 2, "PRIVATE KEY", "SECRET PASSWORD")
        with patch.object(credentials.shutil, "which", side_effect=lambda name: name), \
             patch.object(credentials.subprocess, "run", return_value=failure) as run:
            with self.assertRaises(RuntimeError) as caught:
                credentials.export_keyring(Path("fixture.gpg"), "01234567", "passphrase")
            self.assertNotIn("SECRET", str(caught.exception))
            self.assertNotIn("PRIVATE KEY", str(caught.exception))
            import_args = run.call_args_list[0].args[0]
            self.assertNotIn("passphrase", import_args)
            home = Path(import_args[import_args.index("--homedir") + 1])
            self.assertFalse(home.exists())
            self.assertIn("--kill", run.call_args_list[-1].args[0])

    @unittest.skipUnless(shutil.which("gpg") and shutil.which("gpgconf"), "GnuPG not installed")
    def test_real_encrypted_keyring_export_and_signing(self):
        # Synthetic identity/key only; never use the developer's credentials in tests.
        with tempfile.TemporaryDirectory(prefix="ktc-test-", dir="/tmp" if credentials.os.name != "nt" else None) as directory:
            root = Path(directory)
            home = root / "gpg"
            home.mkdir(mode=0o700)
            command = [shutil.which("gpg"), "--no-options", "--homedir", str(home), "--batch", "--yes",
                       "--pinentry-mode", "loopback", "--passphrase-fd", "0"]
            def gpg(*arguments):
                result = subprocess.run([*command, *arguments], input=b"fixture-password\n", capture_output=True)
                self.assertEqual(result.returncode, 0, "Synthetic GPG operation failed")
                return result.stdout
            try:
                gpg("--quick-generate-key", "Toolchain Test <fixture@example.invalid>", "rsa2048", "sign", "1d")
                listing = gpg("--with-colons", "--list-secret-keys").decode()
                fingerprint = next(line.split(":")[9] for line in listing.splitlines() if line.startswith("fpr:"))
                ring = root / "secret.gpg"
                original = gpg("--export-secret-keys", fingerprint)
                ring.write_bytes(original)
                (root / "local.properties").write_text(
                    f"signing.secretKeyRingFile=secret.gpg\nsigning.keyId={fingerprint}\nsigning.password=fixture-password\n"
                )
                environment = credentials.publishing_environment(root, ["publish", "mavenCentral"], {})
                self.assertTrue(environment[credentials.SIGNING_KEY].startswith(credentials.ARMOR_HEADER))
                self.assertEqual(ring.read_bytes(), original)
                # Prove the exported key is parseable and retains its encrypted private material.
                exported = root / "exported.asc"
                exported.write_text(environment[credentials.SIGNING_KEY])
                verification_home = root / "verification"
                verification_home.mkdir(mode=0o700)
                command[command.index("--homedir") + 1] = str(verification_home)
                gpg("--import", str(exported))
                message = root / "message"
                message.write_text("Local signing verification")
                gpg("--local-user", fingerprint, "--detach-sign", str(message))
                gpg("--verify", str(message) + ".sig", str(message))
            finally:
                for candidate in (home, root / "verification"):
                    if candidate.exists():
                        subprocess.run([shutil.which("gpgconf"), "--homedir", str(candidate), "--kill", "gpg-agent"],
                                       stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, check=False)


if __name__ == "__main__":
    unittest.main()
