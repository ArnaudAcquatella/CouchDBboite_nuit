package com.boite.model;

import com.google.gson.annotations.SerializedName;

public class Employee {
    @SerializedName("_id")
    private String id;
    @SerializedName("_rev")
    private String rev;
    private String type = "employee";
    private String name;
    private double salary;
    private String role;

    public Employee(String id, String name, double salary, String role) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.role = role;
    }

    public String get_id() { return id; }
    public void set_id(String id) { this.id = id; }

    public String getType() { return type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getSalary() { return salary; }
    public void setSalary(int salary) { this.salary = salary; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String get_rev() { return rev; }
    public void set_rev(String rev) { this.rev = rev; }
    
    
}
