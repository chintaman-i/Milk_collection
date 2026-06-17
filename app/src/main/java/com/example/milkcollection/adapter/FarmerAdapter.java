package com.example.milkcollection.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkcollection.R;
import com.example.milkcollection.admin.FarmerProfileActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FarmerAdapter extends RecyclerView.Adapter<FarmerAdapter.ViewHolder> {

    Context context;

    ArrayList<QueryDocumentSnapshot> farmerList;

    FirebaseFirestore db;

    public FarmerAdapter(
            Context context,
            ArrayList<QueryDocumentSnapshot> farmerList
    ) {
        this.context = context;
        this.farmerList = farmerList;

        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_farmer,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        QueryDocumentSnapshot doc =
                farmerList.get(position);

        String firstName =
                doc.getString("firstName");

        String lastName =
                doc.getString("lastName");

        Long farmerIdLong =
                doc.getLong("farmerId");

        String uid = doc.getId();

        int farmerId = 0;

        if (farmerIdLong != null) {
            farmerId = farmerIdLong.intValue();
        }

        String formattedId =
                String.format("%04d", farmerId);

        holder.farmerIdText.setText(
                "Farmer ID: " + formattedId
        );

        holder.nameText.setText(
                firstName + " " + lastName
        );

        loadPendingBalance(holder, uid);

        // OPEN FARMER PROFILE
        holder.itemView.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            context,
                            FarmerProfileActivity.class
                    );

            intent.putExtra("uid", uid);

            context.startActivity(intent);

        });
    }

    private void loadPendingBalance(
            ViewHolder holder,
            String uid
    ) {

        db.collection("milk_entries")
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener(milkSnapshots -> {

                    double totalEarnings = 0;

                    for (QueryDocumentSnapshot milkDoc :
                            milkSnapshots) {

                        Double amount =
                                milkDoc.getDouble("totalAmount");

                        if (amount != null) {
                            totalEarnings += amount;
                        }
                    }

                    double finalTotalEarnings =
                            totalEarnings;

                    db.collection("payments")
                            .whereEqualTo("uid", uid)
                            .whereEqualTo("status", "confirmed")
                            .get()
                            .addOnSuccessListener(paymentSnapshots -> {

                                double totalPayments = 0;

                                for (QueryDocumentSnapshot paymentDoc :
                                        paymentSnapshots) {

                                    Double amount =
                                            paymentDoc.getDouble("amount");

                                    if (amount != null) {
                                        totalPayments += amount;
                                    }
                                }

                                double pending =
                                        finalTotalEarnings
                                                - totalPayments;

                                holder.pendingText.setText(
                                        "Pending: ₹" + pending
                                );

                            });

                });
    }

    @Override
    public int getItemCount() {
        return farmerList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView farmerIdText,
                nameText,
                pendingText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            farmerIdText =
                    itemView.findViewById(R.id.farmerIdText);

            nameText =
                    itemView.findViewById(R.id.nameText);

            pendingText =
                    itemView.findViewById(R.id.pendingText);
        }
    }
}