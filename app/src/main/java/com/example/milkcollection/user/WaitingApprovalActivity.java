package com.example.milkcollection.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.milkcollection.R;
import com.example.milkcollection.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class WaitingApprovalActivity extends AppCompatActivity {

    Button backToLoginBtn;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_approval);

        backToLoginBtn = findViewById(R.id.backToLoginBtn);
        auth = FirebaseAuth.getInstance();

        backToLoginBtn.setOnClickListener(v -> {

            // 🔥 Logout user
            auth.signOut();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    // 🔥 AUTO LOGOUT WHEN APP GOES BACKGROUND
    @Override
    protected void onStop() {
        super.onStop();

        if (!isChangingConfigurations()) {
            FirebaseAuth.getInstance().signOut();
        }
    }
}