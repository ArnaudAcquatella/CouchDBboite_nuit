package com.boite.model;

import com.google.gson.annotations.SerializedName;

public class Locker {
    @SerializedName("_id")
    private String id;
    @SerializedName("_rev")
    private String rev;
    private String type = "locker";
    private String size;

    public Locker(String id, String size) {
        this.id = id;
        this.size = size;
        }

    public String get_id() { return id; }
    public void set_id(String id) { this.id = id; }

    public String getType() { return type; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    
    public String get_rev() { return rev; }
    public void set_rev(String rev) { this.rev = rev; }
}

