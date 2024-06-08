package com.example.carrentalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CustomerScreen extends AppCompatActivity {

    private Button btnCustomerLogin;
    private Button btnCustomerSignUp;
    private EditText edtTxtID;
    private EditText edtTxtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_screen);
        setupViews();
        GoToSignUpScreen();

        btnCustomerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginCustomer();
            }
        });
    }

    private void setupViews() {
        btnCustomerLogin = findViewById(R.id.btnCustomerLogin);
        btnCustomerSignUp = findViewById(R.id.btncustomerSignUp);
        edtTxtID = findViewById(R.id.edtTxtID);
        edtTxtPassword = findViewById(R.id.edtTxtPassword);
    }


    public void GoToSignUpScreen(){
        btnCustomerSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerScreen.this, CustomerSignUp.class);
                startActivity(intent);
            }
        });
    }


    public  void loginCustomer(){
        final String phoneNumber = edtTxtID.getText().toString().trim();
        final String password = edtTxtPassword.getText().toString().trim();

        // Check if the phone number or password fields are empty
        if (phoneNumber.isEmpty() || password.isEmpty()) {
            Toast.makeText(CustomerScreen.this, "Please enter both phone number and password", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.1.3:80/CarRental/CustomerLogin.php";

        Log.d("CustomerLogin", "URL: " + url);  // Log the URL for debugging

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("CustomerLogin", "Response: " + response);  // Log the response for debugging
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("success")) {
                        // Login successful, proceed to the next activity
                        Intent intent = new Intent(CustomerScreen.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Finish current activity to prevent user from coming back with back button
                    } else {
                        // Display error message
                        Toast.makeText(CustomerScreen.this, message, Toast.LENGTH_SHORT).show();
                        if (message.contains("Please sign up")) {
                            // Navigate to sign up activity if user needs to sign up
                            Intent intent = new Intent(CustomerScreen.this, CustomerSignUp.class);
                            startActivity(intent);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CustomerScreen.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(CustomerScreen.this, "", Toast.LENGTH_SHORT).show();
            Toast.makeText(CustomerScreen.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("phoneNumber", phoneNumber);
                params.put("password", password);
                return params;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(CustomerScreen.this);
        requestQueue.add(stringRequest);

    }



}