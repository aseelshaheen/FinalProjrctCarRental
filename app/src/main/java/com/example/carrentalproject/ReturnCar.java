package com.example.carrentalproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

 public class ReturnCar extends AppCompatActivity {
    private SearchView searchView;
    private Button searchButton;
    private RecyclerView recyclerView;
    private ReqDetails adapter;
    private ArrayList<RentalRequest> requestList;
    private RequestQueue requestQueue;
    private ImageButton btVhome, btView, bt_Vrep, btVdelete, btVupdate, btVpay, btReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.return_car);
        btVhome = findViewById(R.id.bt_Vhome);
        bt_Vrep = findViewById(R.id.bt_Vadd);
        btVdelete = findViewById(R.id.bt_Vdelete);
        btVupdate = findViewById(R.id.bt_Vupdate);
        btVpay = findViewById(R.id.bt_Vpay);
        btReturn = findViewById(R.id.bt_return);

        searchView = findViewById(R.id.searchView);
        searchButton = findViewById(R.id.bt_Report);
        recyclerView = findViewById(R.id.recy_rep);
        requestList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReqDetails(requestList, new ReqDetails.OnItemClickListener() {
            @Override
            public void onItemClick(RentalRequest request) {
                changeRequestStatus(request.getRentalID());
            }
        });
        recyclerView.setAdapter(adapter);

        // Load all qualified requests when the activity starts
        loadRequests(null);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = searchView.getQuery().toString().trim();
                if (TextUtils.isEmpty(userID)) {
                    loadRequests(null);
                } else {
                    loadRequests(userID);
                }
            }
        });

        btVhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReturnCar.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btVupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReturnCar.this, UpdateCarActivity.class);
                startActivity(intent);
            }
        });

        btVpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReturnCar.this, PayRequests.class);
                startActivity(intent);
            }
        });

        btReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReturnCar.this, ReturnCar.class);
                startActivity(intent);
            }
        });
    }

    private void loadRequests(String userID) {
        String url = "http://192.168.1.3:80/CarRental/get_toBeReturned_req.php";
        if (userID != null) {
            url += "?idNumber=" + userID;
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        requestList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                RentalRequest request = new RentalRequest(obj);
                                requestList.add(request);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    public void changeRequestStatus(int rentalID) {
        String url = "http://192.168.1.3:80/CarRental/update_req&car_status.php";
        JSONObject postData = new JSONObject();
        try {
            postData.put("rentalID", rentalID);
            postData.put("status", "Returned");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                Toast.makeText(ReturnCar.this, "Status updated to Returned", Toast.LENGTH_SHORT).show();
                                String userID = searchView.getQuery().toString().trim();
                                if (TextUtils.isEmpty(userID)) {
                                    loadRequests(null);
                                } else {
                                    loadRequests(userID);
                                }
                            } else {
                                Toast.makeText(ReturnCar.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}