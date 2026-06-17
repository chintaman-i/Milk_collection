package com.example.milkcollection.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.milkcollection.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class FarmerProfileActivity extends AppCompatActivity {

    TextView farmerIdText,
            nameText,
            mobileText,
            emailText,
            earningsText,
            paymentsText,
            pendingText;

    Button milkHistoryBtn,
            paymentHistoryBtn;

    FirebaseFirestore db;

    String uid;

    double totalEarnings = 0;

    double totalPayments = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_farmer_profile
        );

        farmerIdText =
                findViewById(R.id.farmerIdText);

        nameText =
                findViewById(R.id.nameText);

        mobileText =
                findViewById(R.id.mobileText);

        emailText =
                findViewById(R.id.emailText);

        earningsText =
                findViewById(R.id.earningsText);

        paymentsText =
                findViewById(R.id.paymentsText);

        pendingText =
                findViewById(R.id.pendingText);

        milkHistoryBtn =
                findViewById(R.id.milkHistoryBtn);

        paymentHistoryBtn =
                findViewById(R.id.paymentHistoryBtn);

        db =
                FirebaseFirestore.getInstance();

        uid =
                getIntent().getStringExtra(
                        "uid"
                );

        if (uid == null) {

            finish();

            return;
        }

        loadFarmerInfo();

        loadFinancialSummary();

        // VIEW MILK HISTORY
        milkHistoryBtn.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            this,
                            AdminMilkHistoryActivity.class
                    );

            intent.putExtra(
                    "uid",
                    uid
            );

            startActivity(intent);

        });

        // VIEW PAYMENT HISTORY
        paymentHistoryBtn.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            this,
                            AdminPaymentHistoryActivity.class
                    );

            intent.putExtra(
                    "uid",
                    uid
            );

            startActivity(intent);

        });
    }

    private void loadFarmerInfo() {

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    Long farmerIdLong =
                            documentSnapshot.getLong(
                                    "farmerId"
                            );

                    int farmerId = 0;

                    if (farmerIdLong != null) {

                        farmerId =
                                farmerIdLong.intValue();

                    }

                    String formattedId =
                            String.format(
                                    "%04d",
                                    farmerId
                            );

                    String firstName =
                            documentSnapshot.getString(
                                    "firstName"
                            );

                    String lastName =
                            documentSnapshot.getString(
                                    "lastName"
                            );

                    String mobile =
                            documentSnapshot.getString(
                                    "mobile"
                            );

                    String email =
                            documentSnapshot.getString(
                                    "email"
                            );

                    farmerIdText.setText(
                            "Farmer ID: "
                                    + formattedId
                    );

                    nameText.setText(
                            "Name: "
                                    + firstName
                                    + " "
                                    + lastName
                    );

                    mobileText.setText(
                            "Mobile: "
                                    + mobile
                    );

                    emailText.setText(
                            "Email: "
                                    + email
                    );

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }

    private void loadFinancialSummary() {

        db.collection("milk_entries")
                .whereEqualTo(
                        "uid",
                        uid
                )
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            totalEarnings = 0;

                            for (
                                    QueryDocumentSnapshot doc :
                                    queryDocumentSnapshots
                            ) {

                                Double amount =
                                        doc.getDouble(
                                                "totalAmount"
                                        );

                                if (amount != null) {

                                    totalEarnings += amount;

                                }
                            }

                            earningsText.setText(
                                    "Total Earnings: ₹"
                                            + totalEarnings
                            );

                            loadPayments();

                        }
                );
    }

    private void loadPayments() {

        db.collection("payments")
                .whereEqualTo(
                        "uid",
                        uid
                )
                .whereEqualTo(
                        "status",
                        "confirmed"
                )
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            totalPayments = 0;

                            for (
                                    QueryDocumentSnapshot doc :
                                    queryDocumentSnapshots
                            ) {

                                Double amount =
                                        doc.getDouble(
                                                "amount"
                                        );

                                if (amount != null) {

                                    totalPayments += amount;

                                }
                            }

                            paymentsText.setText(
                                    "Payments Paid: ₹"
                                            + totalPayments
                            );

                            double pending =
                                    totalEarnings
                                            - totalPayments;

                            pendingText.setText(
                                    "Pending Balance: ₹"
                                            + pending
                            );

                        }
                );
    }
}