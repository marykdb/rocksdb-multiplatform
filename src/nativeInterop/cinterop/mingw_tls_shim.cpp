#if defined(__MINGW32__)
namespace std {
thread_local void* __once_callable = nullptr;
thread_local void (*__once_call)() = nullptr;
}
#endif
