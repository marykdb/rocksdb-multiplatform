#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

usage() {
  cat <<'EOF'
Usage: download-rocksdb.sh --kmp-target <target> --version <release-tag> \
                           [--base-url <url>] [--sha256 <hash>] [--destination <dir>]

Downloads and unpacks the prebuilt RocksDB bundle for the given Kotlin Multiplatform
target. The artifacts are published under marykdb/build-rocksdb GitHub releases.
EOF
  exit 1
}

artifact_name_for_target() {
  case "$1" in
    androidNativeArm32) echo "rocksdb-android-arm32.zip" ;;
    androidNativeArm64) echo "rocksdb-android-arm64.zip" ;;
    androidNativeX64) echo "rocksdb-android-x64.zip" ;;
    androidNativeX86) echo "rocksdb-android-x86.zip" ;;
    iosArm64) echo "rocksdb-ios-arm64.zip" ;;
    iosSimulatorArm64) echo "rocksdb-ios-simulator-arm64.zip" ;;
    macosArm64) echo "rocksdb-macos-arm64.zip" ;;
    macosX64) echo "rocksdb-macos-x86_64.zip" ;;
    linuxArm64) echo "rocksdb-linux-arm64.zip" ;;
    linuxX64) echo "rocksdb-linux-x86_64.zip" ;;
    mingwArm64) echo "rocksdb-mingw-arm64.zip" ;;
    mingwX64) echo "rocksdb-mingw-x86_64.zip" ;;
    tvosArm64) echo "rocksdb-tvos-arm64.zip" ;;
    tvosSimulatorArm64) echo "rocksdb-tvos-simulator-arm64.zip" ;;
    watchosArm64) echo "rocksdb-watchos-arm64.zip" ;;
    watchosDeviceArm64) echo "rocksdb-watchos-device-arm64.zip" ;;
    watchosSimulatorArm64) echo "rocksdb-watchos-simulator-arm64.zip" ;;
    *) return 1 ;;
  esac
}

KMP_TARGET=""
VERSION=""
BASE_URL="https://github.com/marykdb/build-rocksdb/releases/download"
SHA256=""
DESTINATION=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    --kmp-target)
      KMP_TARGET="$2"
      shift 2
      ;;
    --version)
      VERSION="$2"
      shift 2
      ;;
    --base-url)
      BASE_URL="$2"
      shift 2
      ;;
    --sha256)
      SHA256="$2"
      shift 2
      ;;
    --destination)
      DESTINATION="$2"
      shift 2
      ;;
    -h|--help)
      usage
      ;;
    *)
      echo "Unknown argument: $1" >&2
      usage
      ;;
  esac
done

if [[ -z "$KMP_TARGET" || -z "$VERSION" ]]; then
  echo "--kmp-target and --version are required." >&2
  usage
fi

ARTIFACT_NAME="$(artifact_name_for_target "$KMP_TARGET" || true)"
if [[ -z "$ARTIFACT_NAME" ]]; then
  echo "No artifact mapping found for KMP target '$KMP_TARGET'." >&2
  exit 2
fi

if [[ -z "$DESTINATION" ]]; then
  DESTINATION="${SCRIPT_DIR}/build/rocksdb/${KMP_TARGET}"
fi

DOWNLOAD_DIR="${SCRIPT_DIR}/build/rocksdb-downloads"
mkdir -p "$DOWNLOAD_DIR"

ZIP_PATH="${DOWNLOAD_DIR}/${ARTIFACT_NAME}"
METADATA_PATH="${DESTINATION}/.rocksdb-prebuilt"
EXPECTED_MARKER="version=${VERSION}"$([[ -n "$SHA256" ]] && printf ' sha256=%s' "$SHA256")

if [[ -f "$METADATA_PATH" && "$(<"$METADATA_PATH")" == "$EXPECTED_MARKER" ]]; then
  if [[ -d "${DESTINATION}/lib" && -d "${DESTINATION}/include" ]]; then
    exit 0
  fi
fi

mkdir -p "$DESTINATION"

URL="${BASE_URL}/${VERSION}/${ARTIFACT_NAME}"

download_zip() {
  local temp_zip
  temp_zip="${ZIP_PATH}.tmp"
  rm -f "$temp_zip"
  curl --fail --location --retry 3 --retry-delay 2 --output "$temp_zip" "$URL"
  mv "$temp_zip" "$ZIP_PATH"
}

verify_sha() {
  local file_sha
  if [[ -z "$SHA256" ]]; then
    return 0
  fi
  if command -v shasum >/dev/null 2>&1; then
    file_sha="$(shasum -a 256 "$ZIP_PATH" | awk '{print $1}')"
  else
    file_sha="$(sha256sum "$ZIP_PATH" | awk '{print $1}')"
  fi
  if [[ "$file_sha" != "$SHA256" ]]; then
    echo "Checksum mismatch for $ARTIFACT_NAME" >&2
    rm -f "$ZIP_PATH"
    return 1
  fi
}

if [[ -f "$ZIP_PATH" ]]; then
  if ! verify_sha; then
    download_zip
    verify_sha
  fi
else
  download_zip
  verify_sha
fi

tmp_dir="$(mktemp -d)"
trap 'rm -rf "$tmp_dir"' EXIT

rm -rf "$DESTINATION"
mkdir -p "$DESTINATION"
unzip -oq "$ZIP_PATH" -d "$tmp_dir"

shopt -s dotglob
mv "$tmp_dir"/* "$DESTINATION"/
shopt -u dotglob

echo "$EXPECTED_MARKER" > "$METADATA_PATH"

exit 0
