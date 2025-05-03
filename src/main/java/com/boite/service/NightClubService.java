package com.boite.service;

import com.boite.model.Client;
import com.boite.model.Drink;
import com.boite.model.Employee;
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
    
    // Méthode pour obtenir les commandes d'un client avec le nom de l'employé qui a pris la commande (jointure)
    public List<String> getOrdersForClient(String clientId) {
        // 1. Obtenir les commandes du client
        String selector = String.format("{ \"type\": \"order\", \"clientId\": \"%s\" }", clientId);
        List<Order> orders = db.findByIndex(selector, Order.class);

        // 2. Charger les employés et créer un map id → nom
        List<Employee> employees = db.findByIndex("{ \"type\": \"employee\" }", Employee.class);
        Map<String, String> employeeNames = employees.stream()
                .collect(Collectors.toMap(Employee::get_id, Employee::getName));

        // 3. Charger les boissons et créer un map id → nom
        List<Drink> drinks = db.findByIndex("{ \"type\": \"drink\" }", Drink.class);
        Map<String, String> drinkNames = drinks.stream()
                .collect(Collectors.toMap(Drink::get_id, Drink::getName));

        // 4. Construit les lignes à afficher
        return orders.stream()
                .map(order -> {
                    String empName = employeeNames.getOrDefault(order.getEmployeeId(), "(inconnu)");
                    String drinkList = order.getDrinks().stream()
                            .map(did -> drinkNames.getOrDefault(did, "(boisson inconnue)"))
                            .collect(Collectors.joining(", "));
                    return "- Commande ID: " + order.get_id() +
                            ", Employé qui a servi: " + empName +
                            ", Boissons: [" + drinkList + "]";
                })
                .collect(Collectors.toList());
    }

    public long countDrinksByCategoryOnDate(String date, String category) {
        // 1. Trouver toutes les commandes à la date donnée
        String selector = String.format("{ \"type\": \"order\", \"date\": \"%s\" }", date);
        List<Order> orders = db.findByIndex(selector, Order.class);
    
        // 2. Charger toutes les boissons disponibles
        List<Drink> drinks = db.findByIndex("{ \"type\": \"drink\" }", Drink.class);
        Map<String, String> drinkCategories = drinks.stream()
                .collect(Collectors.toMap(Drink::get_id, Drink::getCategory)); // drink_id → catégorie
    
        // 3. Compter toutes les boissons de la catégorie demandée dans les commandes du jour
        return orders.stream()
                .flatMap(order -> order.getDrinks().stream())
                .filter(drinkId -> category.equalsIgnoreCase(drinkCategories.get(drinkId)))
                .count();
    }
    




    // Méthodes complexes : on utilise MapReduce et les Vues

    // Méthode pour obtenir le nombre de commandes par boisson avec MapReduce (Aggregation)
    public Map<String, Integer> getOrdersByDrink() throws IOException {
        Map<String, Integer> countsByDrinkId = aggregateCountGroup("orders", "by_drink");
    
        // Charger les noms de boissons
        List<com.boite.model.Drink> drinks = db.findByIndex("{ \"type\": \"drink\" }", com.boite.model.Drink.class);
        Map<String, String> drinkNamesById = drinks.stream()
                .collect(Collectors.toMap(com.boite.model.Drink::get_id, com.boite.model.Drink::getName));
    
        // Convertir les drinkId en noms lisibles
        return replaceIdsWithNames(countsByDrinkId, drinkNamesById, "inconnu");
    }

    // Méthode pour obtenir le nombre de réservations par événement (groupBy)
    public Map<String, Integer> getReservationsByEvent() throws IOException {
        // 1. Obtenir l'agrégation par eventId
        Map<String, Integer> countsByEventId = aggregateCountGroup("reservations", "by_event");
    
        // 2. Charger tous les événements pour mapper les noms
        List<com.boite.model.Event> events = db.findByIndex("{ \"type\": \"event\" }", com.boite.model.Event.class);
        Map<String, String> eventNamesById = events.stream()
                .collect(Collectors.toMap(com.boite.model.Event::get_id, com.boite.model.Event::getName));
    
        // 3. Convertir les eventId en noms lisibles
        return replaceIdsWithNames(countsByEventId, eventNamesById, "(inconnu)");
    }

    // Méthode pour obtenir le nombre de commandes servies par un employé
    public int getOrderCountForEmployee(String employeeId) throws IOException {
        ViewRequestBuilder builder = db.getViewRequestBuilder("orders", "by_employee");

        ViewResponse<String, Integer> response = builder
                .newRequest(Key.Type.STRING, Integer.class)
                .keys(employeeId)
                .reduce(true)
                .group(true)
                .build()
                .getResponse();

        return response.getRows().stream()
                .map(ViewResponse.Row::getValue)
                .findFirst()
                .orElse(0);
    }


    // ######## Méthodes Utilitaires ###########

    // Méthode Aggregation par clé 
    public Map<String, Integer> aggregateCountGroup(String designDoc, String viewName) throws IOException {
        ViewRequestBuilder builder = db.getViewRequestBuilder(designDoc, viewName);
    
        ViewResponse<String, Integer> response = builder
                .newRequest(Key.Type.STRING, Integer.class)
                .group(true)
                .build()
                .getResponse();
    
        return response.getRows().stream()
                .collect(Collectors.toMap(ViewResponse.Row::getKey, ViewResponse.Row::getValue));
    }
    
    // Méthode pour remplacer les IDs par des noms 
    public static <T> Map<String, Integer> replaceIdsWithNames(
        Map<String, Integer> idValueMap,
        Map<String, String> idToNameMap,
        String defaultName
) {
    return idValueMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> {
                            String id = entry.getKey();
                            String name = idToNameMap.getOrDefault(id, defaultName);
                            return name + " (" + id + ")";
                        },
                        Map.Entry::getValue
                ));
    }
    
} 

