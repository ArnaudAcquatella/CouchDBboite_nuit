# Boîte de Nuit - CouchDB + Java

Ce projet est une application de gestion d'une boîte de nuit, développée en **Java (Maven)** avec **CouchDB/Cloudant** comme base de données NoSQL.

---

## 🚀 Fonctionnalités

- Gestion des **clients**, **réservations**, **commandes**, **casiers**, **tables**, **employés**, **événements**, **boissons**
- Accès aux données via **CouchDB** en respectant les principes **NoSQL document-based**
- Requêtes via :
  - 🔍 **Mango** (requêtes simples)
  - 📊 **MapReduce (views)** pour les agrégations (boissons commandées, réservations par événement, etc.)
- Méthodes CRUD (Create/Read/Update/Delete) pour chaque entité
- Génération de données en JSON pour tests

---

## 🛠 Technologies

- Java 8+
- Maven
- Cloudant Java SDK (client CouchDB compatible)
- CouchDB 3+

---

## 📁 Structure du projet

```
boite-nuit-couchdb/
├── src/
│   ├── main/java/com/boite/
│   │   ├── model/          # Entités (Client, Order, etc.)
│   │   ├── service/        # Logique requêtes (NightClubService)
│   │   ├── CRUD/           # Méthodes CRUD pour chaque entité
│   │   └── db/             # Connexion et design docs CouchDB
│   └── Main.java           # Point d'entrée
├── pom.xml                 # Dépendances Maven
└── README.md
```

---

## 📦 Lancer le projet

1. **Configurer CouchDB / Cloudant**
    - Télécharger Maven:
        https://maven.apache.org/download.cgi
      Les instructions d'installation sont disponible ici:
        https://maven.apache.org/install.html
    - Télécharger CouchDB:
        https://couchdb.apache.org/#download
    - Une interface de visualisation de la base est disponible sur Fauxton:
        http://127.0.0.1:5984/_utils/#login
    
2. **Configurer CouchDB / Cloudant**
   - Créer la base : `boite_nuit` (L'execution du projet la créé si elle n'existe pas encore)
   - Créer un utilisateur + mot de passe

3. **Modifier la configuration dans `CouchDBConnect.java`**
   ```java
   public CouchDBConnect() throws MalformedURLException {
        client = ClientBuilder
                .url(new URL("http://127.0.0.1:5984"))
                .username("admin")
                .password("admin")
                .build();
        db = client.database("boite_nuit", true);
   ```

## 📤 Importer des données

Des fichiers JSON de test peuvent être insérés via `curl` :

Sur Linux:
```bash
curl -X POST -u user:pass http://localhost:5984/boite_nuit/_bulk_docs \
  -H "Content-Type: application/json" \
  -d @generated_orders.json
```
  
Sur Windows, se placer dans le dossier boite-nuit-couchdb puis:
```
  for %f in (data\*.json) do curl -X POST http://127.0.0.1:5984/boite_nuit/_bulk_docs -H "Content-Type: application/json" -u admin:admin -d @%f
```
---

4. **Lancer l'app :**

```bash
mvn clean compile
mvn exec:java
```

---

## 🔍 Exemples de requêtes

- Nombre de commandes par boisson (MapReduce)
- Nombre de réservations par événement (MapReduce)
- Réservations d’un client (Mango)
- Commandes servies par un employé (MapReduce avec filtrage)
- Recherche de boissons par catégorie ou prix (Mango)

---

## 🧠 Bonnes pratiques respectées

- Structure NoSQL claire : documents séparés, IDs utilisés comme références
- Vues MapReduce bien nommées et optimisées (`by_client`, `by_event`, etc.)
- Aucun champ redondant (les jointures sont faites côté Java)
- Vues mises à jour automatiquement via `DesignDocBuilder`
