#if defined(__MINGW32__)
namespace std {
thread_local void* __once_callable = nullptr;
thread_local void (*__once_call)() = nullptr;
}

extern "C" void __once_proxy() {
    auto call = std::__once_call;
    if (call) {
        call();
        return;
    }

    auto callable = reinterpret_cast<void (*)()>(std::__once_callable);
    if (callable) {
        callable();
    }
}
#endif
