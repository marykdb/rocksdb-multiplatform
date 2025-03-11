#!/usr/bin/env bash

#
# buildDependencies.sh
#
# This script downloads, verifies, and builds static libraries
# for zlib, bzip2, zstd, snappy, and lz4 into a specified output directory.
#
# Usage:
#   chmod +x buildDependencies.sh
#   ./buildDependencies.sh [--extra-cflags "FLAGS"] [--extra-cmakeflags "FLAGS"] [--output-dir "/path/to/dir"]
#
# Options:
#   --extra-cflags       Additional CFLAGS to pass to the compiler.
#   --extra-cmakeflags   Additional CFLAGS to pass to the compiler.
#   --output-dir         Directory where libz.a, libbz2.a, libzstd.a, libsnappy.a, and liblz4.a will be placed.
#                        Defaults to the current directory.
#   -h, --help           Display this help message.

set -euo pipefail

# ---------------------------------------------------------
# Default Values
# ---------------------------------------------------------
DOWNLOAD_DIR="lib"
TOOLCHAIN_FILE=null

# iOS Toolchain
IOS_TOOLCHAIN_URL="https://github.com/leetal/ios-cmake/archive/refs/tags/4.5.0.tar.gz"
IOS_TOOLCHAIN_ARCHIVE="${DOWNLOAD_DIR}/ios-cmake-4.5.0.tar.gz"
IOS_TOOLCHAIN_DIR="${DOWNLOAD_DIR}/ios-toolchain"

# zlib
DEFAULT_ZLIB_VER="1.3.1"
DEFAULT_ZLIB_SHA256="9a93b2b7dfdac77ceba5a558a580e74667dd6fede4585b91eefb60f03b72df23"
DEFAULT_ZLIB_DOWNLOAD_BASE="http://zlib.net"

# bzip2
DEFAULT_BZIP2_VER="1.0.8"
DEFAULT_BZIP2_SHA256="ab5a03176ee106d3f0fa90e381da478ddae405918153cca248e682cd0c4a2269"
DEFAULT_BZIP2_DOWNLOAD_BASE="http://sourceware.org/pub/bzip2"

# zstd
DEFAULT_ZSTD_VER="1.5.6"
DEFAULT_ZSTD_SHA256="8c29e06cf42aacc1eafc4077ae2ec6c6fcb96a626157e0593d5e82a34fd403c1"
DEFAULT_ZSTD_DOWNLOAD_BASE="https://github.com/facebook/zstd/releases/download/v${DEFAULT_ZSTD_VER}"

# snappy
DEFAULT_SNAPPY_VER="1.2.1"
DEFAULT_SNAPPY_SHA256="736aeb64d86566d2236ddffa2865ee5d7a82d26c9016b36218fcc27ea4f09f86"
DEFAULT_SNAPPY_DOWNLOAD_BASE="https://github.com/google/snappy/archive"

# lz4
DEFAULT_LZ4_VER="1.9.4"
DEFAULT_LZ4_SHA256="0b0e3aa07c8c063ddf40b082bdf7e37a1562bda40a0ff5272957f3e987e0e54b"
DEFAULT_LZ4_DOWNLOAD_BASE="https://github.com/lz4/lz4/archive"

# Build Flags (can be overridden by environment variables)
ARCHFLAG="${ARCHFLAG:-}"
EXTRA_CMAKEFLAGS="${EXTRA_CMAKEFLAGS:-}"
EXTRA_CFLAGS="${EXTRA_CFLAGS:-}"
EXTRA_CXXFLAGS="${EXTRA_CXXFLAGS:-}"
EXTRA_LDFLAGS="${EXTRA_LDFLAGS:-}"
PLATFORM_CMAKE_FLAGS="${PLATFORM_CMAKE_FLAGS:-}"
SNAPPY_MAKE_TARGET="${SNAPPY_MAKE_TARGET:-}"

DEFAULT_OUTPUT_DIR="$(pwd)"

# ---------------------------------------------------------
# Function to Display Help
# ---------------------------------------------------------
usage() {
  cat <<EOF
Usage: $0 [OPTIONS]

Options:
  --extra-cflags "FLAGS"        Additional CFLAGS to pass to the compiler.
  --extra-cmakeflags "FLAGS"    Additional flags to pass to CMAKE.
  --output-dir "/path"          Directory where libz.a, libbz2.a, libzstd.a, libsnappy.a, and liblz4.a will be placed.
                                Defaults to the current directory.
  -h, --help                    Display this help message.

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
      EXTRA_CXXFLAGS="$2"
      shift 2
      ;;
    --extra-cmakeflags)
      EXTRA_CMAKEFLAGS="$2"
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

set +u
if [[ "$OUTPUT_DIR" == *linux_x86_64* ]]; then
  export CC="$HOME/.konan/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/bin/x86_64-unknown-linux-gnu-gcc"
  export CXX="$HOME/.konan/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/bin/x86_64-unknown-linux-gnu-g++"
elif [[ "$OUTPUT_DIR" == *linux_arm64* ]]; then
  export CC="$HOME/.konan/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/bin/aarch64-unknown-linux-gnu-gcc"
  export CXX="$HOME/.konan/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/bin/aarch64-unknown-linux-gnu-g++"
elif [[ "$OUTPUT_DIR" == *macos_x86_64* ]]; then
  export CC="clang"
  export CXX="clang++"
elif [[ "$OUTPUT_DIR" == *macos_arm64* ]]; then
  export CC="clang"
  export CXX="clang++"
elif [[ "$OUTPUT_DIR" == *mingw_x86_64* ]]; then
  export CC="x86_64-w64-mingw32-gcc"
  export CXX="x86_64-w64-mingw32-g++"
  export AR="x86_64-w64-mingw32-ar"
  export uname="mingw32"
  export CROSS_PREFIX="x86_64-w64-mingw32-"
fi
set -u

# Create the output directory if it doesn't exist
mkdir -p "$OUTPUT_DIR"
mkdir -p "$DOWNLOAD_DIR/include"

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

SNAPPY_VER="${SNAPPY_VER:-$DEFAULT_SNAPPY_VER}"
SNAPPY_SHA256="${SNAPPY_SHA256:-$DEFAULT_SNAPPY_SHA256}"
SNAPPY_DOWNLOAD_BASE="${SNAPPY_DOWNLOAD_BASE:-$DEFAULT_SNAPPY_DOWNLOAD_BASE}"

LZ4_VER="${LZ4_VER:-$DEFAULT_LZ4_VER}"
LZ4_SHA256="${LZ4_SHA256:-$DEFAULT_LZ4_SHA256}"
LZ4_DOWNLOAD_BASE="${LZ4_DOWNLOAD_BASE:-$DEFAULT_LZ4_DOWNLOAD_BASE}"

# Ensure DOWNLOAD_DIR exists
mkdir -p "$DOWNLOAD_DIR"

download_ios_toolchain() {
  if [ -d "${IOS_TOOLCHAIN_DIR}" ]; then
    return
  fi

  mkdir -p "${DOWNLOAD_DIR}"
  if curl --silent --fail --location -o "${IOS_TOOLCHAIN_ARCHIVE}" "${IOS_TOOLCHAIN_URL}"; then
    echo "✅ Downloaded iOS toolchain successfully."
  else
    echo "❌ Error downloading iOS toolchain from ${IOS_TOOLCHAIN_URL}" >&2
    exit 1
  fi
  mkdir -p "${IOS_TOOLCHAIN_DIR}"
  tar -xzf "${IOS_TOOLCHAIN_ARCHIVE}" -C "${DOWNLOAD_DIR}"
  mv "${DOWNLOAD_DIR}/ios-cmake-4.5.0/ios.toolchain.cmake" "${IOS_TOOLCHAIN_DIR}/"
  rm -rf "${DOWNLOAD_DIR}/ios-cmake-4.5.0"
}

if [[ "${EXTRA_CFLAGS}" == *"-isysroot"* ]]; then
  download_ios_toolchain
  TOOLCHAIN_FILE="${IOS_TOOLCHAIN_DIR}/ios.toolchain.cmake"
fi

# ---------------------------------------------------------
# Function to Download and Verify Tarball
# ---------------------------------------------------------
download_and_verify() {
  local name="$1"
  local version="$2"
  local url_base="$3"
  local sha256="$4"
  local tarball
  if [ "$name" == "snappy" ]; then
    tarball="${version}.tar.gz"
  elif [ "$name" == "lz4" ]; then
    tarball="v${version}.tar.gz"
  else
    tarball="${name}-${version}.tar.gz"
  fi
  local target_path="${DOWNLOAD_DIR}/${name}-${version}.tar.gz"

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
  CROSS_PREFIX="${CROSS_PREFIX}" CFLAGS="${EXTRA_CFLAGS} -fPIC" ./configure --static
  make clean > /dev/null
  make static
  cp "zlib.h" "zconf.h" "../../${DOWNLOAD_DIR}/include/"
  cp "libz.a" "../../${OUTPUT_DIR}/"
  popd > /dev/null
  echo "✅ Finished building libz.a into ${OUTPUT_DIR}!"
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
  cp "bzlib.h" "../../${DOWNLOAD_DIR}/include/"
  cp "libbz2.a" "../../${OUTPUT_DIR}/"
  popd > /dev/null
  echo "✅ Finished building libbz2.a into ${OUTPUT_DIR}!"
}

# ---------------------------------------------------------
# Function to Build zstd
# ---------------------------------------------------------
build_zstd() {
  local tarball="${DOWNLOAD_DIR}/zstd-${ZSTD_VER}.tar.gz"
  local src_dir="${DOWNLOAD_DIR}/zstd-${ZSTD_VER}"
  tar xzf "${tarball}" -C "${DOWNLOAD_DIR}"  > /dev/null
  pushd "${src_dir}/lib" > /dev/null
  make clean > /dev/null
  CFLAGS="${EXTRA_CFLAGS} -fPIC -O2" make libzstd.a > /dev/null
  popd > /dev/null
  cp "${src_dir}/lib/zstd.h" "${src_dir}/lib/zdict.h" "${DOWNLOAD_DIR}/include/"
  cp "${src_dir}/lib/libzstd.a" "${OUTPUT_DIR}/"
  echo "✅ Finished building libzstd.a into ${OUTPUT_DIR}!"
}

# ---------------------------------------------------------
# Function to Build snappy
# ---------------------------------------------------------
build_snappy() {
  local tarball="${DOWNLOAD_DIR}/snappy-${SNAPPY_VER}.tar.gz"
  local src_dir="${DOWNLOAD_DIR}/snappy-${SNAPPY_VER}"

  rm -rf $src_dir

  tar xzf "${tarball}" -C "${DOWNLOAD_DIR}"  > /dev/null
  pushd "${src_dir}" > /dev/null

  if [ "${TOOLCHAIN_FILE}" != null ]; then
    EXTRA_CMAKEFLAGS+=""" -DCMAKE_TOOLCHAIN_FILE="../../${TOOLCHAIN_FILE}""""
  else
    local toolchain_file="toolchain.cmake"
    echo "set(CMAKE_C_FLAGS \"\${CMAKE_CXX_FLAGS} ${EXTRA_CFLAGS}\")" > "${toolchain_file}"
    echo "set(CMAKE_CXX_FLAGS \"\${CMAKE_CXX_FLAGS} ${EXTRA_CXXFLAGS}\")" >> "${toolchain_file}"
    echo "set(CMAKE_POSITION_INDEPENDENT_CODE ON)" >> "${toolchain_file}"
    echo "set(CMAKE_BUILD_TYPE Release)" >> "${toolchain_file}"
    echo "set(CMAKE_C_COMPILER_WORKS ON)" >> "${toolchain_file}"
    echo "set(CMAKE_CXX_COMPILER_WORKS ON)" >> "${toolchain_file}"
    echo "set(CMAKE_16BIT_TYPE \"unsigned long\")" >> "${toolchain_file}"

    EXTRA_CMAKEFLAGS+=""" -DCMAKE_C_COMPILER=$CC -DCMAKE_CXX_COMPILER=$CXX -DCMAKE_TOOLCHAIN_FILE=$toolchain_file"""
    if [[ "$OUTPUT_DIR" == *mingw_x86_64* ]]; then
      EXTRA_CMAKEFLAGS+=" -DCMAKE_SYSTEM_NAME=Windows"
      EXTRA_CMAKEFLAGS+=" -DSNAPPY_IS_BIG_ENDIAN=0"
    fi
  fi

  cmake -DCMAKE_POSITION_INDEPENDENT_CODE=ON \
        ${EXTRA_CMAKEFLAGS} \
        -DCMAKE_C_FLAGS="${EXTRA_CFLAGS}" \
        -DCMAKE_CXX_FLAGS="${EXTRA_CXXFLAGS}" \
        -DSNAPPY_BUILD_BENCHMARKS=OFF \
        -DSNAPPY_BUILD_TESTS=OFF \
        -Wno-dev ${PLATFORM_CMAKE_FLAGS} . \
        --compile-no-warning-as-error

  make clean > /dev/null
  CXXFLAGS="${EXTRA_CXXFLAGS} -fPIC -O2" CFLAGS="${EXTRA_CFLAGS} -fPIC -O2" make ${SNAPPY_MAKE_TARGET} > /dev/null
  cp "snappy.h" "snappy-stubs-public.h" "../../${DOWNLOAD_DIR}/include/"
  cp "libsnappy.a" "../../${OUTPUT_DIR}/"
  popd > /dev/null
  echo "✅ Finished building libsnappy.a into ${OUTPUT_DIR}!"
}

# ---------------------------------------------------------
# Function to Build LZ4
# ---------------------------------------------------------
build_lz4() {
  local tarball="${DOWNLOAD_DIR}/lz4-${LZ4_VER}.tar.gz"
  local src_dir="${DOWNLOAD_DIR}/lz4-${LZ4_VER}"

  echo "Building LZ4 version ${LZ4_VER}..."
  tar xzf "${tarball}" -C "${DOWNLOAD_DIR}" > /dev/null
  pushd "${src_dir}/lib" > /dev/null
  make clean > /dev/null

  TARGET_OS=null
  if [[ "$OUTPUT_DIR" == *mingw_x86_64* ]]; then
    # Because we use linux cross compiler, we need to set it to linux
    TARGET_OS="Linux"
  fi

  TARGET_OS=$TARGET_OS CFLAGS="${EXTRA_CFLAGS} -fPIC -O2" LDFLAGS="${EXTRA_LDFLAGS}" make > /dev/null
  cp "lz4.h" "lz4hc.h" "../../../${DOWNLOAD_DIR}/include"
  cp "liblz4.a" "../../../${OUTPUT_DIR}/"
  popd > /dev/null
  echo "✅ Finished building liblz4.a into ${OUTPUT_DIR}!"
}

# ---------------------------------------------------------
# Main Execution Flow
# ---------------------------------------------------------
# Build bzip2
if [ -f "${OUTPUT_DIR}/libbz2.a" ]; then
  echo "libbz2.a already exists in ${OUTPUT_DIR}, skipping bzip2 build."
else
  # Download, verify, and build bzip2
  download_and_verify "bzip2" "$BZIP2_VER" "$BZIP2_DOWNLOAD_BASE" "$BZIP2_SHA256"
  build_bzip2
fi

# Build zlib
if [ -f "${OUTPUT_DIR}/libz.a" ]; then
  echo "libz.a already exists in ${OUTPUT_DIR}, skipping zlib build."
else
  # Download, verify, and build zlib
  download_and_verify "zlib" "$ZLIB_VER" "$ZLIB_DOWNLOAD_BASE" "$ZLIB_SHA256"
  build_zlib
fi

# Build zstd
if [ -f "${OUTPUT_DIR}/libzstd.a" ]; then
  echo "libzstd.a already exists in ${OUTPUT_DIR}, skipping zstd build."
else
  # Download, verify, and build zstd
  download_and_verify "zstd" "$ZSTD_VER" "$ZSTD_DOWNLOAD_BASE" "$ZSTD_SHA256"
  build_zstd
fi

# Build snappy
if [ -f "${OUTPUT_DIR}/libsnappy.a" ]; then
  echo "libsnappy.a already exists in ${OUTPUT_DIR}, skipping snappy build."
else
  # Download, verify, and build snappy
  download_and_verify "snappy" "$SNAPPY_VER" "$SNAPPY_DOWNLOAD_BASE" "$SNAPPY_SHA256"
  build_snappy
fi

# Build LZ4
if [ -f "${OUTPUT_DIR}/liblz4.a" ]; then
  echo "liblz4.a already exists in ${OUTPUT_DIR}, skipping LZ4 build."
else
  # Download, verify, and build LZ4
  download_and_verify "lz4" "$LZ4_VER" "$LZ4_DOWNLOAD_BASE" "$LZ4_SHA256"
  build_lz4
fi

echo "All dependencies have been successfully built and are located in ${OUTPUT_DIR}!"
