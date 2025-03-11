cmake_minimum_required(VERSION 3.10)
project(RocksDBWithDependencies)

set(lz4_INCLUDE_DIRS "${CMAKE_CURRENT_SOURCE_DIR}/lib/lz4-1.9.4/lib")
set(lz4_ROOT_DIR "${CMAKE_CURRENT_SOURCE_DIR}/lib/lz4-1.9.4")
set(lz4_LIBRARIES "${CMAKE_CURRENT_BINARY_DIR}/liblz4.so")

set(Snappy_INCLUDE_DIRS "${CMAKE_CURRENT_SOURCE_DIR}/lib/snappy-1.2.1")
set(Snappy_ROOT_DIR "${CMAKE_CURRENT_SOURCE_DIR}/lib/snappy-1.2.1")
set(Snappy_DIR "${Snappy_ROOT_DIR}")
set(Snappy_LIBRARIES "${CMAKE_CURRENT_BINARY_DIR}/libsnappy.so")

set(BZIP2_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/lib/bzip2-1.0.8")
set(BZIP2_LIBRARIES "${CMAKE_CURRENT_BINARY_DIR}/libbz2.so")

set(ZSTD_INCLUDE_DIRS "${CMAKE_CURRENT_SOURCE_DIR}/lib/zstd-1.5.6/lib")
set(ZSTD_LIBRARIES "${CMAKE_CURRENT_BINARY_DIR}/libzstd.so")

set(ZLIB_INCLUDE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/lib/zlib-1.3.1")
set(ZLIB_LIBRARIES "${CMAKE_CURRENT_BINARY_DIR}/libzlib.so")

include_directories(${lz4_INCLUDE_DIRS})
include_directories(${Snappy_ROOT_DIR})
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/lib/snappy/include)
include_directories(${ZLIB_INCLUDE_DIRS})

if(NOT TARGET Snappy::snappy)
  add_library(Snappy::snappy STATIC IMPORTED)
  set_target_properties(Snappy::snappy PROPERTIES
    IMPORTED_LOCATION "${CMAKE_CURRENT_BINARY_DIR}/libsnappy.so"  # Adjust if needed for your platform
    INTERFACE_INCLUDE_DIRECTORIES "${CMAKE_CURRENT_SOURCE_DIR}/lib/snappy-1.2.1"
  )
endif()

# Include the RocksDB project
add_subdirectory(rocksdb rocksdb-build)


