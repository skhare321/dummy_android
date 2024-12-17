package com.example.dummy_android;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MyFlutterActivity extends FlutterActivity {

    private static final String CHANNEL = "com.example.channel";
    private static MethodChannel methodChannel;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        methodChannel = new MethodChannel(flutterEngine.getDartExecutor(), CHANNEL);

        methodChannel.setMethodCallHandler((call, result) -> {
            if (call.method.equals("submitFormJson")) {
                Map<String, String> formData = call.arguments();
                if (formData != null) {
                    Map<String, String> processedData = new HashMap<>();
                    for (Map.Entry<String, String> entry : formData.entrySet()) {
                        processedData.put(entry.getKey(), entry.getValue());
                    }
                    Log.d("MyFlutterActivity", "Processed form data: " + processedData);

                    Intent intent = new Intent();
                    for (String key : processedData.keySet()) {
                        intent.putExtra(key, processedData.get(key));
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }else if (call.method.equals("getAllUsers")) {
                result.success(null);
            }else if (call.method.equals("storeDataInFlutter")) {
                result.success(null);
            }else if (call.method.equals("sendDataViaApi")) {
                result.success(null);
            } else {
                result.notImplemented();
            }
        });
    }

    /**
     * Static method to invoke the "getAllUsers" method on Flutter.
     */
    public static void getAllUsersFromFlutter() {
        if (methodChannel != null) {
            methodChannel.invokeMethod("getAllUsers", null, new MethodChannel.Result() {
                @Override
                public void success(Object result) {
                    if (result instanceof List) {
                        List<Map<String, Object>> users = (List<Map<String, Object>>) result;
                        for (Map<String, Object> user : users) {
                            Log.d("MyFlutterActivity", "User from Flutter DB: " + user);
                        }
                    } else {
                        Log.e("MyFlutterActivity", "Unexpected result format: " + result);
                    }
                }

                @Override
                public void error(String errorCode, String errorMessage, Object errorDetails) {
                    Log.e("MyFlutterActivity", "Error fetching users: " + errorMessage);
                }

                @Override
                public void notImplemented() {
                    Log.e("MyFlutterActivity", "getAllUsers method not implemented in Flutter");
                }
            });
        } else {
            Log.e("MyFlutterActivity", "MethodChannel is not initialized");
        }
    }

    public static void storeDataInFlutter() {
        if (methodChannel != null) {
            methodChannel.invokeMethod("storeDataInFlutter", null, new MethodChannel.Result() {
                @Override
                public void success(Object result) {
                    System.out.println("MyFlutterActivity"+ "-------------------------- " + result);
                }

                @Override
                public void error(String errorCode, String errorMessage, Object errorDetails) {
                    Log.e("MyFlutterActivity", "Error fetching users: " + errorMessage);
                }

                @Override
                public void notImplemented() {
                    Log.e("MyFlutterActivity", "getAllUsers method not implemented in Flutter");
                }
            });
        } else {
            Log.e("MyFlutterActivity", "MethodChannel is not initialized");
        }
    }

    public static void sendDataViaApi() {
        if (methodChannel != null) {
            methodChannel.invokeMethod("sendDataViaApi", null, new MethodChannel.Result() {
                @Override
                public void success(Object result) {
                    System.out.println("MyFlutterActivity"+ "-------------------------- " + result);
                }

                @Override
                public void error(String errorCode, String errorMessage, Object errorDetails) {
                    Log.e("MyFlutterActivity", "Error sending data: " + errorMessage);
                }

                @Override
                public void notImplemented() {
                    Log.e("MyFlutterActivity", "sendDataViaApi method not implemented in Flutter");
                }
            });
        } else {
            Log.e("MyFlutterActivity", "MethodChannel is not initialized");
        }
    }
}
