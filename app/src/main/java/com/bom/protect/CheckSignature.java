package com.bom.protect;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import java.security.MessageDigest;

class CheckSignature {
    private static final String SIGNATURE = "Icfbw/ycu7xy/vwUFeiFPDWH40Y=";

    static boolean checkSignatures(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                MessageDigest sha = MessageDigest.getInstance("SHA");
                sha.update(signature.toByteArray());
                final String currentSignature = Base64.encodeToString(sha.digest(), Base64.DEFAULT);
                if (SIGNATURE.equals(currentSignature)) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
