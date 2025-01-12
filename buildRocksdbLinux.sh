#!/usr/bin/env bash

echo "Building RocksDB..."

# Optional: navigate to the rocksdb directory
cd "rocksdb" || { echo "Failed to navigate to rocksdb"; exit 1; }

# Simple function to check build output
check_build() {
  local output="$1"
  local arch="$2"

  if echo "$output" | grep -q "AR       build/linux_${arch}/librocksdb.a"; then
      echo "** BUILD SUCCEEDED for $arch **"
  elif echo "$output" | grep -q "make: Nothing to be done for 'static_lib'."; then
      echo "** BUILD NOT NEEDED for $arch (Already up to date) **"
  else
      echo "** BUILD FAILED for $arch **"
      echo "$output"
      exit 1
  fi
}

build_for_arch() {
  local arch="$1"

  # Set up arch-specific variables
  local pkgs
  local cc
  local cxx
  local extra_cflags
  local extra_cxxflags

  if [[ "$arch" == "arm64" ]]; then
    pkgs="gcc-aarch64-linux-gnu g++-aarch64-linux-gnu"
    cc="aarch64-linux-gnu-gcc"
    cxx="aarch64-linux-gnu-g++"
    extra_cflags="-march=armv8-a"
    extra_cxxflags="-march=armv8-a"
  else
    pkgs="gcc-x86-64-linux-gnu g++-x86-64-linux-gnu"
    cc="x86_64-linux-gnu-gcc"
    cxx="x86_64-linux-gnu-g++"
    extra_cflags="-march=x86-64"
    extra_cxxflags="-march=x86-64"
  fi

  echo "Building for $arch..."

  # Run either in Docker (non-Linux) or natively (Linux)
  local output
  if [[ "$(uname -s)" == "Linux" ]]; then
    echo "Detected Linux — building $arch directly..."
    output=$(
      apt-get update &&
      apt-get install -y $pkgs &&
      make -j"$(nproc)" \
        LIB_MODE=static \
        LIBNAME="build/linux_${arch}/librocksdb" \
        DEBUG_LEVEL=0 \
        OBJ_DIR="build/linux_${arch}" \
        EXTRA_CXXFLAGS="$extra_cxxflags" \
        EXTRA_CFLAGS="$extra_cflags" \
        LD_FLAGS="-lbz2 -lz" \
        static_lib
    )
  else
    echo "Detected non-Linux system — building $arch via Docker..."
    output=$(
      docker run --rm \
        -v "$PWD":/rocks \
        -w /rocks \
        buildpack-deps \
        bash -c "
          apt-get update &&
          apt-get install -y $pkgs &&
          make -j\$(nproc) \
               LIB_MODE=static \
               LIBNAME=build/linux_${arch}/librocksdb \
               DEBUG_LEVEL=0 \
               OBJ_DIR=build/linux_${arch} \
               EXTRA_CXXFLAGS='$extra_cxxflags' \
               EXTRA_CFLAGS='$extra_cflags' \
               LD_FLAGS='-lbz2 -lz' \
               CC=$cc \
               CXX=$cxx \
               static_lib
        "
    )
  fi

  # Check build results
  check_build "$output" "$arch"
}

build_for_arch "x86_64"
build_for_arch "aarch64"
