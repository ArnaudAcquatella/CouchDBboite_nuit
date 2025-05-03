package com.boite.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Event {
    @SerializedName("_id")
    private String id;
    @SerializedName("_rev")
    private String rev;
    private String type = "event";
    private String name;

    public Event(String id, String name, List<String> reservationId) {
        this.id = id;
        this.name = name;    }
    
    public String get_id() { return id; }
    public void set_id(String id) { this.id = id; }

    public String getType() { return type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String get_rev() { return rev; }
    public void set_rev(String rev) { this.rev = rev; }
    
}
