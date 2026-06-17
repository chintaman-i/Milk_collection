package com.example.milkcollection.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkcollection.R;
import com.example.milkcollection.adapter.PaymentAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdminPaymentHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    FirebaseFirestore db;

    ArrayList<QueryDocumentSnapshot> paymentList;

    PaymentAdapter adapter;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_admin_payment_history
        );

        recyclerView =
                findViewById(R.id.recyclerView);

        db =
                FirebaseFirestore.getInstance();

        uid =
                getIntent().getStringExtra("uid");

        paymentList =
                new ArrayList<>();

        adapter =
                new PaymentAdapter(
                        this,
                        paymentList
                );

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerView.setAdapter(adapter);

        loadPayments();
    }

    private void loadPayments() {

        if (uid == null) return;

        db.collection("payments")
                .whereEqualTo("uid", uid)
                .orderBy(
                        "createdAt",
                        Query.Direction.DESCENDING
                )
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            paymentList.clear();

                            for (
                                    QueryDocumentSnapshot doc :
                                    queryDocumentSnapshots
                            ) {

                                paymentList.add(doc);

                            }

                            adapter.notifyDataSetChanged();

                        }
                )
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }
}