package com.example.milkcollection.core;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.example.milkcollection.auth.VerifyEmailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthManager {

    private FirebaseAuth auth;

    public AuthManager() {
        auth = FirebaseAuth.getInstance();
    }

    public void login(Activity activity, String email, String password) {

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(activity,"Enter email and password",Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {

                    if(task.isSuccessful()){

                        FirebaseUser user = auth.getCurrentUser();

                        if(user == null){
                            Toast.makeText(activity,"Login error",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(!user.isEmailVerified()){

                            activity.startActivity(new Intent(activity, VerifyEmailActivity.class));
                            activity.finish();
                            return;
                        }

                        NavigationController.goToNextScreen(activity);

                    }else{

                        String error = task.getException().getMessage();

                        if(error != null && error.contains("no user record")){

                            Toast.makeText(activity,
                                    "No account found. Please create one.",
                                    Toast.LENGTH_LONG).show();

                        }else if(error != null && error.contains("password")){

                            Toast.makeText(activity,
                                    "Incorrect password. Try reset password.",
                                    Toast.LENGTH_LONG).show();

                        }else{

                            Toast.makeText(activity,
                                    "Login failed",
                                    Toast.LENGTH_LONG).show();

                        }
                    }

                });

    }

}
