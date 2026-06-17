package com.example.milkcollection.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.milkcollection.user.FeedStoreActivity;
import com.example.milkcollection.user.UserFeedOrdersActivity;
import androidx.appcompat.app.AppCompatActivity;

import com.example.milkcollection.R;
import com.example.milkcollection.auth.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserDashboardActivity extends AppCompatActivity {

    Button logoutBtn, historyBtn, paymentHistoryBtn, feedStoreBtn, feedOrdersBtn;

    TextView farmerIdText, milkText, earningsText,
            totalEarningsText, receivedPaymentsText, pendingBalanceText;
    Button requestLoanBtn;
    Button myLoansBtn;
    FirebaseAuth auth;
    FirebaseFirestore db;
    GoogleSignInClient googleSignInClient;
    FirebaseUser currentUser;

    double totalMilk = 0;
    double todayEarnings = 0;
    double allTimeEarnings = 0;
    double totalPaymentsReceived = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        initViews();
        initFirebase();
        setupGoogleSignIn();
        setupClicks();


        loadFarmerData();
    }

    private void initViews() {

        logoutBtn = findViewById(R.id.logoutBtn);
        historyBtn = findViewById(R.id.historyBtn);
        paymentHistoryBtn = findViewById(R.id.paymentHistoryBtn);

        feedStoreBtn = findViewById(R.id.feedStoreBtn);
        feedOrdersBtn = findViewById(R.id.feedOrdersBtn);

        farmerIdText = findViewById(R.id.farmerIdText);
        milkText = findViewById(R.id.milkText);
        earningsText = findViewById(R.id.earningsText);

        totalEarningsText = findViewById(R.id.totalEarningsText);
        receivedPaymentsText = findViewById(R.id.receivedPaymentsText);
        pendingBalanceText = findViewById(R.id.pendingBalanceText);

        requestLoanBtn = findViewById(R.id.requestLoanBtn);

        myLoansBtn = findViewById(R.id.myLoansBtn);
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupClicks() {

        historyBtn.setOnClickListener(v ->
                startActivity(new Intent(this, MilkHistoryActivity.class)));

        paymentHistoryBtn.setOnClickListener(v ->
                startActivity(new Intent(this, PaymentHistoryActivity.class)));

        feedStoreBtn.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                this,
                                FeedStoreActivity.class
                        )
                ));

        feedOrdersBtn.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                this,
                                UserFeedOrdersActivity.class
                        )
                ));
        requestLoanBtn.setOnClickListener(v ->

                startActivity(
                        new Intent(
                                this,
                                RequestLoanActivity.class
                        )
                )
        );

        myLoansBtn.setOnClickListener(v ->

                startActivity(
                        new Intent(
                                this,
                                MyLoansActivity.class
                        )
                )
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

    private void loadFarmerData() {

        if (currentUser == null) return;

        String uid = currentUser.getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) return;

                    Long farmerIdLong = document.getLong("farmerId");

                    if (farmerIdLong != null) {
                        farmerIdText.setText(
                                "Farmer ID: " + String.format("%04d", farmerIdLong)
                        );
                    }

                    loadTodayMilkData(uid);
                    loadFinancialSummary(uid);

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void loadTodayMilkData(String uid) {

        String today = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());

        db.collection("milk_entries")
                .whereEqualTo("uid", uid)
                .whereEqualTo("date", today)
                .get()
                .addOnSuccessListener(snapshot -> {

                    totalMilk = 0;
                    todayEarnings = 0;

                    for (QueryDocumentSnapshot doc : snapshot) {

                        Double qty = doc.getDouble("quantity");
                        Double amt = doc.getDouble("totalAmount");

                        if (qty != null) totalMilk += qty;
                        if (amt != null) todayEarnings += amt;
                    }

                    milkText.setText("Today's Milk: " + totalMilk + " L");
                    earningsText.setText("Today's Earnings: ₹" + todayEarnings);
                });
    }

    private void loadFinancialSummary(String uid) {

        db.collection("milk_entries")
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener(snapshot -> {

                    allTimeEarnings = 0;

                    for (QueryDocumentSnapshot doc : snapshot) {
                        Double amt = doc.getDouble("totalAmount");
                        if (amt != null) allTimeEarnings += amt;
                    }

                    totalEarningsText.setText("Total Earnings: ₹" + allTimeEarnings);

                    loadPayments(uid);
                });
    }

    private void loadPayments(String uid) {

        db.collection("payments")
                .whereEqualTo("uid", uid)
                .whereEqualTo("status", "confirmed")
                .get()
                .addOnSuccessListener(snapshot -> {

                    totalPaymentsReceived = 0;

                    for (QueryDocumentSnapshot doc : snapshot) {
                        Double amt = doc.getDouble("amount");
                        if (amt != null) totalPaymentsReceived += amt;
                    }

                    receivedPaymentsText.setText(
                            "Received Payments: ₹" + totalPaymentsReceived
                    );

                    double pending = allTimeEarnings - totalPaymentsReceived;

                    pendingBalanceText.setText(
                            "Pending Balance: ₹" + pending
                    );
                });
    }
}