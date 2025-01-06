#!/usr/bin/env bash

#
# buildDependencies.sh
#
# This script downloads, verifies, and builds static libraries
# for zlib, bzip2, and zstd into a specified output directory.
#
# Usage:
#   chmod +x buildDependencies.sh
#   ./buildDependencies.sh [--extra-cflags "FLAGS"] [--output-dir "/path/to/dir"]
#
# Options:
#   --extra-cflags   Additional CFLAGS to pass to the compiler.
#   --output-dir     Directory where libz.a, libbz2.a, and libzstd.a will be placed.
#                    Defaults to the current directory.
#   -h, --help       Display this help message.

set -euo pipefail

# ---------------------------------------------------------
# Default Values
# ---------------------------------------------------------
DOWNLOAD_DIR="lib"

DEFAULT_ZLIB_VER="1.3.1"
DEFAULT_ZLIB_SHA256="9a93b2b7dfdac77ceba5a558a580e74667dd6fede4585b91eefb60f03b72df23"
DEFAULT_ZLIB_DOWNLOAD_BASE="http://zlib.net"

DEFAULT_BZIP2_VER="1.0.8"
DEFAULT_BZIP2_SHA256="ab5a03176ee106d3f0fa90e381da478ddae405918153cca248e682cd0c4a2269"
DEFAULT_BZIP2_DOWNLOAD_BASE="http://sourceware.org/pub/bzip2"

DEFAULT_ZSTD_VER="1.5.6"
DEFAULT_ZSTD_SHA256="8c29e06cf42aacc1eafc4077ae2ec6c6fcb96a626157e0593d5e82a34fd403c1"
DEFAULT_ZSTD_DOWNLOAD_BASE="https://github.com/facebook/zstd/releases/download/v${DEFAULT_ZSTD_VER}"

DEFAULT_OUTPUT_DIR="$(pwd)"
EXTRA_CFLAGS=""

# ---------------------------------------------------------
# Function to Display Help
# ---------------------------------------------------------
usage() {
  cat <<EOF
Usage: $0 [OPTIONS]

Options:
  --extra-cflags "FLAGS"    Additional CFLAGS to pass to the compiler.
  --output-dir "/path"      Directory where libz.a, libbz2.a, and libzstd.a will be placed.
                            Defaults to the current directory.
  -h, --help                Display this help message.

Example:
  ./buildDependencies.sh --extra-cflags "-O3 -march=native" --output-dir "./libs"
EOF
  exit 1
}

# ---------------------------------------------------------
# Parse Command-Line Arguments
# ---------------------------------------------------------
while [[ $# -gt 0 ]]; do
  case "$1" in
    --extra-cflags)
      EXTRA_CFLAGS="$2"
      shift 2
      ;;
    --output-dir)
      OUTPUT_DIR="$2"
      shift 2
      ;;
    -h|--help)
      usage
      ;;
    *)
      echo "Unknown option: $1"
      usage
      ;;
  esac
done

# Set OUTPUT_DIR to DEFAULT_OUTPUT_DIR if not provided
OUTPUT_DIR="${OUTPUT_DIR:-$DEFAULT_OUTPUT_DIR}"

# Create the output directory if it doesn't exist
mkdir -p "$OUTPUT_DIR"

# ---------------------------------------------------------
# Versions and checksums
# ---------------------------------------------------------
ZLIB_VER="${ZLIB_VER:-$DEFAULT_ZLIB_VER}"
ZLIB_SHA256="${ZLIB_SHA256:-$DEFAULT_ZLIB_SHA256}"
ZLIB_DOWNLOAD_BASE="${ZLIB_DOWNLOAD_BASE:-$DEFAULT_ZLIB_DOWNLOAD_BASE}"

BZIP2_VER="${BZIP2_VER:-$DEFAULT_BZIP2_VER}"
BZIP2_SHA256="${BZIP2_SHA256:-$DEFAULT_BZIP2_SHA256}"
BZIP2_DOWNLOAD_BASE="${BZIP2_DOWNLOAD_BASE:-$DEFAULT_BZIP2_DOWNLOAD_BASE}"

ZSTD_VER="${ZSTD_VER:-$DEFAULT_ZSTD_VER}"
ZSTD_SHA256="${ZSTD_SHA256:-$DEFAULT_ZSTD_SHA256}"
ZSTD_DOWNLOAD_BASE="${ZSTD_DOWNLOAD_BASE:-$DEFAULT_ZSTD_DOWNLOAD_BASE}"

# Ensure DOWNLOAD_DIR exists
mkdir -p "$DOWNLOAD_DIR"

# ---------------------------------------------------------
# Function to Download and Verify Tarball
# ---------------------------------------------------------
download_and_verify() {
  local name="$1"
  local version="$2"
  local url_base="$3"
  local sha256="$4"
  local tarball="${name}-${version}.tar.gz"
  local target_path="${DOWNLOAD_DIR}/${tarball}"

  echo "Downloading ${name}-${version}..."
  if curl --silent --fail --location -o "${target_path}" "${url_base}/${tarball}"; then
    echo "Verifying ${name}-${version}..."
    local sha256_actual
    sha256_actual="$(shasum -a 256 "${target_path}" | awk '{print $1}')"
    if [[ "${sha256}" != "${sha256_actual}" ]]; then
      echo "Error: ${tarball} checksum mismatch!" >&2
      echo "  expected: ${sha256}" >&2
      echo "  actual:   ${sha256_actual}" >&2
      exit 1
    fi
  else
    echo "Error downloading ${name}-${version}!" >&2
    exit 1
  fi
}

# ---------------------------------------------------------
# Function to Build zlib
# ---------------------------------------------------------
build_zlib() {
  local tarball="${DOWNLOAD_DIR}/zlib-${ZLIB_VER}.tar.gz"
  local src_dir="${DOWNLOAD_DIR}/zlib-${ZLIB_VER}"
  tar xzf "${tarball}" -C "${DOWNLOAD_DIR}"  > /dev/null
  pushd "${src_dir}" > /dev/null
  CFLAGS="${EXTRA_CFLAGS} -fPIC" ./configure --static
  make clean > /dev/null
  make > /dev/null
  cd ../..
  cp "${src_dir}/libz.a" "${OUTPUT_DIR}/"
  popd > /dev/null
}

# ---------------------------------------------------------
# Function to Build bzip2
# ---------------------------------------------------------
build_bzip2() {
  local tarball="${DOWNLOAD_DIR}/bzip2-${BZIP2_VER}.tar.gz"
  local src_dir="${DOWNLOAD_DIR}/bzip2-${BZIP2_VER}"
  tar xzf "${tarball}" -C "${DOWNLOAD_DIR}"  > /dev/null
  pushd "${src_dir}" > /dev/null
  make clean > /dev/null
  make CFLAGS="${EXTRA_CFLAGS} -fPIC -O2 -g -D_FILE_OFFSET_BITS=64" libbz2.a > /dev/null
  cd ../..
  cp "${src_dir}/libbz2.a" "${OUTPUT_DIR}/"
  popd > /dev/null
}

# ---------------------------------------------------------
# Function to Build zstd
# ---------------------------------------------------------
build_zstd() {
  local tarball="${DOWNLOAD_DIR}/zstd-${ZSTD_VER}.tar.gz"
  local src_dir="${DOWNLOAD_DIR}/zstd-${ZSTD_VER}"
  tar xzf "${tarball}" -C "${DOWNLOAD_DIR}"  > /dev/null
  pushd "${src_dir}/lib" > /dev/null
  CFLAGS="${EXTRA_CFLAGS} -fPIC -O2" make clean > /dev/null
  CFLAGS="${EXTRA_CFLAGS} -fPIC -O2" make libzstd.a > /dev/null
  cd ../../..
  cp "${src_dir}/lib/libzstd.a" "${OUTPUT_DIR}/"
  popd > /dev/null
}

# ---------------------------------------------------------
# Main Execution Flow
# ---------------------------------------------------------
if [ -f "${OUTPUT_DIR}/libbz2.a" ]; then
  echo "libbz2.a already exists in ${OUTPUT_DIR}, skipping bzip2 build."
else
  # Download, verify, and build bzip2
  download_and_verify "bzip2" "$BZIP2_VER" "$BZIP2_DOWNLOAD_BASE" "$BZIP2_SHA256"
  build_bzip2
  echo "✅ Finished building libbz2.a into ${OUTPUT_DIR}!"
fi

if [ -f "${OUTPUT_DIR}/libz.a" ]; then
  echo "libz.a already exists in ${OUTPUT_DIR}, skipping zlib build."
else
  # Download, verify, and build zlib
  download_and_verify "zlib" "$ZLIB_VER" "$ZLIB_DOWNLOAD_BASE" "$ZLIB_SHA256"
  build_zlib
  echo "✅ Finished building libz.a into ${OUTPUT_DIR}!"
fi

if [ -f "${OUTPUT_DIR}/libzstd.a" ]; then
  echo "libzstd.a already exists in ${OUTPUT_DIR}, skipping zstd build."
else
  # Download, verify, and build zstd
  download_and_verify "zstd" "$ZSTD_VER" "$ZSTD_DOWNLOAD_BASE" "$ZSTD_SHA256"
  build_zstd
  echo "✅ Finished building libzstd.a into ${OUTPUT_DIR}!"
fi
