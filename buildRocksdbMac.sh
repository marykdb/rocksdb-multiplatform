#!/usr/bin/env bash

echo "Building RocksDB macOS ..."
cd "rocksdb" || exit

# Function to check build output
check_build() {
    local output="$1"
    local arch="$2"

    # Check for success messages
    if echo "$output" | grep -q "AR       build/macos_$arch/librocksdb.a"; then
        echo "** BUILD SUCCEEDED for $arch **"
    elif echo "$output" | grep -q "make: Nothing to be done for \`static_lib'."; then
        echo "** BUILD NOT NEEDED for $arch (Already up to date) **"
    else
        echo "** BUILD FAILED for $arch **"
        echo "$output"
        exit 1
    fi
}

OUTPUT1=$(make -j$(sysctl -n hw.ncpu) LIB_MODE=static LIBNAME=build/macos_arm64/librocksdb DEBUG_LEVEL=0 OBJ_DIR=build/macos_arm64 EXTRA_CXXFLAGS="-arch arm64 -mmacosx-version-min=11.0" EXTRA_CFLAGS="-arch arm64 -mmacosx-version-min=11.0" LD_FLAGS="-lbz2 -lz" static_lib)
check_build "$OUTPUT1" "arm64"

OUTPUT2=$(make -j$(sysctl -n hw.ncpu) LIB_MODE=static LIBNAME=build/macos_x86_64/librocksdb DEBUG_LEVEL=0 OBJ_DIR=build/macos_x86_64 EXTRA_CXXFLAGS="-arch x86_64 -mmacosx-version-min=11.0" EXTRA_CFLAGS="-arch x86_64 -mmacosx-version-min=11.0" LD_FLAGS="-lbz2 -lz" static_lib)
check_build "$OUTPUT2" "x86_64"

echo "Both builds completed successfully."

