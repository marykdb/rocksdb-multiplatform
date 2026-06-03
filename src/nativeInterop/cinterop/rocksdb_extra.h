#ifndef MARYK_ROCKSDB_EXTRA_H
#define MARYK_ROCKSDB_EXTRA_H

#include "c.h"

extern void rocksdb_status_ptr_get_error(rocksdb_status_ptr_t* status, char** errptr);

static inline rocksdb_column_family_handle_t* maryk_rocksdb_create_column_family_with_import(
    rocksdb_t* db,
    const rocksdb_options_t* column_family_options,
    const void* column_family_name,
    const rocksdb_import_column_family_options_t* import_options,
    const rocksdb_export_import_files_metadata_t* metadata,
    char** errptr
) {
    return rocksdb_create_column_family_with_import(
        db,
        column_family_options,
        (const char*)column_family_name,
        import_options,
        metadata,
        errptr
    );
}

static inline rocksdb_column_family_handle_t* maryk_rocksdb_create_column_family_with_import_list(
    rocksdb_t* db,
    const rocksdb_options_t* column_family_options,
    const void* column_family_name,
    const rocksdb_import_column_family_options_t* import_options,
    const rocksdb_export_import_files_metadata_t* const* metadata,
    size_t metadata_count,
    char** errptr
) {
    return rocksdb_create_column_family_with_import_list(
        db,
        column_family_options,
        (const char*)column_family_name,
        import_options,
        metadata,
        metadata_count,
        errptr
    );
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
