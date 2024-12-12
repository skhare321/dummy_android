package com.example.dummy_android;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MyFlutterActivity extends FlutterActivity {

    private static final String CHANNEL = "com.example.channel";

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        new MethodChannel(flutterEngine.getDartExecutor(), "com.example.channel")
                .setMethodCallHandler((call, result) -> {
                    if (call.method.equals("submitFormJson")) {
                        // Retrieve form data from Flutter
                        Map<String, String> formData = call.arguments();
                        Log.d("MyFlutterActivity", "Received form data: " + formData.toString());

                        // Process the data or pass it back to the MainActivity, if needed
                        Intent intent = new Intent();
                        for (String key : formData.keySet()) {
                            intent.putExtra(key, formData.get(key));
                        }
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        result.notImplemented();
                    }
                });
    }

}
