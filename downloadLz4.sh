#!/usr/bin/env bash

KONAN_USER_DIR=${KONAN_DATA_DIR:-"$HOME/.konan"}
LZ4_TARGET_DIRECTORY="$KONAN_USER_DIR/third-party/lz4"
LZ4_VERSION="1.9.2"

if [ ! -d "${LZ4_TARGET_DIRECTORY}/lz4-${LZ4_VERSION}" ]; then
    echo "Downloading lz4 ${LZ4_VERSION} into $LZ4_TARGET_DIRECTORY ..."
    mkdir -p "$LZ4_TARGET_DIRECTORY"
    curl -s -L "https://github.com/lz4/lz4/archive/v${LZ4_VERSION}.tar.gz" | tar -C "$LZ4_TARGET_DIRECTORY" -xz
else
    echo "lz4 ${LZ4_VERSION} has already been downloaded!"
fi

if [ ! -f "${LZ4_TARGET_DIRECTORY}/lz4-${LZ4_VERSION}/lib/liblz4.a" ]; then
    echo "Building lz4 ${LZ4_VERSION} ..."
    cd "${LZ4_TARGET_DIRECTORY}/lz4-${LZ4_VERSION}" || exit
    CXXFLAGS="-mmacosx-version-min=10.11" LDFLAGS="-mmacosx-version-min=10.11" make lib
else
    echo "lz4 ${LZ4_VERSION} has already been built!"
fi
