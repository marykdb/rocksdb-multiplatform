#!/usr/bin/env bash

KONAN_USER_DIR=${KONAN_DATA_DIR:-"$HOME/.konan"}
BZIP2_TARGET_DIRECTORY="$KONAN_USER_DIR/third-party/bzip2"
BZIP2_VERSION="1.0.8"

if [ ! -d "${BZIP2_TARGET_DIRECTORY}/bzip2-${BZIP2_VERSION}" ]; then
    echo "Downloading bzip2 ${BZIP2_VERSION} into $BZIP2_TARGET_DIRECTORY ..."
    mkdir -p "$BZIP2_TARGET_DIRECTORY"
    curl -s -L "https://sourceware.org/pub/bzip2/bzip2-${BZIP2_VERSION}.tar.gz" | tar -C "$BZIP2_TARGET_DIRECTORY" -xz
else
    echo "bzip2 ${BZIP2_VERSION} has already been downloaded!"
fi

if [ ! -f "${BZIP2_TARGET_DIRECTORY}/bzip2-${BZIP2_VERSION}/libbz2.a" ]; then
    echo "Building bzip2 ${BZIP2_VERSION} ..."
    cd "${BZIP2_TARGET_DIRECTORY}/bzip2-${BZIP2_VERSION}" || exit
    CXXFLAGS="-mmacosx-version-min=10.11" LDFLAGS="-mmacosx-version-min=10.11" make bzip2
else
    echo "bzip2 ${BZIP2_VERSION} has already been built!"
fi
