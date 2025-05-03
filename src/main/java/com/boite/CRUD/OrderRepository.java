package com.boite.CRUD;

import com.boite.model.Order;
import com.cloudant.client.api.Database;

import java.util.List;

// Methodes CRUD pour la classe Order
public class OrderRepository {

    private final Database db;

    public OrderRepository(Database db) {
        this.db = db;
    }

    public void createOrder(Order order) {
        db.save(order);
    }

    public void createManyOrder(List<Order> order) {
        db.bulk(order); 
    }

    public Order readOrder(String id) {
        return db.find(Order.class, id);
    }

    public List<Order> readManyOrder(List<String> ids) {
        return db.findByIndex(
                String.format("{ \"type\": \"order\", \"_id\": { \"$in\": %s } }", toJsonArray(ids)),
                Order.class);
    }

    public void updateOrder(Order order) {
        db.update(order);
    }

    public void updateManyOrder(List<Order> order) {
        db.bulk(order);
    }

    public void deleteOrder(Order order) {
        db.remove(order);
    }

    public void deleteManyOrder(List<Order> order) {
        order.forEach(c -> c.set_rev(db.find(Order.class, c.get_id()).get_rev()));
        order.forEach(c -> db.remove(c));
    }

    public List<Order> findAllOrder() {
        String selector = "{ \"type\": \"order\" }";
        return db.findByIndex(selector, Order.class);
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