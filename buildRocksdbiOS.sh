#!/usr/bin/env bash

# Exit immediately if a command exits with a non-zero status.
set -e

xcrun --sdk iphonesimulator --show-sdk-path

echo "Building Rocksdb iOS ..."

# Navigate to the rocksdb directory
cd "rocksdb" || { echo "Failed to navigate to rocksdb"; exit 1; }

# Define build directories
BUILD_DIR_DEVICE="build/ios_arm64"
BUILD_DIR_SIMULATOR="build/ios_simulator_arm64"

# Function to check build output
check_build() {
    local output="$1"
    local arch="$2"

    # Check for success messages
    if echo "$output" | grep -q "AR       build/ios_$arch/librocksdb.a"; then
        echo "** BUILD SUCCEEDED for $arch **"
    elif echo "$output" | grep -q "make: Nothing to be done for \`static_lib'."; then
        echo "** BUILD NOT NEEDED for $arch (Already up to date) **"
    else
        echo "** BUILD FAILED for $arch **"
        echo "$output"
        exit 1
    fi
}

# Build for iOS Device (arm64)
echo "Building for iOS Device (arm64)..."
OUTPUT1=$(make -j$(sysctl -n hw.ncpu) \
    LIB_MODE=static \
    LIBNAME="$BUILD_DIR_DEVICE/librocksdb" \
    DEBUG_LEVEL=0 \
    OBJ_DIR="$BUILD_DIR_DEVICE" \
    EXTRA_CXXFLAGS="-arch arm64 -miphoneos-version-min=13.0 -isysroot $(xcrun --sdk iphoneos --show-sdk-path)" \
    EXTRA_CFLAGS="-arch arm64 -miphoneos-version-min=13.0 -isysroot $(xcrun --sdk iphoneos --show-sdk-path)" \
    static_lib)

check_build "$OUTPUT1" "arm64"

# Build for iOS Simulator (arm64)
echo "Building for iOS Simulator (arm64)..."
OUTPUT2=$(make -j$(sysctl -n hw.ncpu) \
    LIB_MODE=static \
    LIBNAME="$BUILD_DIR_SIMULATOR/librocksdb" \
    DEBUG_LEVEL=0 \
    OBJ_DIR="$BUILD_DIR_SIMULATOR" \
    EXTRA_CXXFLAGS="-arch arm64 -target arm64-apple-ios13.0-simulator -isysroot $(xcrun --sdk iphonesimulator --show-sdk-path)" \
    EXTRA_CFLAGS="-arch arm64 -target arm64-apple-ios13.0-simulator -isysroot $(xcrun --sdk iphonesimulator --show-sdk-path)" \
    static_lib)

check_build "$OUTPUT2" "simulator_arm64"

echo "Both builds completed successfully."