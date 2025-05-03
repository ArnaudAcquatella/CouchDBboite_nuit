package com.boite.db;

import com.cloudant.client.api.Database;

import java.util.HashMap;
import java.util.Map;

//créer les design docs pour les vues de la base de données
// ces vues sont utilisées pour les requêtes sur les documents de type client, commande, réservation...

public class DesignDocBuilder {

    public static void setupDesignDocs(Database db) {
        createClientViews(db);
        createOrderViews(db);
        createReservationViews(db);
    }

    private static void createClientViews(Database db) {
        Map<String, Object> mapFunction = new HashMap<>();
        mapFunction.put("map", "function(doc) { if (doc.type === 'client' && doc.lockerId) emit(doc._id, null); }");

        Map<String, Object> views = new HashMap<>();
        views.put("clients_with_locker", mapFunction);

        Map<String, Object> ddoc = new HashMap<>();
        ddoc.put("_id", "_design/client_views");
        ddoc.put("views", views);

        saveDesignDoc(db, ddoc);
    }

    public static void createOrderViews(Database db) {
        Map<String, Object> views = new HashMap<>();

        Map<String, Object> topDrinks = new HashMap<>();
        topDrinks.put("map", "function(doc) { if (doc.type === 'order' && doc.drinksId) { doc.drinksId.forEach(function(d) { emit(d, 1); }); } }");
        topDrinks.put("reduce", "_sum");
        views.put("top_drinks", topDrinks);

        Map<String, Object> totalOrders = new HashMap<>();
        totalOrders.put("map", "function(doc) { if (doc.type === 'order' && doc.date) emit(doc.date, 1); }");
        totalOrders.put("reduce", "_sum");
        views.put("total_orders_by_date", totalOrders);

        Map<String, Object> ddoc = new HashMap<>();
        ddoc.put("_id", "_design/order_views");
        ddoc.put("views", views);

        saveDesignDoc(db, ddoc);
    }

    private static void createReservationViews(Database db) {
        Map<String, Object> views = new HashMap<>();

        Map<String, Object> todayRes = new HashMap<>();
        todayRes.put("map", "function(doc) { if (doc.type === 'reservation' && doc.date) { var date = new Date(doc.date); var today = new Date(); if (date.toDateString() === today.toDateString()) emit(doc._id, null); } }");
        views.put("reservations_today", todayRes);

        Map<String, Object> resPerEvent = new HashMap<>();
        resPerEvent.put("map", "function(doc) { if (doc.type === 'reservation' && doc.eventId) emit(doc.eventId, 1); }");
        resPerEvent.put("reduce", "_sum");
        views.put("reservations_per_event", resPerEvent);

        Map<String, Object> ddoc = new HashMap<>();
        ddoc.put("_id", "_design/reservation_views");
        ddoc.put("views", views);

        saveDesignDoc(db, ddoc);
    }

    private static void saveDesignDoc(Database db, Map<String, Object> ddoc) {
        try {
            db.save(ddoc);
        } catch (Exception e) {
            System.out.println("Design doc already exists or error: " + e.getMessage());
        }
    }
} 
