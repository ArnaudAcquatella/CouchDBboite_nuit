package com.boite.db;

import com.cloudant.client.api.Database;

import java.util.HashMap;
import java.util.Map;

public class DesignDocBuilder {

    // Cette classe permet de créer des documents de design pour la base de données CouchDB.
    // Elle contient des méthodes pour créer des vues pour différents types de documents (clients, commandes, réservations, boissons, tables).

    public static void createAllDesignDocs(Database db) {
        createClientViews(db);
        createOrderViews(db);
        createReservationViews(db);
        createDrinkViews(db);
        createTableViews(db);
    }

    public static void createClientViews(Database db) {
        Map<String, Object> views = new HashMap<>();

        views.put("by_name", mapOnly("function(doc) { if(doc.type === 'client' && doc.name) { emit(doc.name, null); } }"));
        views.put("by_age", mapOnly("function(doc) { if(doc.type === 'client' && doc.age) { emit(doc.age, null); } }"));
        views.put("with_locker", mapOnly("function(doc) { if(doc.type === 'client' && doc.lockerId) { emit(doc._id, doc.lockerId); } }"));

        saveDesignDoc(db, "clients", views);
    }

    public static void createOrderViews(Database db) {
        Map<String, Object> views = new HashMap<>();

        views.put("by_client", mapOnly("function(doc) { if(doc.type === 'order' && doc.clientId) { emit(doc.clientId, null); } }"));
        views.put("by_date", mapOnly("function(doc) { if(doc.type === 'order' && doc.date) { emit(doc.date, null); } }"));

        Map<String, Object> byDrink = new HashMap<>();
        byDrink.put("map", "function(doc) { if(doc.type === 'order' && doc.drinksId) { doc.drinksId.forEach(function(drinkId) { emit(drinkId, 1); }); } }" );
        byDrink.put("reduce", "_sum");
        views.put("by_drink", byDrink);

        Map<String, Object> byEmployee = new HashMap<>();
        byEmployee.put("map", "function(doc) { if(doc.type === 'order' && doc.employeeId) { emit(doc.employeeId, 1); } }");
        byEmployee.put("reduce", "_count");
        views.put("by_employee", byEmployee);

        saveDesignDoc(db, "orders", views);
    }

    public static void createReservationViews(Database db) {
        Map<String, Object> views = new HashMap<>();

        views.put("by_table", mapOnly("function(doc) { if(doc.type === 'reservation' && doc.tableId) { emit(doc.tableId, null); } }"));        
        views.put("by_client", mapOnly("function(doc) { if(doc.type === 'reservation' && doc.clientId) { emit(doc.clientId, null); } }"));
        views.put("by_date", mapOnly("function(doc) { if(doc.type === 'reservation' && doc.date) { emit(doc.date, null); } }"));

        Map<String, Object> byEvent = new HashMap<>();
        byEvent.put("map", "function(doc) { if(doc.type === 'reservation' && doc.eventId) { emit(doc.eventId, null); } }");
        byEvent.put("reduce", "_count");
        views.put("by_event", byEvent);

        saveDesignDoc(db, "reservations", views);
    }

    public static void createDrinkViews(Database db) {
        Map<String, Object> views = new HashMap<>();

        views.put("by_category", mapOnly("function(doc) { if(doc.type === 'drink' && doc.category) { emit(doc.category, null); } }"));
        views.put("by_price", mapOnly("function(doc) { if(doc.type === 'drink' && doc.price) { emit(doc.price, null); } }"));

        saveDesignDoc(db, "drinks", views);
    }

    public static void createTableViews(Database db) {
        Map<String, Object> views = new HashMap<>();

        views.put("by_capacity", mapOnly("function(doc) { if(doc.type === 'table' && doc.capacity) { emit(doc.capacity, null); } }"));

        saveDesignDoc(db, "tables", views);
    }

    private static Map<String, Object> mapOnly(String mapCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("map", mapCode);
        return map;
    }

    // Cette méthode permet de sauvegarder un document de design dans la base de données.
    // Elle vérifie d'abord si le document existe déjà et met à jour la révision si nécessaire.
    private static void saveDesignDoc(Database db, String name, Map<String, Object> views) {
        String designId = "_design/" + name;
        try {
            // On essaie de récupérer le design doc existant pour obtenir le _rev
            Map<String, Object> existing = db.find(Map.class, designId);
            if (existing != null && existing.get("_rev") != null) {
                views = new HashMap<>(views); // copie défensive
                Map<String, Object> ddoc = new HashMap<>();
                ddoc.put("_id", designId);
                ddoc.put("_rev", existing.get("_rev"));
                ddoc.put("views", views);
                db.update(ddoc); // mise à jour propre
                return;
            }
        } catch (Exception ignored) { /* doc non trouvé, on va le créer */ }

        try {
            Map<String, Object> ddoc = new HashMap<>();
            ddoc.put("_id", designId);
            ddoc.put("views", views);
            db.save(ddoc); // création
        } catch (Exception e) {
            System.out.println("Erreur lors de la création du design doc '" + name + "' : " + e.getMessage());
        }
    }
} 
