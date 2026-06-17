package com.example.milkcollection.user;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkcollection.R;
import com.example.milkcollection.model.LoanRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyLoansActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LoanAdapter adapter;
    private final List<LoanRequest> list = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_loans);

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        adapter = new LoanAdapter(list, this);
        recyclerView.setAdapter(adapter);

        loadLoans();
    }

    private void loadLoans() {

        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();

        db.collection("loan_requests")
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener(snapshot -> {

                    Toast.makeText(
                            this,
                            "Found Loans: " + snapshot.size(),
                            Toast.LENGTH_LONG
                    ).show();

                    list.clear();

                    for (var doc : snapshot.getDocuments()) {

                        System.out.println(doc.getData());

                        LoanRequest loan =
                                doc.toObject(LoanRequest.class);

                        if (loan != null) {
                            loan.setLoanId(doc.getId());
                            list.add(loan);
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }
}