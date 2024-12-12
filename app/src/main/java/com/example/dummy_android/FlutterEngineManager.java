//package com.example.dummy_android;
//
//import android.content.Context;
//
//import io.flutter.embedding.engine.FlutterEngine;
//import io.flutter.embedding.engine.dart.DartExecutor;
//
//public class FlutterEngineManager {
//
//    private static FlutterEngineManager instance;
//    private FlutterEngine flutterEngine;
//
//    private FlutterEngineManager(Context context) {
//        flutterEngine = new FlutterEngine(context.getApplicationContext());
//        flutterEngine.getDartExecutor().executeDartEntrypoint(
//                DartExecutor.DartEntrypoint.createDefault()
//        );
//    }
//
//    public static synchronized FlutterEngineManager getInstance(Context context) {
//        if (instance == null) {
//            instance = new FlutterEngineManager(context);
//        }
//        return instance;
//    }
//
//    public FlutterEngine getFlutterEngine() {
//        return flutterEngine;
//    }
//}
