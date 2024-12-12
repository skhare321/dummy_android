package com.example.dummy_android;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_GET_JSON = 1;
    private static final int REQUEST_CODE_FLUTTER_ACTIVITY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnOpenOpenSRPForm = findViewById(R.id.btn_open_opensrp_form);
        Button btnOpenFlutterActivity = findViewById(R.id.btn_open_flutter_activity);

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
    }

    /**
     * Loads and returns the JSON form string from the assets folder.
     *
     * @return The JSON form as a String.
     */
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

    /**
     * Starts the JsonFormActivity with the provided JSON form.
     *
     * @param formJson The JSON form string.
     */
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
