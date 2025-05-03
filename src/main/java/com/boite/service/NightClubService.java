package com.boite.service;

import com.boite.model.Client;
import com.boite.model.Order;
import com.boite.model.Reservation;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.views.Key;
import com.cloudant.client.api.views.ViewRequestBuilder;
import com.cloudant.client.api.views.ViewResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NightClubService {

    private final Database db;

    public NightClubService(Database db) {
        this.db = db;
    }

    // Méthode simples : on utilise Mango

    // Méthode pour trouver les clients avec un casier
    public List<Client> findClientsWithLockers() {
        String selector = "{ \"type\": \"client\", \"lockerId\": { \"$exists\": true, \"$ne\": null } }";
        return db.findByIndex(selector, Client.class);
    }

    // Méthode pour trouver les réservations d'aujourd'hui
    public List<Reservation> getReservationsByDate(String dateString) {
        String selector = String.format("{ \"type\": \"reservation\", \"date\": \"%s\" }", dateString);
        return db.findByIndex(selector, Reservation.class);
    }

    // Méthode pour obtenir les réservations d'un client spécifique
    public List<Reservation> getReservationsForClient(String clientId) throws IOException {
        String selector = String.format("{ \"type\": \"reservation\", \"clientId\": \"%s\" }", clientId);
        return db.findByIndex(selector, Reservation.class);
    }
    
    public List<Order> getOrdersForClient(String clientId) {
        String selector = String.format("{ \"type\": \"order\", \"clientId\": \"%s\" }", clientId);
        return db.findByIndex(selector, Order.class);
    }




    // Méthodes complexes : on utilise MapReduce et les Views

    // Méthode pour obtenir le nombre de commandes par boisson avec MapReduce (Aggregation)
    public Map<String, Integer> getOrdersCountByDrink() throws IOException {    
        // Accéder à la vue _design/order_views/_view/top_drinks
        ViewRequestBuilder builder = db.getViewRequestBuilder("order_views", "top_drinks");
    
        ViewResponse<String, Integer> response = builder
                .newRequest(Key.Type.STRING, Integer.class)
                .group(true) // pour agréger les résultats par clé (drinkId)
                .build()
                .getResponse();
    
        // Convertir la réponse en map drinkId → nombre de commandes
        return response.getRows().stream()
                .collect(Collectors.toMap(ViewResponse.Row::getKey, ViewResponse.Row::getValue));
    }

    // Méthode pour obtenir le nombre de réservations par événement (groupBy)
    public Map<String, Integer> getReservationsPerEvent() throws IOException {
        ViewRequestBuilder builder = db.getViewRequestBuilder("reservation_views", "per_event");
    
        ViewResponse<String, Integer> response = builder
                .newRequest(Key.Type.STRING, Integer.class)
                .group(true)
                .build()
                .getResponse();
    
        return response.getRows().stream()
                .collect(Collectors.toMap(ViewResponse.Row::getKey, ViewResponse.Row::getValue));
    }
    

    
} 

