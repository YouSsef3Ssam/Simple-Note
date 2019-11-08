package com.example.simplenote.network.client;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public abstract class FirebaseDB {

    private static FirebaseDatabase instance;
    private static DatabaseReference reference;

    public static synchronized DatabaseReference getInstance(){
        if (instance == null){
            instance = FirebaseDatabase.getInstance();
            instance.setPersistenceEnabled(true);
            reference = instance.getReference();
            reference.keepSynced(true);
        }
        return reference;
    }
}