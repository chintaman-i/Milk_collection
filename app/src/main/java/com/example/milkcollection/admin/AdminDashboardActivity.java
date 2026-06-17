package com.example.milkcollection.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.milkcollection.R;
import com.example.milkcollection.auth.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    Button manageUsersBtn,
            logoutBtn,
            milkEntryBtn,
            paymentEntryBtn,
            farmerManagementBtn,
            globalMilkHistoryBtn,
            globalPaymentHistoryBtn,
            manageFeedBtn,
            feedOrdersBtn,
            loanRequestsBtn;

    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // INIT VIEWS
        manageUsersBtn = findViewById(R.id.manageUsersBtn);
        milkEntryBtn = findViewById(R.id.milkEntryBtn);
        paymentEntryBtn = findViewById(R.id.paymentEntryBtn);
        farmerManagementBtn = findViewById(R.id.farmerManagementBtn);
        globalMilkHistoryBtn = findViewById(R.id.globalMilkHistoryBtn);
        globalPaymentHistoryBtn = findViewById(R.id.globalPaymentHistoryBtn);
        manageFeedBtn = findViewById(R.id.manageFeedBtn);
        feedOrdersBtn = findViewById(R.id.feedOrdersBtn);
        loanRequestsBtn = findViewById(R.id.loanRequestsBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        auth = FirebaseAuth.getInstance();

        // GOOGLE SIGN-IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // MILK ENTRY
        milkEntryBtn.setOnClickListener(v ->
                startActivity(new Intent(this, MilkEntryActivity.class))
        );

        // PAYMENT ENTRY
        paymentEntryBtn.setOnClickListener(v ->
                startActivity(new Intent(this, PaymentEntryActivity.class))
        );

        // FARMER MANAGEMENT
        farmerManagementBtn.setOnClickListener(v ->
                startActivity(new Intent(this, FarmerManagementActivity.class))
        );

        // GLOBAL MILK HISTORY
        globalMilkHistoryBtn.setOnClickListener(v ->
                startActivity(new Intent(this, GlobalMilkHistoryActivity.class))
        );

        // GLOBAL PAYMENT HISTORY
        globalPaymentHistoryBtn.setOnClickListener(v ->
                startActivity(new Intent(this, GlobalPaymentHistoryActivity.class))
        );

        // MANAGE USERS
        manageUsersBtn.setOnClickListener(v ->
                startActivity(new Intent(this, ManageUsersActivity.class))
        );

        // FEED MANAGEMENT
        manageFeedBtn.setOnClickListener(v ->
                startActivity(new Intent(this, ManageFeedActivity.class))
        );

        // FEED ORDERS
        feedOrdersBtn.setOnClickListener(v ->
                startActivity(new Intent(this, FeedRequestsActivity.class))
        );

        // LOAN REQUESTS (FIXED ✔)
        loanRequestsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AdminLoanRequestsActivity.class))
        );

        // LOGOUT
        logoutBtn.setOnClickListener(v -> {

            auth.signOut();

            googleSignInClient.signOut().addOnCompleteListener(task -> {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });

        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!isChangingConfigurations()) {
            FirebaseAuth.getInstance().signOut();
        }
    }
}