package com.bom.protect;

import android.os.Build;

import java.io.File;
import java.io.IOException;

class CheckRoot {
    static boolean a() {
        String str = Build.TAGS;
        return (str != null) && (str.contains("test-keys"));
    }

    static boolean b() {
        String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
                "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    static boolean c() {
        try {
            Runtime.getRuntime().exec("su").destroy();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
