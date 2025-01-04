#!/usr/bin/env bash

echo "Building RocksDB linux ..."

cd "rocksdb" || { echo "Failed to navigate to rocksdb"; exit 1; }
OUTPUT1=$(docker run -v $PWD:/rocks -w /rocks buildpack-deps bash -c "
  apt-get update &&
  apt-get install -y gcc-aarch64-linux-gnu g++-aarch64-linux-gnu &&
  make -j$(sysctl -n hw.ncpu) LIB_MODE=static LIBNAME=build/linux_arm64/librocksdb DEBUG_LEVEL=0 OBJ_DIR=build/linux_arm64 ARCH=arm64 EXTRA_CXXFLAGS=\"-march=armv8-a\" EXTRA_CFLAGS=\"-march=armv8-a\" LD_FLAGS=\"-lbz2 -lz\" CC=aarch64-linux-gnu-gcc CXX=aarch64-linux-gnu-g++ static_lib")
OUTPUT2=$(docker run -v $PWD:/rocks -w /rocks buildpack-deps bash -c "
  apt-get update &&
  apt-get install -y gcc-x86-64-linux-gnu g++-x86-64-linux-gnu &&
  make -j$(sysctl -n hw.ncpu) LIB_MODE=static LIBNAME=build/linux_x86_64/librocksdb DEBUG_LEVEL=0 OBJ_DIR=build/linux_x86_64 ARCH=x86_64 EXTRA_CXXFLAGS=\"-march=x86-64\" EXTRA_CFLAGS=\"-march=x86-64\" LD_FLAGS=\"-lbz2 -lz\" CC=x86_64-linux-gnu-gcc CXX=x86_64-linux-gnu-g++ static_lib")

# Function to check build output
check_build() {
    local output="$1"
    local arch="$2"

    # Check for success messages
    if echo "$output" | grep -q "AR       build/linux_$arch/librocksdb.a"; then
        echo "** BUILD SUCCEEDED for $arch **"
    elif echo "$output" | grep -q "make: Nothing to be done for 'static_lib'."; then
        echo "** BUILD NOT NEEDED for $arch (Already up to date) **"
    else
        echo "** BUILD FAILED for $arch **"
        echo "$output"
        exit 1
    fi
}

# Check the build results
check_build "$OUTPUT1" "arm64"
check_build "$OUTPUT2" "x86_64"

echo "Both builds completed successfully."

