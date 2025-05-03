package com.boite;

import com.cloudant.client.api.Database;
import com.boite.db.CouchDBClient;
import com.boite.db.DesignDocBuilder;
import com.boite.model.Client;
import com.boite.model.Order;
import com.boite.model.Reservation;
import com.boite.service.NightClubService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            // Crée une instance de CouchDBClient
            CouchDBClient couchDBClient = new CouchDBClient();
            // Recupère une base de données
            Database db = couchDBClient.getDb();
            // Crée les vues nécessaires
            DesignDocBuilder.setupDesignDocs(db); 
            // Crée une instance de NightClubService
            NightClubService service = new NightClubService(db);


            System.out.println("\nClients avec casier :");
            List<Client> clients = service.findClientsWithLockers();
            clients.forEach(c -> System.out.println("- " + c.getName() + " (casier: " + c.getLockerId() + ")"));

            System.out.println("\nReservations par date :");
            List<Reservation> reservationsByDate = service.getReservationsByDate("2023/10/01");
            reservationsByDate.forEach(r -> System.out.println("- Reservation ID: " + r.get_id() + " (client: " + r.getClientId() + ")"));

            System.out.println("\nTop boissons commandees (MapReduce) :");
            Map<String, Integer> drinks = service.getOrdersCountByDrink();
            drinks.forEach((id, count) -> System.out.println("- Drink ID: " + id + ", Quantite: " + count));

            System.out.println("\nReservations par événement :");
            Map<String, Integer> perEvent = service.getReservationsPerEvent();
            perEvent.forEach((eventId, count) -> System.out.println("- Evenement ID: " + eventId + ", Réservations: " + count));

            System.out.println("\nReservations pour un client :");
            List<Reservation> reservationsForClient = service.getReservationsForClient("client2");
            reservationsForClient.forEach(r -> System.out.println("- Reservation ID: " + r.get_id() + ", Evenement: " + r.getEventId() + ", Date: " + r.getDate()));

            List<Order> orders = service.getOrdersForClient("client1");
            System.out.println("\nCommandes du client client1  :");
            for (Order order : orders) {
                System.out.println("- Order ID: " + order.get_id() + ", Date: " + order.getDate() + ", Employe: " + order.getEmployeeId());
                System.out.println("  Boissons commandees : " + order.getDrinks());
            }


        } catch (IOException e) {
            System.err.println("Erreur d'accès aux vues CouchDB : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erreur générale : " + e.getMessage());
            e.printStackTrace();
        }
    }
} 
