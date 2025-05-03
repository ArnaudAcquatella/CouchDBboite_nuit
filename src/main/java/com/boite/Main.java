package com.boite;

import com.cloudant.client.api.Database;
import com.boite.CRUD.ClientRepository;
import com.boite.CRUD.OrderRepository;
import com.boite.db.CouchDBConnect;
import com.boite.db.DesignDocBuilder;
import com.boite.model.Client;
import com.boite.model.Employee;
import com.boite.model.Order;
import com.boite.model.Reservation;
import com.boite.service.NightClubService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            // Crée une instance de CouchDBClient
            CouchDBConnect couchDBConnect = new CouchDBConnect();
            // Recupère une base de données
            Database db = couchDBConnect.getDb();
            // Crée les vues nécessaires
            DesignDocBuilder.createAllDesignDocs(db); 
            // Crée une instance de NightClubService
            NightClubService service = new NightClubService(db);
            ClientRepository repoClient = new ClientRepository(db);
            OrderRepository repoOrder = new OrderRepository(db);


            // ################# Tests Méthodes CRUD #####################

            System.out.println("\nTests CRUD :");

            // === Create One ===
            Client c1 = new Client("client-101", "Alice", 28, "0600000001");
            repoClient.createClient(c1);
            System.out.println("\nCree : " + c1.getName());

            // === Read One ===
            Client client = repoClient.readClient("client-101");
            System.out.println("\nLu : " + client.getName());

            // === Update One ===
            client.setPhone("0600000002");
            repoClient.updateClient(client);
            System.out.println("\nMis a jour : " + client.getPhone());

            // == Delete One ===
            Client updatedClient = repoClient.readClient("client-101");  // Recharger le client, obligatoire pour la suppression avec couchDB a cause de la gestion des versions (_rev)
            repoClient.deleteClient(updatedClient);
            System.out.println("\nSupprimé : " + client.get_id());


            // === Create Many ===
            System.out.println("\nCreation de plusieurs order :");
            Order o1 = new Order("order-101", "client-101", "employee-101", Arrays.asList("drink-101", "drink-102"), "2023/10/01");
            Order o2 = new Order("order-102", "client-102", "employee-102", Arrays.asList("drink-103"), "2023/10/02");
            Order o3 = new Order("order-103", "client-103", "employee-103", Arrays.asList("drink-104"), "2023/10/03");

            repoOrder.createManyOrder(Arrays.asList(o1, o2, o3));
            System.out.println("\nCommandes créées :");
            System.out.println("- " + o1.get_id());
            System.out.println("- " + o2.get_id());
            System.out.println("- " + o3.get_id());

            // === Read Many ===
            System.out.println("\nLecture de plusieurs commandes :");
            List<Order> orders = repoOrder.readManyOrder(Arrays.asList("order-101", "order-102", "order-103"));
            orders.forEach(o -> System.out.println("- " + o.get_id()));

            // === Update Many ===
            System.out.println("\nMise à jour de plusieurs commandes :");
            orders.forEach(o -> {
                o.setDate("2023/10/04");
                repoOrder.updateManyOrder(orders);
                System.out.println("- Mise à jour de la commande : " + o.get_id());
            });

            // === Delete Many ===
            System.out.println("\nSuppression de plusieurs commandes :");
            repoOrder.deleteManyOrder(orders);
            System.out.println("- Commandes supprimées :");
            orders.forEach(o -> System.out.println("- " + o.get_id()));
            

            System.out.println("\nFin Tests CRUD -----------------------------");


            // ################# Tests Méthodes Avancées #####################

            System.out.println("\nTests Méthodes Avancées :");

            // === Trouve les clients avec un casier ===
            System.out.println("\nClients avec casier :");
            List<Client> clients = service.findClientsWithLockers();
            clients.forEach(c -> System.out.println("- " + c.getName() + " (casier: " + c.getLockerId() + ")"));

            // === Trouve les réservations par date ===
            System.out.println("\nReservations a la date du 2025/05/03:");
            List<Reservation> reservationsByDate = service.getReservationsByDate("2025/05/03");
            reservationsByDate.forEach(r -> System.out.println("- Reservation ID: " + r.get_id() + " (client: " + r.getClientId() + ")"));

            // === Trouve le nombre de boissons commandées ===
            System.out.println("\nNombre de boissons commandees (MapReduce) :");
            Map<String, Integer> drinks = service.getOrdersByDrink();
            drinks.forEach((id, count) -> System.out.println("- Drink : " + id + ", Quantite: " + count));

            // === Trouve le nombre de réservations par événement ===
            System.out.println("\nReservations par evenement :");
            Map<String, Integer> perEvent = service.getReservationsByEvent();
            perEvent.forEach((eventId, count) -> System.out.println("- Evenement : " + eventId + ", Réservations: " + count));

            // === Trouve les réservations pour un client spécifique ===
            System.out.println("\nReservations pour un client (Id 677556409):");
            List<Reservation> reservationsForClient = service.getReservationsForClient("677556409");
            reservationsForClient.forEach(r -> System.out.println("- Reservation ID: " + r.get_id() + ", Evenement: " + r.getEventId() + ", Date: " + r.getDate()));

            // === Trouve les commandes pour un client spécifique ===
            System.out.println("\nCommandes du client (Id 587048636) :");
            List<String> commandes = service.getOrdersForClient("587048636");
            commandes.forEach(System.out::println);

            // === Trouve le nombre de commande servies par un employé ===
            String employeeId = "182801953";
            Employee employee = db.find(Employee.class, employeeId);
            System.out.println("\nNombre de commande servie par l'employee " + employeeId + " : ");
            int boissons = service.getOrderCountForEmployee(employeeId);
            System.out.println("- Nombre de commande servies par " + employee.getName() + " : " + boissons);

            // === Trouve le nombre de boisson d'un type spécifique commandées par date ===
            long beerCount = service.countDrinksByCategoryOnDate("2025/05/08", "beer");
            System.out.println("\nNombre de bières commandées le 2025/05/08 : " + beerCount);
        


        } catch (IOException e) {
            System.err.println("Erreur d'accès aux vues CouchDB : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erreur générale : " + e.getMessage());
            e.printStackTrace();
        }
    }
} 
