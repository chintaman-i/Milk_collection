package com.example.milkcollection.splash;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.milkcollection.R;
import com.example.milkcollection.core.NavigationController;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    FirebaseFirestore db;

    // 🔥 Smooth splash timing
    private long startTime;
    private static final long MIN_SPLASH_TIME = 600; // adjust 500–800 if needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        db = FirebaseFirestore.getInstance();

        // 🔥 Start time tracking
        startTime = System.currentTimeMillis();

        checkAppVersion();
    }

    private void checkAppVersion() {

        db.collection("app_config")
                .document("android")
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        proceedNext();
                        return;
                    }

                    Long minVersionObj = doc.getLong("minRequiredVersion");

                    int minVersion = (minVersionObj != null) ? minVersionObj.intValue() : 1;

                    int currentVersion = 1;

                    if (currentVersion < minVersion) {

                        Toast.makeText(this,
                                "Please update app to continue",
                                Toast.LENGTH_LONG).show();

                        finish();

                    } else {

                        proceedNext();
                    }

                })
                .addOnFailureListener(e -> proceedNext());
    }

    private void proceedNext() {

        long elapsed = System.currentTimeMillis() - startTime;
        long remaining = MIN_SPLASH_TIME - elapsed;

        if (remaining > 0) {

            new Handler().postDelayed(this::goNext, remaining);

        } else {

            goNext();
        }
    }

    private void goNext() {

        // 🚀 DIRECT NAVIGATION (Language screen skipped)
        NavigationController.goToNextScreen(this);
        finish();

        /*
        🔁 TO ENABLE LANGUAGE SCREEN IN FUTURE:

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean firstLaunch = prefs.getBoolean("first_launch", true);

        if (firstLaunch) {
            startActivity(new Intent(this, LanguageActivity.class));
            finish();
        } else {
            NavigationController.goToNextScreen(this);
            finish();
        }
        */
    }
}