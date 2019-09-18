#!/usr/bin/env bash

KONAN_USER_DIR=${KONAN_DATA_DIR:-"$HOME/.konan"}
SNAPPY_TARGET_DIRECTORY="$KONAN_USER_DIR/third-party/snappy"
SNAPPY_VERSION="1.1.7"

if [ ! -d "${SNAPPY_TARGET_DIRECTORY}/snappy-${SNAPPY_VERSION}" ]; then
    echo "Downloading Snappy ${SNAPPY_VERSION} into $SNAPPY_TARGET_DIRECTORY ..."
    mkdir -p "$SNAPPY_TARGET_DIRECTORY"
    curl -s -L "https://github.com/google/snappy/archive/${SNAPPY_VERSION}.tar.gz" | tar -C "$SNAPPY_TARGET_DIRECTORY" -xz
else
    echo "Snappy ${SNAPPY_VERSION} has already been downloaded!"
fi

if [ ! -d "${SNAPPY_TARGET_DIRECTORY}/snappy-${SNAPPY_VERSION}/build" ]; then
    echo "Building Snappy ${SNAPPY_VERSION} ..."
    cd "${SNAPPY_TARGET_DIRECTORY}/snappy-${SNAPPY_VERSION}" || exit
    mkdir build
    cd build && CXXFLAGS="-mmacosx-version-min=10.11" LDFLAGS="-mmacosx-version-min=10.11" cmake ../ && make
else
    echo "Snappy ${SNAPPY_VERSION} has already been built!"
fi
