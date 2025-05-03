package com.boite.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Order {
    @SerializedName("_id")
    private String id;
    @SerializedName("_rev")
    private String rev;
    private String type = "order";
    private String clientId;
    private String employeeId;
    private List<String> drinksId;
    private String date;


    public Order(String id, String clientId, String employeeId, List<String> drinksId, String date) {
        this.id = id;
        this.clientId = clientId;
        this.employeeId = employeeId;
        this.drinksId = drinksId;
        this.date = date;
    }

    public String get_id() { return id; }
    public void set_id(String id) { this.id = id; }
    public String getType() { return type; }
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public List<String> getDrinks() { return drinksId; }
    public void setDrinks(List<String> drinksId) { this.drinksId = drinksId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String get_rev() { return rev; }
    public void set_rev(String rev) { this.rev = rev; }
}
