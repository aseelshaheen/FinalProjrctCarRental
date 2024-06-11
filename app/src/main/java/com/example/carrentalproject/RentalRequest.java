package com.example.carrentalproject;


import org.json.JSONException;
import org.json.JSONObject;

public class RentalRequest {
    private int rentalID, userID, carID;
    private String startDate, endDate, status;
    private double totalPrice;

    public RentalRequest(JSONObject obj) throws JSONException {
        this.rentalID = obj.getInt("rentalID");
        this.userID = obj.getInt("idNumber");
        this.carID = obj.getInt("carID");
        this.startDate = obj.getString("startDate");
        this.endDate = obj.getString("endDate");
        this.totalPrice = obj.getDouble("totalPrice");
        this.status = obj.getString("status");
    }

    public RentalRequest(int rentalID, int idNumber, int carID, String startDate, String endDate, double totalPrice, String status) {
        this.rentalID = rentalID;
        this.userID = idNumber;
        this.carID = carID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // Getters
    public int getRentalID() { return rentalID; }
    public int getIdNumber() { return userID; }
    public int getCarID() { return carID; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return rentalID + "  " + userID + "  " + startDate + "  " + endDate;
    }
}
