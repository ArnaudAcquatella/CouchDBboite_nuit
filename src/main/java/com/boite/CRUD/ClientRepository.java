package com.boite.CRUD;

import com.boite.model.Client;
import com.cloudant.client.api.Database;

import java.util.List;

// Methodes CRUD pour la classe Client
public class ClientRepository {

    private final Database db;

    public ClientRepository(Database db) {
        this.db = db;
    }

    public void createClient(Client client) {
        db.save(client);
    }

    public void createManyClient(List<Client> clients) {
        db.bulk(clients); 
    }

    public Client readClient(String id) {
        return db.find(Client.class, id);
    }

    public List<Client> readManyClient(List<String> ids) {
        return db.findByIndex(
                String.format("{ \"type\": \"client\", \"_id\": { \"$in\": %s } }", toJsonArray(ids)),
                Client.class);
    }

    public void updateClient(Client client) {
        db.update(client);
    }
    
    public void updateManyClient(List<Client> clients) {
        db.bulk(clients);
    }

    public void deleteClient(Client client) {
        db.remove(client);
    }

    public void deleteManyClient(List<Client> clients) {
        clients.forEach(c -> c.set_rev(db.find(Client.class, c.get_id()).get_rev()));
        clients.forEach(c -> db.remove(c));
    }

    public List<Client> findAllClient() {
        String selector = "{ \"type\": \"client\" }";
        return db.findByIndex(selector, Client.class);
    }

    private String toJsonArray(List<String> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("\"").append(list.get(i)).append("\"");
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
