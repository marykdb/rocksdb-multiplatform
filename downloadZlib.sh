#!/usr/bin/env bash

KONAN_USER_DIR=${KONAN_DATA_DIR:-"$HOME/.konan"}
ZLIB_TARGET_DIRECTORY="$KONAN_USER_DIR/third-party/zlib"
ZLIB_VERSION="1.2.11"

if [ ! -d "${ZLIB_TARGET_DIRECTORY}/zlib-${ZLIB_VERSION}" ]; then
    echo "Downloading zlib ${ZLIB_VERSION} into $ZLIB_TARGET_DIRECTORY ..."
    mkdir -p "$ZLIB_TARGET_DIRECTORY"
    curl -s -L "https://www.zlib.net/zlib-${ZLIB_VERSION}.tar.gz" | tar -C "$ZLIB_TARGET_DIRECTORY" -xz
else
    echo "zlib ${ZLIB_VERSION} has already been downloaded!"
fi

if [ ! -f "${ZLIB_TARGET_DIRECTORY}/zlib-${ZLIB_VERSION}/libz.a" ]; then
    echo "Building zlib ${ZLIB_VERSION} ..."
    cd "${ZLIB_TARGET_DIRECTORY}/zlib-${ZLIB_VERSION}" || exit
    ./configure
    CXXFLAGS="-mmacosx-version-min=10.11" LDFLAGS="-mmacosx-version-min=10.11" make static
else
    echo "zlib ${ZLIB_VERSION} has already been built!"
fi
