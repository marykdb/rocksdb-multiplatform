#!/usr/bin/env bash

set -euo pipefail

ARCH=""
for arg in "$@"; do
  case $arg in
    --arch=*)
      ARCH="${arg#*=}"
      ;;
    *)
      echo "Unknown option: $arg"
      exit 1
      ;;
  esac
done

if [ -z "${ARCH}" ]; then
  echo "Usage: $0 --arch=x86_64"
  exit 1
fi

case "$ARCH" in
  x86_64)
    CC="x86_64-w64-mingw32-gcc"
    CXX="x86_64-w64-mingw32-g++"
    CMAKE_TOOLCHAIN_FLAGS="-DCMAKE_SYSTEM_NAME=Windows -DCMAKE_SYSTEM_PROCESSOR=x86_64"
    EXTRA_FLAGS="-march=x86-64"
    OBJ_DIR="rocksdb/build/mingw_x86_64"
    ;;
  i686)
    CC="i686-w64-mingw32-gcc"
    CXX="i686-w64-mingw32-g++"
    CMAKE_TOOLCHAIN_FLAGS="-DCMAKE_SYSTEM_NAME=Windows -DCMAKE_SYSTEM_PROCESSOR=i686"
    EXTRA_FLAGS="-march=i686"
    OBJ_DIR="rocksdb/build/mingw_i686"
    ;;
  *)
    echo "Unsupported ARCH: $ARCH"
    exit 1
    ;;
esac

get_num_cores() {
  if command -v nproc >/dev/null 2>&1; then
    nproc
  elif [[ "$OSTYPE" == "darwin"* ]]; then
    sysctl -n hw.ncpu
  else
    echo "Error: Unable to determine the number of CPU cores for parallel build." >&2
    exit 1
  fi
}

echo "Building RocksDB for Windows (MinGW) with ARCH=${ARCH}"
echo "Compiler: $CC / $CXX"

# Make sure we are in the RocksDB repo root (adjust if needed)
cd "$(dirname "$0")/" || { echo "Failed to navigate to rocksdb"; exit 1; }

# Check if the output library already exists
if [ -f "${OBJ_DIR}/librocksdb.a" ]; then
  echo "** BUILD SKIPPED: ${OBJ_DIR}/librocksdb.a already exists **"
  exit 0
fi

# Create build directory if it doesn't exist
mkdir -p "$OBJ_DIR"

NUM_CORES=$(get_num_cores)

# Configure with CMake
echo "Configuring RocksDB with CMake..."
cmake -S . -B "$OBJ_DIR" \
  $CMAKE_TOOLCHAIN_FLAGS \
  -DCMAKE_C_COMPILER="$CC" \
  -DCMAKE_CXX_COMPILER="$CXX" \
  -DCMAKE_C_FLAGS="$EXTRA_FLAGS" \
  -DCMAKE_CXX_FLAGS="$EXTRA_FLAGS" \
  -DCMAKE_BUILD_TYPE=Release \
  -DPORTABLE=1 \
  -DCMAKE_INSTALL_PREFIX="$OBJ_DIR" \
  -DWITH_GFLAGS=OFF \
  -DWITH_SNAPPY=ON \
  -DWITH_LZ4=ON \
  -DWITH_ZLIB=ON \
  -DWITH_ZSTD=ON \
  -DWITH_BZ2=ON \
  -DROCKSDB_BUILD_SHARED=OFF \
  -DFAIL_ON_WARNINGS=OFF \
  -DWITH_TESTS=OFF \
  -DWITH_TOOLS=OFF

# Build with CMake
echo "Building RocksDB with CMake..."
output=$(cmake --build "$OBJ_DIR" --config Release -j --target rocksdb --parallel "${NUM_CORES}" 2>&1)

# Check if the library was built successfully
if [ -f "${OBJ_DIR}/rocksdb-build/librocksdb.a" ]; then
    echo "** BUILD SUCCEEDED for $ARCH **"
elif echo "$output" | grep -q "up-to-date"; then
    echo "** BUILD NOT NEEDED for $ARCH (Already up to date) **"
else
    echo "** BUILD FAILED for $ARCH **"
    echo "$output"
    exit 1
fi
