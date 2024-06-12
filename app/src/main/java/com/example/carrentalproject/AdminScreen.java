package com.example.carrentalproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class AdminScreen extends AppCompatActivity {

    private Button btnAdminLogin;
    private Button btnAdminSignUp;
    private EditText edtTxtUsername;
    private EditText edtTxtPassword;
    private ImageButton homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_screen);
        setupViews();
        goToSignUpScreen();

        btnAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminLogin();
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminScreen.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupViews() {
        btnAdminLogin = findViewById(R.id.btnCustomerLogin);
        btnAdminSignUp = findViewById(R.id.btncustomerSignUp);
        edtTxtUsername = findViewById(R.id.edtTxtUsername);
        edtTxtPassword = findViewById(R.id.edtTxtPassword);
        homeBtn = findViewById(R.id.homeBtn);
    }

    public void goToSignUpScreen() {
        btnAdminSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminScreen.this, SignUpAdminScreen.class);
                startActivity(intent);
            }
        });
    }

    private void adminLogin() {
        final String username = edtTxtUsername.getText().toString().trim();
        final String password = edtTxtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(AdminScreen.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.1.3:80/CarRental/AdminLogin.php";

        Log.d("AdminLogin", "URL: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("AdminLogin", "Response: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        saveUsername(username);

                        Intent intent = new Intent(AdminScreen.this, HomeActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(AdminScreen.this, message, Toast.LENGTH_SHORT).show();
                        message.contains("Username or password is incorrect,please try again or Sign up");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AdminScreen.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(AdminScreen.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void saveUsername(String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.apply();
    }
}

