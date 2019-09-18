#!/usr/bin/env bash

KONAN_USER_DIR=${KONAN_DATA_DIR:-"$HOME/.konan"}
RDB_TARGET_DIRECTORY="$KONAN_USER_DIR/third-party/rocksdb"
RDB_VERSION="6.2.2"

if [ ! -d "${RDB_TARGET_DIRECTORY}/rocksdb-${RDB_VERSION}/include/rocksdb" ]; then
    echo "Downloading RocksDB ${RDB_VERSION} into $RDB_TARGET_DIRECTORY ..."
    mkdir -p "$RDB_TARGET_DIRECTORY"
    curl -s -L "https://github.com/facebook/rocksdb/archive/v${RDB_VERSION}.tar.gz" | tar -C $RDB_TARGET_DIRECTORY -xz
else
    echo "RocksDB ${RDB_VERSION} has already been downloaded!"
fi

if [ ! -f "${RDB_TARGET_DIRECTORY}/rocksdb-${RDB_VERSION}/librocksdb.a" ]; then
    echo "Building RocksDB ${RDB_VERSION} ..."
    cd "${RDB_TARGET_DIRECTORY}/rocksdb-${RDB_VERSION}" || exit
    CXXFLAGS="-mmacosx-version-min=10.11" LDFLAGS="-mmacosx-version-min=10.11" make static_lib
else
    echo "RocksDB ${RDB_VERSION} has already been built!"
fi
