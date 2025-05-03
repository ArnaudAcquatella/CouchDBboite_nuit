package com.boite.CRUD;

import com.boite.model.Event;
import com.cloudant.client.api.Database;

import java.util.List;

// Methodes CRUD pour la classe Event
public class EventRepository {

    private final Database db;

    public EventRepository(Database db) {
        this.db = db;
    }

    public void createEvent(Event event) {
        db.save(event);
    }

    public void createManyEvent(List<Event> event) {
        db.bulk(event); 
    }

    public Event readEvent(String id) {
        return db.find(Event.class, id);
    }

    public List<Event> readManyEvent(List<String> ids) {
        return db.findByIndex(
                String.format("{ \"type\": \"event\", \"_id\": { \"$in\": %s } }", toJsonArray(ids)),
                Event.class);
    }

    public void updateEvent(Event event) {
        db.update(event);
    }

    public void updateManyEvent(List<Event> event) {
        db.bulk(event);
    }

    public void deleteEvent(Event event) {
        db.remove(event);
    }

    public void deleteManyEvent(List<Event> event) {
        event.forEach(c -> c.set_rev(db.find(Event.class, c.get_id()).get_rev()));
        event.forEach(c -> db.remove(c));
    }

    public List<Event> findAllEvent() {
        String selector = "{ \"type\": \"event\" }";
        return db.findByIndex(selector, Event.class);
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
    
