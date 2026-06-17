package com.example.milkcollection.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkcollection.R;
import com.example.milkcollection.model.LoanRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminLoanRequestsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminLoanAdapter adapter;
    private final List<LoanRequest> list = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_loan_requests);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        adapter = new AdminLoanAdapter(list, this);
        recyclerView.setAdapter(adapter);

        loadLoans();
    }

    private void loadLoans() {

        db.collection("loan_requests")
                .get()
                .addOnSuccessListener(snapshot -> {

                    list.clear();

                    for (var doc : snapshot.getDocuments()) {

                        LoanRequest loan = doc.toObject(LoanRequest.class);

                        if (loan != null) {
                            loan.setLoanId(doc.getId());
                            list.add(loan);
                        }
                    }

                    list.sort((a, b) -> {

                        String s1 = a.getLoanStatus() == null ? "pending" : a.getLoanStatus();
                        String s2 = b.getLoanStatus() == null ? "pending" : b.getLoanStatus();

                        int p1 = getPriority(s1);
                        int p2 = getPriority(s2);

                        if (p1 != p2) return p1 - p2;

                        Long t1 = a.getCreatedAt() == null ? 0L : a.getCreatedAt();
                        Long t2 = b.getCreatedAt() == null ? 0L : b.getCreatedAt();

                        return Long.compare(t2, t1);
                    });

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private int getPriority(String status) {
        if (status == null || status.equals("pending")) return 0;
        if (status.equals("approved")) return 1;
        return 2;
    }
}