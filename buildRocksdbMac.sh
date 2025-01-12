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

MORE_FLAGS="-DZLIB -DBZIP2 -DSNAPPY -DLZ4 -DZSTD -mmacosx-version-min=11.0 -I../lib/include"
LD_FLAGS="-lbz2 -lz -lz4 -lsnappy"

OUTPUT1=$(make -j$(sysctl -n hw.ncpu) LIB_MODE=static LIBNAME=build/macos_arm64/librocksdb DEBUG_LEVEL=0 OBJ_DIR=build/macos_arm64 EXTRA_CXXFLAGS="-arch arm64 ${MORE_FLAGS}" EXTRA_CFLAGS="-arch arm64 ${MORE_FLAGS}" LD_FLAGS="${LD_FLAGS}" static_lib)
check_build "$OUTPUT1" "arm64"

OUTPUT2=$(make -j$(sysctl -n hw.ncpu) LIB_MODE=static LIBNAME=build/macos_x86_64/librocksdb DEBUG_LEVEL=0 OBJ_DIR=build/macos_x86_64 EXTRA_CXXFLAGS="-arch x86_64 ${MORE_FLAGS}" EXTRA_CFLAGS="-arch x86_64 ${MORE_FLAGS}" LD_FLAGS="${LD_FLAGS}" static_lib)
check_build "$OUTPUT2" "x86_64"

echo "Both builds completed successfully."

