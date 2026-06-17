package com.example.milkcollection.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkcollection.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {

    Context context;

    ArrayList<QueryDocumentSnapshot> paymentList;

    FirebaseFirestore db;

    boolean showFarmerInfo;

    public PaymentAdapter(
            Context context,
            ArrayList<QueryDocumentSnapshot> paymentList
    ) {

        this.context = context;
        this.paymentList = paymentList;

        this.showFarmerInfo = false;

        db = FirebaseFirestore.getInstance();
    }

    public PaymentAdapter(
            Context context,
            ArrayList<QueryDocumentSnapshot> paymentList,
            boolean showFarmerInfo
    ) {

        this.context = context;
        this.paymentList = paymentList;

        this.showFarmerInfo = showFarmerInfo;

        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_payment,
                        parent,
                        false
                );

        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PaymentViewHolder holder,
            int position
    ) {

        QueryDocumentSnapshot doc =
                paymentList.get(position);

        Double amount =
                doc.getDouble("amount");

        String method =
                doc.getString("paymentMethod");

        String status =
                doc.getString("status");

        String uid =
                doc.getString("uid");

        if (status == null) {

            status =
                    "pending_confirmation";
        }

        Timestamp createdAt =
                doc.getTimestamp("createdAt");

        String time = "";

        if (createdAt != null) {

            SimpleDateFormat format =
                    new SimpleDateFormat(
                            "dd MMM yyyy hh:mm a",
                            Locale.getDefault()
                    );

            time =
                    format.format(
                            createdAt.toDate()
                    );
        }

        holder.amountText.setText("₹" + amount);

        holder.methodText.setText(method);

        holder.timeText.setText(time);

        // SHOW FARMER INFO
        if (showFarmerInfo && uid != null) {

            loadFarmerInfo(holder, uid);

        }

        // STATUS UI
        if (status.equals("confirmed")) {

            holder.statusText.setText("Received");

            holder.statusText.setBackgroundColor(
                    Color.parseColor("#E8F5E9")
            );

            holder.statusText.setTextColor(
                    Color.parseColor("#2E7D32")
            );

        } else {

            holder.statusText.setText("Pending");

            holder.statusText.setBackgroundColor(
                    Color.parseColor("#FFF3E0")
            );

            holder.statusText.setTextColor(
                    Color.parseColor("#EF6C00")
            );
        }

        // CLICK DETAILS
        holder.itemView.setOnClickListener(v -> {

            showDetails(doc);

        });
    }

    private void loadFarmerInfo(
            PaymentViewHolder holder,
            String uid
    ) {

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    String firstName =
                            documentSnapshot.getString(
                                    "firstName"
                            );

                    String lastName =
                            documentSnapshot.getString(
                                    "lastName"
                            );

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

                    holder.farmerNameText.setVisibility(
                            View.VISIBLE
                    );

                    holder.farmerIdText.setVisibility(
                            View.VISIBLE
                    );

                    holder.farmerNameText.setText(
                            firstName + " " + lastName
                    );

                    holder.farmerIdText.setText(
                            "Farmer ID: " + formattedId
                    );

                });
    }

    @Override
    public int getItemCount() {

        return paymentList.size();
    }

    class PaymentViewHolder extends RecyclerView.ViewHolder {

        TextView amountText,
                statusText,
                methodText,
                timeText,
                farmerNameText,
                farmerIdText;

        public PaymentViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            amountText =
                    itemView.findViewById(
                            R.id.amountText
                    );

            statusText =
                    itemView.findViewById(
                            R.id.statusText
                    );

            methodText =
                    itemView.findViewById(
                            R.id.methodText
                    );

            timeText =
                    itemView.findViewById(
                            R.id.timeText
                    );

            farmerNameText =
                    itemView.findViewById(
                            R.id.farmerNameText
                    );

            farmerIdText =
                    itemView.findViewById(
                            R.id.farmerIdText
                    );
        }
    }

    private void showDetails(
            QueryDocumentSnapshot doc
    ) {

        Double amount =
                doc.getDouble("amount");

        String method =
                doc.getString("paymentMethod");

        String note =
                doc.getString("note");

        String status =
                doc.getString("status");

        if (status == null) {

            status =
                    "pending_confirmation";
        }

        String displayStatus;

        if ("confirmed".equals(status)) {

            displayStatus = "Received";

        } else {

            displayStatus = "Pending";
        }

        Timestamp createdAt =
                doc.getTimestamp("createdAt");

        String time = "";

        if (createdAt != null) {

            SimpleDateFormat format =
                    new SimpleDateFormat(
                            "dd MMM yyyy hh:mm a",
                            Locale.getDefault()
                    );

            time =
                    format.format(
                            createdAt.toDate()
                    );
        }

        String details =
                "💰 Amount: ₹" + amount
                        + "\n\n💳 Method: " + method
                        + "\n\n✅ Status: " + displayStatus
                        + "\n\n🕒 Time: " + time
                        + "\n\n📝 Note: " + note;

        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);

        builder.setTitle("Payment Details");

        builder.setMessage(details);

        // CONFIRM BUTTON
        if (status.equals("pending_confirmation")) {

            builder.setPositiveButton(
                    "Confirm Received",
                    (dialog, which) -> {

                        confirmPayment(doc.getId());

                    }
            );
        }

        builder.setNegativeButton(
                "Close",
                null
        );

        builder.show();
    }

    private void confirmPayment(
            String paymentId
    ) {

        db.collection("payments")
                .document(paymentId)
                .update(
                        "status",
                        "confirmed"
                )
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            context,
                            "Payment Confirmed",
                            Toast.LENGTH_SHORT
                    ).show();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            context,
                            e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }
}