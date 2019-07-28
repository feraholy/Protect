package com.bom.protect;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;

class CheckEmulator {
    private static final String[] NOX_FILES = { "fstab.shamu", "init.shamu.rc", "ueventd.shamu.rc" };
    private static final String[] GENY_FILES = { "/dev/socket/genyd", "/dev/socket/baseband_genyd" };
    private static final String[] MEMU_FILES = { "fstab.intel", "init.intel.rc", "ueventd.intel.rc" };

    private static boolean checkFiles(String[] filename) {
        for (String s : filename) {
            if (new File(s).exists()) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkPackageName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage("com.bluestacks");
        return (intent != null) && (!packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty());
    }

    static boolean isEmulator(Context context) {
        if (checkFiles(NOX_FILES) || checkFiles(GENY_FILES) || checkFiles(MEMU_FILES))
            return true;
        if (checkPackageName(context))
            return true;
        return Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion") || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }
}
