package com.example.dummy_android;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.p2p.P2PLibrary;
import org.smartregister.p2p.activity.P2pModeSelectActivity;
import org.smartregister.p2p.authorizer.P2PAuthorizationService;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends AppCompatActivity implements P2PAuthorizationService {

    private static final int REQUEST_CODE_GET_JSON = 1;
    private static final int REQUEST_CODE_FLUTTER_ACTIVITY = 2;
    private static final String CHANNEL = "com.example.channel";
    private FlutterEngine flutterEngine;
    private MethodChannel methodChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        P2PLibrary.init(new P2PLibrary.Options(this
//                , "db_password_here"
//                , "John Doe"
//                , new MyP2PAuthorizationService()
//                , new MyReceiverDao()
//                , new MySenderDao()));
        P2PLibrary.Options options = new P2PLibrary.Options(this
                , "12345678"
                , "12345678"
                , this, new MyReceiverDao(), new MySenderDao());

        options.setBatchSize(100);
        options.setRecalledIdentifier(new FailSafeRecalledID());
        P2PLibrary.init(options);

        flutterEngine = new FlutterEngine(this);
        flutterEngine.getDartExecutor().executeDartEntrypoint(
                io.flutter.embedding.engine.dart.DartExecutor.DartEntrypoint.createDefault()
        );
        methodChannel = new MethodChannel(flutterEngine.getDartExecutor(), CHANNEL);

        Button btnOpenOpenSRPForm = findViewById(R.id.btn_open_opensrp_form);
        Button btnOpenFlutterActivity = findViewById(R.id.btn_open_flutter_activity);
        Button btnStoreData = findViewById(R.id.btn_store_data);

        Button sendDataViaApi = findViewById(R.id.send_data_via_api);

        Button btnGetData = findViewById(R.id.btn_get_data);

        Button btnP2PdataTransfer = findViewById(R.id.p2p_data_transfer);

        btnOpenOpenSRPForm.setOnClickListener(v -> {
            String formJson = getAutoPopulatedJsonForm();
            if (formJson != null) {
                try {
                    JSONObject jsonObject = new JSONObject(formJson);
                    Log.d("MainActivity", "Valid JSON loaded: " + jsonObject.toString());

                    startJsonFormActivity(formJson);
                } catch (JSONException e) {
                    Log.e("MainActivity", "Invalid JSON structure", e);
                }
            } else {
                Log.e("MainActivity", "Failed to load the JSON form");
            }
        });

        btnOpenFlutterActivity.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyFlutterActivity.class);
            intent.putExtra("name", "John Doe");
            intent.putExtra("age", "30");
            startActivityForResult(intent, REQUEST_CODE_FLUTTER_ACTIVITY);
        });

        btnStoreData.setOnClickListener(v -> {
            Log.d("MainActivity", "Store Data button clicked");
            storeDataInFlutter();
        });

        btnGetData.setOnClickListener(v -> {
            Log.d("MainActivity", "Get Data button clicked");
            getAllUsersFromFlutter();
        });
        sendDataViaApi.setOnClickListener(v -> {
            Log.d("MainActivity", "Send Data via api button clicked");
            sendDataViaApi();
        });
        btnP2PdataTransfer.setOnClickListener(v -> {
            Log.d("MainActivity", "p2p data transfer button clicked");
            startActivity(new Intent(this, P2pModeSelectActivity.class));
        });
    }
    private void storeDataInFlutter() {
        methodChannel.invokeMethod("storeDataInFlutter", null, new MethodChannel.Result() {
            @Override
            public void success(Object result) {
                Log.d("MainActivity", "Data stored successfully in Flutter DB.");
            }

            @Override
            public void error(String errorCode, String errorMessage, Object errorDetails) {
                Log.e("MainActivity", "Error storing data: " + errorMessage);
            }

            @Override
            public void notImplemented() {
                Log.e("MainActivity", "storeDataInFlutter method not implemented in Flutter");
            }
        });
    }

    @Override
    public void authorizeConnection(@NonNull Map<String, Object> authorizationDetails, @NonNull AuthorizationCallback authorizationCallback) {
        Object appVersion = authorizationDetails.get("app-version");
        Object appType = authorizationDetails.get("app-type");

        if (appVersion != null && appVersion instanceof Double && ((double) appVersion) >= 9d
                && appType != null && appType instanceof String && appType.equals("normal-user")) {
            authorizationCallback.onConnectionAuthorized();
        } else {
            authorizationCallback.onConnectionAuthorizationRejected("App version or app type is incorrect");
        }
    }

    @Override
    public void getAuthorizationDetails(@NonNull OnAuthorizationDetailsProvidedCallback onAuthorizationDetailsProvidedCallback) {
        HashMap<String, Object> authorizationDetails = new HashMap<>();
        authorizationDetails.put("app-version", 9);
        authorizationDetails.put("app-type", "normal-user");

        onAuthorizationDetailsProvidedCallback.onAuthorizationDetailsProvided(authorizationDetails);
    }

    private void getAllUsersFromFlutter() {
        methodChannel.invokeMethod("getAllUsers", null, new MethodChannel.Result() {
            @Override
            public void success(Object result) {
                Log.d("MainActivity", "Users fetched successfully: " + result);
            }

            @Override
            public void error(String errorCode, String errorMessage, Object errorDetails) {
                Log.e("MainActivity", "Error fetching users: " + errorMessage);
            }

            @Override
            public void notImplemented() {
                Log.e("MainActivity", "getAllUsers method not implemented in Flutter");
            }
        });
    }

    private void sendDataViaApi() {
        methodChannel.invokeMethod("sendDataViaApi", null, new MethodChannel.Result() {
            @Override
            public void success(Object result) {
                Log.d("MainActivity", "Data sent successfully via API.");
            }

            @Override
            public void error(String errorCode, String errorMessage, Object errorDetails) {
                Log.e("MainActivity", "Error sending data via API: " + errorMessage);
            }

            @Override
            public void notImplemented() {
                Log.e("MainActivity", "sendDataViaApi method not implemented in Flutter");
            }
        });
    }

    // Loads and returns the JSON form string from the assets folder.
    //return The JSON form as a String
    private String getAutoPopulatedJsonForm() {
        try {
            InputStream inputStream = getAssets().open("forms/sample_form.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer, "UTF-8");
        } catch (Exception e) {
            Log.e("MainActivity", "Error loading JSON form", e);
            return null;
        }
    }


    //Starts the JsonFormActivity with the provided JSON form.
    // param formJson The JSON form string.

    private void startJsonFormActivity(String formJson) {
        try {
            Intent intent = new Intent(this, JsonFormActivity.class);
            intent.putExtra("json", formJson);
            startActivityForResult(intent, REQUEST_CODE_GET_JSON);
        } catch (Exception e) {
            Log.e("MainActivity", "Error starting JsonFormActivity", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GET_JSON && resultCode == RESULT_OK && data != null) {
            String jsonString = data.getStringExtra("json");
            if (jsonString != null) {
                Log.d("Form Result", "Form JSON Result: " + jsonString);
            } else {
                Log.e("MainActivity", "No JSON result returned");
            }
        } else if (requestCode == REQUEST_CODE_FLUTTER_ACTIVITY && resultCode == RESULT_OK && data != null) {
//            String address = data.getStringExtra("address");
            String name = data.getStringExtra("firstName");
            String age = data.getStringExtra("age");
            if (name != null || age != null) {
                Log.d("Flutter Result", "name, age from Flutter: " + name + ',' + age);
                insertUserIntoDatabase(name, age);

            } else {
                Log.e("MainActivity", "No address returned from Flutter activity");
            }
        }
    }

    private void insertUserIntoDatabase(String name, String age) {
        try {
            UserRepository userRepository = new UserRepository(this);
            SQLiteDatabase db = userRepository.getWritableDatabase();
            userRepository.onCreate(db);

            userRepository.addUser(name, age);
            Log.d("Database", "User inserted into table");
        } catch (Exception e) {
            Log.e("Database", "Error inserting user into database", e);
        }
    }

}
