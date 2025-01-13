#!/usr/bin/env bash

ARCH="" # e.g. arm64, x86_64, etc.

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

# Validate
if [ -z "$ARCH" ]; then
  echo "Usage: $0 --arch=<arm64|x86_64>"
  exit 1
fi

echo "Building RocksDB for $ARCH..."

# Optional: navigate to the rocksdb directory
cd "rocksdb" || { echo "Failed to navigate to rocksdb"; exit 1; }

# Simple function to check build output
check_build() {
  local output="$1"
  local folder="$2"

  if echo "$output" | grep -q "AR       build/$folder/librocksdb.a"; then
      echo "** BUILD SUCCEEDED for $ARCH **"
  elif echo "$output" | grep -q "make: Nothing to be done for 'static_lib'."; then
      echo "** BUILD NOT NEEDED for $ARCH (Already up to date) **"
  else
      echo "** BUILD FAILED for $ARCH **"
      echo "$output"
      exit 1
  fi
}

EXTRA_FLAGS="-I../lib/include -DZLIB -DBZIP2 -DSNAPPY -DLZ4 -DZSTD "

if [[ "$ARCH" == "arm64" ]]; then
  folder="linux_arm64"
  cc="$HOME/.konan/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/bin/aarch64-unknown-linux-gnu-gcc"
  cxx="$HOME/.konan/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/bin/aarch64-unknown-linux-gnu-g++"
  EXTRA_FLAGS+="-march=armv8-a"
else
  folder="linux_x86_64"
  cc="$HOME/.konan/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/bin/x86_64-unknown-linux-gnu-gcc"
  cxx="$HOME/.konan/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/bin/x86_64-unknown-linux-gnu-g++"
  EXTRA_FLAGS+="-march=x86-64"
fi

# Check if the build output already exists
if [ -f "build/$folder/librocksdb.a" ]; then
  echo "** BUILD SKIPPED: build/$folder/librocksdb.a already exists **"
  exit 0
fi

if [[ "$(uname -s)" == "Linux" ]]; then
  output=$(
    make -j"$(nproc)" \
      LIB_MODE=static \
      LIBNAME="build/$folder/librocksdb" \
      DEBUG_LEVEL=0 \
      CC=$cc \
      CXX=$cxx \
      OBJ_DIR="build/$folder" \
      EXTRA_CXXFLAGS="$EXTRA_FLAGS" \
      EXTRA_CFLAGS="$EXTRA_FLAGS" \
      PORTABLE=1 \
      LD_FLAGS="-lbz2 -lz -lz4 -lsnappy" \
      static_lib
  )

  check_build "$output" "$folder"
else
  echo "Should only build on linux"
  exit 1
fi
