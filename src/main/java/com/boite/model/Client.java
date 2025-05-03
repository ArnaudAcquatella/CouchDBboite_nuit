package com.boite.model;

import com.google.gson.annotations.SerializedName;

public class Client {
    @SerializedName("_id")
    private String id;
    @SerializedName("_rev")
    private String rev;
    private String type = "client";
    private String name;
    private int age;
    private String phone;
    private String lockerId;


    public Client(String id, String name, int age, String phone) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.phone = phone;
    }

    public String get_id() { return id; }
    public void set_id(String id) { this.id = id; }

    public String getType() { return type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLockerId() { return lockerId; }
    public void setLockerId(String lockerId) { this.lockerId = lockerId; }
    
    public String get_rev() { return rev; }
    public void set_rev(String rev) { this.rev = rev; }
}
