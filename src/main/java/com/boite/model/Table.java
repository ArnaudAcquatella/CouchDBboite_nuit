package com.boite.model;

import com.google.gson.annotations.SerializedName;

public class Table {
    @SerializedName("_id")
    private String id;
    @SerializedName("_rev")
    private String rev;
    private String type = "table";
    private int capacity;
    
    public Table(String id, int capacity) {
        this.id = id;
        this.capacity = capacity;
    }

    public String get_id() { return id; }
    public void set_id(String id) { this.id = id; }

    public String getType() { return type; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String get_rev() { return rev; }
    public void set_rev(String rev) { this.rev = rev; }
    
}
