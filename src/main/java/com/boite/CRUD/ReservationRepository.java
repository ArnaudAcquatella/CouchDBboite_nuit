package com.boite.CRUD;

import java.util.List;

import com.boite.model.Reservation;
import com.cloudant.client.api.Database;

// Methodes CRUD pour la classe Reservation
public class ReservationRepository {
    
    private final Database db;

    public ReservationRepository(Database db) {
        this.db = db;
    }

    public void createReservation(Reservation reservation) {
        db.save(reservation);
    }

    public void createManyReservation(List<Reservation> reservation) {
        db.bulk(reservation); 
    }

    public Reservation readReservation(String id) {
        return db.find(Reservation.class, id);
    }

    public List<Reservation> readManyReservation(List<String> ids) {
        return db.findByIndex(
                String.format("{ \"type\": \"reservation\", \"_id\": { \"$in\": %s } }", toJsonArray(ids)),
                Reservation.class);
    }

    public void updateReservation(Reservation reservation) {
        db.update(reservation);
    }

    public void updateManyReservation(List<Reservation> reservation) {
        db.bulk(reservation);
    }

    public void deleteReservation(Reservation reservation) {
        db.remove(reservation);
    }

    public void deleteManyReservation(List<Reservation> reservation) {
        reservation.forEach(c -> c.set_rev(db.find(Reservation.class, c.get_id()).get_rev()));
        reservation.forEach(c -> db.remove(c));
    }

    public List<Reservation> findAllReservation() {
        String selector = "{ \"type\": \"reservation\" }";
        return db.findByIndex(selector, Reservation.class);
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
    
