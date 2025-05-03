package com.boite.db;

import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.Database;

import java.net.MalformedURLException;
import java.net.URL;

public class CouchDBClient {

    private final CloudantClient client;
    private final Database db;

    public CouchDBClient() throws MalformedURLException {
        client = ClientBuilder
                .url(new URL("http://127.0.0.1:5984"))
                .username("admin")
                .password("admin")
                .build();
        db = client.database("boite_nuit", true); // true pour créer la DB si elle n'existe pas
    }
    
    public Database getDb() {
        return db;
    }

}
