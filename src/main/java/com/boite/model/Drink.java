package com.boite.model;

import com.google.gson.annotations.SerializedName;

public class Drink {
    @SerializedName("_id")
    private String id;
    @SerializedName("_rev")
    private String rev;
    private String type = "drink";
    private String name;
    private String category;
    private int price;

    public Drink(String id, String name, String category, int price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    public String get_id() { return id; }
    public void set_id(String id) { this.id = id; }

    public String getType() { return type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String get_rev() { return rev; }
    public void set_rev(String rev) { this.rev = rev; }
}