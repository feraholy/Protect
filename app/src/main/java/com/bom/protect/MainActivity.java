package com.bom.protect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private native String toastFromJNI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.loadLibrary("bomjh");

        if (CheckRoot.a() || CheckRoot.b() || CheckRoot.c())
            Toast.makeText(this, "Root Detected!!", Toast.LENGTH_SHORT).show();
        if (!CheckSignature.checkSignatures(this))
            Toast.makeText(this, "Invalid Signatures!!", Toast.LENGTH_SHORT).show();
        if (CheckEmulator.isEmulator(this))
            Toast.makeText(this, "Emulator Detected!!", Toast.LENGTH_SHORT).show();

        toast();
    }

    private void toast() {
        Toast.makeText(this, toastFromJNI(), Toast.LENGTH_SHORT).show();
    }
}
