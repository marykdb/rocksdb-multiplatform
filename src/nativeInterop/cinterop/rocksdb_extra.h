#ifndef MARYK_ROCKSDB_EXTRA_H
#define MARYK_ROCKSDB_EXTRA_H

#include "c.h"
#include <stdint.h>
#include <stdlib.h>

static inline size_t* maryk_rocksdb_size_t_lengths(int count, const uint64_t* lengths) {
    if (count <= 0) return NULL;
    size_t* converted = (size_t*)malloc((size_t)count * sizeof(size_t));
    if (converted == NULL) return NULL;
    for (int index = 0; index < count; ++index) {
        if (lengths[index] > (uint64_t)(size_t)-1) {
            free(converted);
            return NULL;
        }
        converted[index] = (size_t)lengths[index];
    }
    return converted;
}

static inline rocksdb_t* maryk_rocksdb_open_column_families_with_lengths(
    const rocksdb_options_t* options, const void* name, int count,
    const void* names, const uint64_t* lengths,
    const rocksdb_options_t* const* column_family_options,
    rocksdb_column_family_handle_t** handles, char** errptr
) {
    size_t* converted = maryk_rocksdb_size_t_lengths(count, lengths);
    if (count > 0 && converted == NULL) return NULL;
    rocksdb_t* result = rocksdb_open_column_families_with_lengths(
        options, (const char*)name, count, (const char* const*)names, converted,
        column_family_options, handles, errptr);
    free(converted);
    return result;
}

static inline rocksdb_t* maryk_rocksdb_open_for_read_only_column_families_with_lengths(
    const rocksdb_options_t* options, const void* name, int count,
    const void* names, const uint64_t* lengths,
    const rocksdb_options_t* const* column_family_options,
    rocksdb_column_family_handle_t** handles, unsigned char error_if_wal_file_exists,
    char** errptr
) {
    size_t* converted = maryk_rocksdb_size_t_lengths(count, lengths);
    if (count > 0 && converted == NULL) return NULL;
    rocksdb_t* result = rocksdb_open_for_read_only_column_families_with_lengths(
        options, (const char*)name, count, (const char* const*)names, converted,
        column_family_options, handles, error_if_wal_file_exists, errptr);
    free(converted);
    return result;
}

static inline rocksdb_t* maryk_rocksdb_open_as_secondary_column_families_with_lengths(
    const rocksdb_options_t* options, const void* name, const void* secondary_path, int count,
    const void* names, const uint64_t* lengths,
    const rocksdb_options_t* const* column_family_options,
    rocksdb_column_family_handle_t** handles, char** errptr
) {
    size_t* converted = maryk_rocksdb_size_t_lengths(count, lengths);
    if (count > 0 && converted == NULL) return NULL;
    rocksdb_t* result = rocksdb_open_as_secondary_column_families_with_lengths(
        options, (const char*)name, (const char*)secondary_path, count, (const char* const*)names, converted,
        column_family_options, handles, errptr);
    free(converted);
    return result;
}

static inline rocksdb_ttl_t* maryk_rocksdb_ttl_open_column_families(
    rocksdb_options_t* options, const void* name, int count, const void* names,
    const uint64_t* lengths, rocksdb_options_t* const* column_family_options,
    const int* ttls, rocksdb_column_family_handle_t** handles, unsigned char read_only,
    char** errptr
) {
    size_t* converted = maryk_rocksdb_size_t_lengths(count, lengths);
    if (count > 0 && converted == NULL) return NULL;
    rocksdb_ttl_t* result = rocksdb_ttl_open_column_families(
        options, (const char*)name, count, (const char* const*)names, converted,
        column_family_options, ttls, handles, read_only, errptr);
    free(converted);
    return result;
}

static inline rocksdb_transactiondb_t* maryk_rocksdb_transactiondb_open_column_families_with_lengths(
    const rocksdb_options_t* options, const rocksdb_transactiondb_options_t* transaction_options,
    const void* name, int count, const void* names, const uint64_t* lengths,
    const rocksdb_options_t* const* column_family_options,
    rocksdb_column_family_handle_t** handles, char** errptr
) {
    size_t* converted = maryk_rocksdb_size_t_lengths(count, lengths);
    if (count > 0 && converted == NULL) return NULL;
    rocksdb_transactiondb_t* result = rocksdb_transactiondb_open_column_families_with_lengths(
        options, transaction_options, (const char*)name, count, (const char* const*)names, converted,
        column_family_options, handles, errptr);
    free(converted);
    return result;
}

static inline rocksdb_optimistictransactiondb_t* maryk_rocksdb_optimistictransactiondb_open_column_families_with_lengths(
    const rocksdb_options_t* options, const void* name, int count,
    const void* names, const uint64_t* lengths,
    const rocksdb_options_t* const* column_family_options,
    rocksdb_column_family_handle_t** handles, char** errptr
) {
    size_t* converted = maryk_rocksdb_size_t_lengths(count, lengths);
    if (count > 0 && converted == NULL) return NULL;
    rocksdb_optimistictransactiondb_t* result = rocksdb_optimistictransactiondb_open_column_families_with_lengths(
        options, (const char*)name, count, (const char* const*)names, converted,
        column_family_options, handles, errptr);
    free(converted);
    return result;
}

static inline rocksdb_column_family_handle_t* maryk_rocksdb_create_column_family_with_length(
    rocksdb_t* db, const rocksdb_options_t* options, const void* name,
    uint64_t length, char** errptr
) {
    if (length > (uint64_t)(size_t)-1) return NULL;
    return rocksdb_create_column_family_with_length(db, options, (const char*)name, (size_t)length, errptr);
}

static inline rocksdb_column_family_handle_t* maryk_rocksdb_ttl_create_column_family(
    rocksdb_ttl_t* db, rocksdb_options_t* options, const void* name,
    uint64_t length, int ttl, char** errptr
) {
    if (length > (uint64_t)(size_t)-1) return NULL;
    return rocksdb_ttl_create_column_family(db, options, (const char*)name, (size_t)length, ttl, errptr);
}

static inline rocksdb_column_family_handle_t* maryk_rocksdb_create_column_family_with_import(
    rocksdb_t* db, const rocksdb_options_t* options, const void* name, uint64_t length,
    const rocksdb_import_column_family_options_t* import_options,
    const rocksdb_export_import_files_metadata_t* metadata, char** errptr
) {
    if (length > (uint64_t)(size_t)-1) return NULL;
    return rocksdb_create_column_family_with_import(
        db, options, (const char*)name, (size_t)length, import_options, metadata, errptr);
}

static inline rocksdb_column_family_handle_t* maryk_rocksdb_create_column_family_with_import_list(
    rocksdb_t* db, const rocksdb_options_t* options, const void* name, uint64_t length,
    const rocksdb_import_column_family_options_t* import_options,
    const rocksdb_export_import_files_metadata_t* const* metadata, uint64_t metadata_count,
    char** errptr
) {
    if (length > (uint64_t)(size_t)-1 || metadata_count > (uint64_t)(size_t)-1) return NULL;
    return rocksdb_create_column_family_with_import_list(
        db, options, (const char*)name, (size_t)length, import_options, metadata,
        (size_t)metadata_count, errptr);
}

static inline void maryk_rocksdb_transaction_singledelete(
    rocksdb_transaction_t* txn,
    const void* key,
    size_t klen,
    char** errptr
) {
    rocksdb_transaction_singledelete(txn, (const char*)key, klen, errptr);
}

static inline void maryk_rocksdb_transaction_singledelete_cf_assume_tracked(
    rocksdb_transaction_t* txn,
    rocksdb_column_family_handle_t* column_family,
    const void* key,
    size_t klen,
    unsigned char assume_tracked,
    char** errptr
) {
    rocksdb_transaction_singledelete_cf_assume_tracked(
        txn,
        column_family,
        (const char*)key,
        klen,
        assume_tracked,
        errptr
    );
}

#endif
