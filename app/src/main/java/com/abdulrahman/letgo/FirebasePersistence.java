package com.abdulrahman.letgo;

import com.google.firebase.database.FirebaseDatabase;

public class FirebasePersistence extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}