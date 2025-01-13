#!/usr/bin/env bash
#
# Build script for RocksDB on Apple platforms:
# iOS, iOS-simulator, macOS, watchOS, watchOS-simulator, tvOS, tvOS-simulator
#
# Usage Examples:
#   ./build_rocksdb.sh --platform=ios --arch=arm64
#   ./build_rocksdb.sh --platform=ios --simulator --arch=arm64
#   ./build_rocksdb.sh --platform=macos --arch=arm64
#   ./build_rocksdb.sh --platform=macos --arch=x86_64
#
#   Not supported yet because watchos needs arm64_32 which is not supported by the dependencies
#   ./build_rocksdb.sh --platform=watchos --arch=arm64
#   ./build_rocksdb.sh --platform=watchos --simulator --arch=x86_64
#   Not supported because tvOS limits certain calls which are needed for RocksDB
#   ./build_rocksdb.sh --platform=tvos --arch=arm64
#   ./build_rocksdb.sh --platform=tvos --simulator --arch=x86_64
#

set -e  # Exit immediately if any command fails.

###############################################################################
# Parse arguments
###############################################################################
PLATFORM="macos"     # e.g. ios, macos, watchos, tvos
SIMULATOR=false # If true, build for simulator (for iOS, watchOS, tvOS)
ARCH=""         # e.g. arm64, x86_64, etc.

for arg in "$@"; do
  case $arg in
    --platform=*)
      PLATFORM="${arg#*=}"
      ;;
    --simulator)
      SIMULATOR=true
      ;;
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
if [ -z "$PLATFORM" ] || [ -z "$ARCH" ]; then
  echo "Usage: $0 --platform=<ios|macos|watchos|tvos> [--simulator] --arch=<arm64|x86_64|...>"
  exit 1
fi

echo "Building RocksDB for: $PLATFORM, Arch: $ARCH, Simulator?: $SIMULATOR"

###############################################################################
# Determine SDK name, min OS version, and associated flags (once per platform)
###############################################################################
SDK_NAME=""
MIN_VERSION=""
MIN_FLAG=""
TARGET_TRIPLE=""

case "$PLATFORM" in
  ios)
    SDK_NAME=$([[ "$SIMULATOR" == true ]] && echo "iphonesimulator" || echo "iphoneos")
    MIN_VERSION="13.0"
    if [ "$SIMULATOR" = true ]; then
      MIN_FLAG="-mios-simulator-version-min=${MIN_VERSION}"
      TARGET_TRIPLE="-target ${ARCH}-apple-ios${MIN_VERSION}-simulator"
    else
      MIN_FLAG="-miphoneos-version-min=${MIN_VERSION}"
      TARGET_TRIPLE="-arch ${ARCH}"
    fi
    ;;
  macos)
    SDK_NAME="macosx"
    MIN_VERSION="11.0"
    MIN_FLAG="-mmacosx-version-min=${MIN_VERSION}"
    TARGET_TRIPLE="-arch ${ARCH}"
    ;;
  watchos)
    SDK_NAME=$([[ "$SIMULATOR" == true ]] && echo "watchsimulator" || echo "watchos")
    MIN_VERSION="7.0"
    if [ "$SIMULATOR" = true ]; then
      MIN_FLAG="-mwatchos-simulator-version-min=${MIN_VERSION}"
      TARGET_TRIPLE="-target ${ARCH}-apple-watchos${MIN_VERSION}-simulator"
    else
      MIN_FLAG="-mwatchos-version-min=${MIN_VERSION}"
      TARGET_TRIPLE="-arch ${ARCH}"
    fi
    ;;
  tvos)
    SDK_NAME=$([[ "$SIMULATOR" == true ]] && echo "appletvsimulator" || echo "appletvos")
    MIN_VERSION="13.0"
    if [ "$SIMULATOR" = true ]; then
      MIN_FLAG="-mtvos-simulator-version-min=${MIN_VERSION}"
      TARGET_TRIPLE="-target ${ARCH}-apple-tvos${MIN_VERSION}-simulator"
    else
      MIN_FLAG="-mtvos-version-min=${MIN_VERSION}"
      TARGET_TRIPLE="-arch ${ARCH}"
    fi
    ;;
  *)
    echo "Unsupported platform: $PLATFORM"
    exit 1
    ;;
esac

# Get the actual SDK path
SDK_PATH=$(xcrun --sdk "$SDK_NAME" --show-sdk-path)
if [ -z "$SDK_PATH" ]; then
  echo "Failed to get SDK path for $SDK_NAME"
  exit 1
fi

###############################################################################
# Build directory name (once per platform + simulator state)
###############################################################################
SIM_SUFFIX=$([[ "$SIMULATOR" == true ]] && echo "_simulator" || echo "")

BUILD_DIR="build/${PLATFORM}${SIM_SUFFIX}_${ARCH}"

###############################################################################
# Common compiler & linker flags
###############################################################################
EXTRA_FLAGS=""
EXTRA_FLAGS+=" $MIN_FLAG"
EXTRA_FLAGS+=" $TARGET_TRIPLE"
EXTRA_FLAGS+=" -isysroot $SDK_PATH"
EXTRA_FLAGS+=" -I../lib/include"
EXTRA_FLAGS+=" -DZLIB -DBZIP2 -DSNAPPY -DLZ4 -DZSTD"

LD_FLAGS="-lbz2 -lz -lz4 -lsnappy"

###############################################################################
# Move into rocksdb directory
###############################################################################
cd "rocksdb" || { echo "Could not enter rocksdb directory"; exit 1; }

###############################################################################
# Function to check the build output
###############################################################################
check_build() {
  local output="$1"
  local build_path="$2"

  if echo "$output" | grep -q "AR       $build_path/librocksdb.a"; then
    echo "** BUILD SUCCEEDED for $build_path **"
  elif echo "$output" | grep -q "Nothing to be done for \`static_lib'."; then
    echo "** BUILD NOT NEEDED for $build_path (Already up to date) **"
  else
    echo "** BUILD FAILED for $build_path **"
    echo "$output"
    exit 1
  fi
}

###############################################################################
# Build (make)
###############################################################################
echo "Starting build for: $BUILD_DIR"

if [ -f "${BUILD_DIR}/librocksdb.a" ]; then
  echo "** BUILD SKIPPED: ${BUILD_DIR}/librocksdb.a already exists **"
  exit 0
fi

BUILD_OUTPUT=$(
  make -j"$(sysctl -n hw.ncpu)" \
    LIB_MODE=static \
    LIBNAME="${BUILD_DIR}/librocksdb" \
    DEBUG_LEVEL=0 \
    OBJ_DIR="${BUILD_DIR}" \
    EXTRA_CXXFLAGS="${EXTRA_FLAGS}" \
    EXTRA_CFLAGS="${EXTRA_FLAGS}" \
    LD_FLAGS="${LD_FLAGS}" \
    static_lib
)

check_build "$BUILD_OUTPUT" "$BUILD_DIR"

echo "✅ RocksDB build completed successfully for $PLATFORM"
