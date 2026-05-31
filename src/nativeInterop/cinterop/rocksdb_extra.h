#ifndef MARYK_ROCKSDB_EXTRA_H
#define MARYK_ROCKSDB_EXTRA_H

#include "c.h"

extern void rocksdb_status_ptr_get_error(rocksdb_status_ptr_t* status, char** errptr);

#endif
