package com.boite.CRUD;

import com.boite.model.Employee;
import com.cloudant.client.api.Database;

import java.util.List;

// Methodes CRUD pour la classe Employee
public class EmployeeRepository {

    private final Database db;

    public EmployeeRepository(Database db) {
        this.db = db;
    }

    public void createEmployee(Employee employee) {
        db.save(employee);
    }

    public void createManyEmployee(List<Employee> employee) {
        db.bulk(employee); 
    }

    public Employee readEmployee(String id) {
        return db.find(Employee.class, id);
    }

    public List<Employee> readManyEmployee(List<String> ids) {
        return db.findByIndex(
                String.format("{ \"type\": \"employee\", \"_id\": { \"$in\": %s } }", toJsonArray(ids)),
                Employee.class);
    }

    public void updateEmployee(Employee employee) {
        db.update(employee);
    }

    public void updateManyEmployee(List<Employee> employee) {
        db.bulk(employee);
    }

    public void deleteEmployee(Employee employee) {
        db.remove(employee);
    }

    public void deleteManyEmployee(List<Employee> employee) {
        employee.forEach(c -> c.set_rev(db.find(Employee.class, c.get_id()).get_rev()));
        employee.forEach(c -> db.remove(c));
    }

    public List<Employee> findAllEmployee() {
        String selector = "{ \"type\": \"employee\" }";
        return db.findByIndex(selector, Employee.class);
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
