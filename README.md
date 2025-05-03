# Projet Maven - Boîte de Nuit avec CouchDB

## 📦 Contenu
Ce projet Java utilise CouchDB pour gérer une base de données NoSQL correspondant à la gestion d'une boîte de nuit.

## 🔧 Dépendances
- Apache HttpClient
- Gson (JSON)
- CouchDB (externe)

## ▶️ Lancer le projet

Assurez-vous d’avoir Maven installé.

```bash
cd boite-nuit-couchdb
mvn compile
mvn exec:java -Dexec.mainClass="com.boite.Main"
```

## 📁 Structure

- `src/main/java/com/boite/model/` : classes Java (Client, Drink, etc.)
- `src/main/java/com/boite/db/` : gestion des appels CouchDB (CRUD)
- `Main.java` : point d'entrée

## 📝 Configuration CouchDB
Modifiez les URL dans `CouchDBClient.java` si besoin : http://127.0.0.1:5984

---
