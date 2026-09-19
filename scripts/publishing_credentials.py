"""Read ignored local publishing credentials without putting secrets in build files."""
import os
from pathlib import Path
import re
import shutil
import subprocess
import tempfile

SIGNING_KEY = "KOTLIN_TOOLCHAIN_SIGNING_KEY"
PASSPHRASE = "KOTLIN_TOOLCHAIN_SIGNING_KEY_PASSPHRASE"
ARMOR_HEADER = "-----BEGIN PGP PRIVATE KEY BLOCK-----"


def read_properties(path):
    """Java Properties.load(InputStream) syntax, including escapes and continuations."""
    def unescape(value):
        result = []
        index = 0
        while index < len(value):
            char = value[index]
            index += 1
            if char == "\\" and index < len(value):
                char = value[index]
                index += 1
                if char == "u":
                    digits = value[index:index + 4]
                    if not re.fullmatch(r"[0-9a-fA-F]{4}", digits):
                        raise RuntimeError("Invalid Unicode escape in local.properties")
                    char = chr(int(digits, 16))
                    index += 4
                else:
                    char = {"t": "\t", "r": "\r", "n": "\n", "f": "\f"}.get(char, char)
            result.append(char)
        try:
            return "".join(result).encode("utf-16-le", "surrogatepass").decode("utf-16-le")
        except UnicodeError:
            raise RuntimeError("Invalid Unicode in local.properties") from None

    properties = {}
    pending = None
    lines = re.split(r"\r\n|\r|\n", path.read_text(encoding="iso-8859-1"))
    for line in [*lines, ""]:
        line = line.lstrip(" \t\f")
        if pending is None and (not line or line.startswith(("#", "!"))):
            continue
        line = (pending or "") + line
        trailing_slashes = len(line) - len(line.rstrip("\\"))
        if trailing_slashes % 2:
            pending = line[:-1]
            continue
        pending = None
        index = 0
        while index < len(line) and line[index] not in "=:\t\f ":
            index += 2 if line[index] == "\\" else 1
        key = line[:index]
        rest = line[index:].lstrip(" \t\f")
        if rest.startswith(("=", ":")):
            rest = rest[1:]
        properties[unescape(key)] = unescape(rest.lstrip(" \t\f"))
    return properties


def export_keyring(path, key_id, passphrase):
    """Select/export a Gradle secret-key ring using an isolated, disposable GPG home."""
    if not re.fullmatch(r"(?:0x)?[0-9a-fA-F]{8,64}", key_id):
        raise RuntimeError("Set signing.keyId to the key ID or fingerprint for signing.secretKeyRingFile")
    gpg = shutil.which("gpg")
    gpgconf = shutil.which("gpgconf")
    if not gpg or not gpgconf:
        raise RuntimeError("Install GnuPG to use signing.secretKeyRingFile, or set signing.keyFile to an ASCII-armored private key")
    # A short home path also avoids Unix-domain socket path limits on macOS.
    temporary_root = "/tmp" if os.name != "nt" else None
    with tempfile.TemporaryDirectory(prefix="ktc-gpg-", dir=temporary_root) as home:
        command = [gpg, "--no-options", "--homedir", home, "--batch", "--yes",
                   "--pinentry-mode", "loopback", "--passphrase-fd", "0"]
        try:
            for arguments in (["--import", str(path)], ["--armor", "--export-secret-keys", key_id]):
                result = subprocess.run([*command, *arguments], input=passphrase + "\n",
                                        text=True, capture_output=True, check=False)
                if result.returncode:
                    # GPG diagnostics can include key identities; don't echo them or secrets.
                    raise RuntimeError("Cannot export signing.secretKeyRingFile; check the key file, signing.keyId, and signing.password")
            if not result.stdout.startswith(ARMOR_HEADER):
                raise RuntimeError("No private key matched signing.keyId in signing.secretKeyRingFile")
            return result.stdout
        finally:
            subprocess.run([gpgconf, "--homedir", home, "--kill", "gpg-agent"],
                           stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, check=False)


def publishing_environment(root, arguments, environment):
    """Populate only missing environment variables, and only for remote publishing."""
    environment = dict(environment)
    if "publish" not in arguments or "mavenLocal" in arguments or "--help" in arguments:
        return environment
    path = root / "local.properties"
    if not path.is_file():
        return environment
    properties = read_properties(path)
    mappings = {
        "mavenCentralUsername": "KOTLIN_TOOLCHAIN_MAVEN_CENTRAL_USERNAME",
        "mavenCentralPassword": "KOTLIN_TOOLCHAIN_MAVEN_CENTRAL_PASSWORD",
        "signing.password": PASSPHRASE,
    }
    for property_name, variable in mappings.items():
        if property_name in properties:
            environment.setdefault(variable, properties[property_name])
    if SIGNING_KEY not in environment:
        key_file = properties.get("signing.keyFile")
        keyring = properties.get("signing.secretKeyRingFile")
        if key_file or keyring:
            key_path = Path(key_file or keyring).expanduser()
            if not key_path.is_absolute():
                key_path = root / key_path
            try:
                if key_file:
                    key = key_path.read_text(encoding="ascii").strip()
                    if not key.startswith(ARMOR_HEADER):
                        raise RuntimeError("signing.keyFile must contain an ASCII-armored PGP private key")
                else:
                    key = export_keyring(key_path, properties.get("signing.keyId", ""),
                                         environment.get(PASSPHRASE, ""))
            except (OSError, UnicodeError):
                raise RuntimeError("Cannot read the private key configured in local.properties") from None
            environment[SIGNING_KEY] = key
    return environment
