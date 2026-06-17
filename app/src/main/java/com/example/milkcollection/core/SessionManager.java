package com.example.milkcollection.core;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SessionManager {

    public static FirebaseUser getCurrentUser(){

        return FirebaseAuth.getInstance().getCurrentUser();

    }

    public static boolean isLoggedIn(){

        return getCurrentUser() != null;

    }

}
