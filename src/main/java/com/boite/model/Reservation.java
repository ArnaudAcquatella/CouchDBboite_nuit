package com.boite.model;

import com.google.gson.annotations.SerializedName;

public class Reservation {
    @SerializedName("_id")
    private String id;
    @SerializedName("_rev")
    private String rev;
    private String type = "reservation";
    private String clientId;
    private String tableId;
    private String eventId;
    private String date;

    public Reservation(String id, String clientId, String tableId, String eventId, String date) {
        this.id = id;
        this.clientId = clientId;
        this.tableId = tableId;
        this.eventId = eventId;
        this.date = date;
    }

    public String get_id() { return id; }
    public void set_id(String id) { this.id = id; }

    public String getType() { return type; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getTableId() { return tableId; }
    public void setTableId(String tableId) { this.tableId = tableId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String get_rev() { return rev; }
    public void set_rev(String rev) { this.rev = rev; }
    
}
