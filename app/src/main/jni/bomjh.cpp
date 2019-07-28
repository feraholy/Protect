#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/ptrace.h>
#include <dlfcn.h>

#define TAG  "Bomjh_Log"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

typedef unsigned char boool;
typedef boool (*debug)();

bool checkDebug() {
    void *handle = NULL;
    handle = dlopen("/system/lib/libdvm.so", RTLD_LAZY);
    if (handle != NULL) {
        debug sym = (debug)dlsym(handle, "_Z25dvmDbgIsDebuggerConnectedv");
        if (sym != NULL) {
            boool ret = sym();
            if (ret == 1) {
                return true;
            }
        }
    } else {
        handle = dlopen("/system/lib/libart.so", RTLD_LAZY);
        debug sym = (debug)dlsym(handle, "_ZN3art3Dbg16IsDebuggerActiveEv");
        if (sym != NULL) {
            boool ret = sym();
            if (ret == 1) {
                return true;
            }
        }
    }
    return false;
}

bool checkCmdline() {
    char buff[32];
    char line[32];
    snprintf(buff, sizeof(buff), "/proc/%d/cmdline", getppid());
    FILE *fp = fopen(buff, "r");
    if (fp != NULL) {
        fgets(line, sizeof(line), fp);
        fclose(fp);
    }
    return strcmp(line, "gdb") == 0;
}

bool checkStatus() {
    int pid;
    char line[1024];
    const char *str = "TracerPid:";

    FILE *fp = fopen("/proc/self/status", "r");
    if (fp != NULL) {
        while (fgets(line, sizeof(line), fp)) {
            if (!strncmp(line, "TracerPid:", strlen(str))) {
                sscanf(line, "TracerPid: %d", &pid);
                LOGD("pid:%d", pid);
                if (pid != 0) {
                    return true;
                }
            }
        }
        fclose(fp);
    }
    return false;
}

bool checkStat() {
    char line[1024];

    FILE *fp = fopen("/proc/self/stat", "r");
    if (fp != NULL) {
        while (fgets(line, sizeof(line), fp)) {
            char *str;
            str = strtok(line, " ");
            while (str) {
                LOGD("%s", str);
                if (strcmp(str, "T") == 0 || strcmp(str, "t") == 0)
                    return true;
                str = strtok(NULL, " ");
            }
        }
        fclose(fp);
    }
    return false;
}

bool checkPtrace() {
    return ptrace(PTRACE_TRACEME, 0, 1, 0) < 0;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_bom_protect_MainActivity_toastFromJNI(JNIEnv *env, jobject) {
    return env->NewStringUTF("toastFromJNI");
}

bool checkMaps() {
    char line[1024];
    FILE *fp = fopen("/proc/self/maps", "r");
    if (fp != NULL) {
        while (fgets(line, sizeof(line), fp)) {
            if (strstr(line, "substrate") || strstr(line, "Xposed")) {
                return true;
            }
        }
        fclose(fp);
    }
    return false;
}

bool checkLibrary() {
    void *handle = dlopen("/data/data/com.testunitymono.app/lib/libmono.so", RTLD_LAZY);
    if (handle != NULL) {
        void *sym = dlsym(handle, "mono_runtime_invoke");
        if (sym != NULL) {
            unsigned char *hook = static_cast<unsigned char *>(sym);
            unsigned char orig[4] = {0x00, 0x48, 0x2D, 0xE9};
            int i = 0;
            while (i < 4) {
                if (*hook != orig[i])
                    return true;
                hook++;
                i++;
            }
            return false;
        }
    }
    return false;
}

__attribute__((constructor))
void bomjh_main() {
    if (checkCmdline())
        LOGD("Debugger Detected!!");
    if (checkStatus())
        LOGD("Debugger Detected!!");
    if (checkDebug())
        LOGD("Debugger Detected!!");
    if (checkStat())
        LOGD("Debugger Detected!!");
    if (checkPtrace())
        LOGD("Ptrace Detected!!");
    if (checkMaps())
        LOGD("Hook Detected!!");
    if (checkLibrary())
        LOGD("Hook Detected!!");
}