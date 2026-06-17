package com.example.milkcollection.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkcollection.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MilkHistoryAdapter extends RecyclerView.Adapter<MilkHistoryAdapter.ViewHolder> {

    Context context;
    ArrayList<QueryDocumentSnapshot> milkList;
    boolean showFarmerInfo;
    FirebaseFirestore db;

    public MilkHistoryAdapter(Context context, ArrayList<QueryDocumentSnapshot> milkList) {

        this.context = context;
        this.milkList = milkList;
        this.showFarmerInfo = false;

        db = FirebaseFirestore.getInstance();
    }

    public MilkHistoryAdapter(
            Context context,
            ArrayList<QueryDocumentSnapshot> milkList,
            boolean showFarmerInfo
    ) {

        this.context = context;
        this.milkList = milkList;
        this.showFarmerInfo = showFarmerInfo;

        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.item_milk_history,
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
                milkList.get(position);

        double quantity =
                getDoubleValue(doc, "quantity");

        double amount =
                getDoubleValue(doc, "totalAmount");

        String session =
                doc.getString("session");

        String animal =
                doc.getString("animalType");

        String uid =
                doc.getString("uid");

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

        holder.quantityText.setText(
                quantity + " L"
        );

        holder.amountText.setText(
                "₹" + amount
        );

        holder.sessionText.setText(
                session != null ? session : "-"
        );

        holder.animalText.setText(
                animal != null ? animal : "-"
        );

        holder.timeText.setText(time);

        if (showFarmerInfo && uid != null) {

            loadFarmerInfo(holder, uid);
        }

        holder.itemView.setOnClickListener(v -> {

            showDetails(doc);

        });
    }

    // SAFE DOUBLE VALUE METHOD
    private double getDoubleValue(
            QueryDocumentSnapshot doc,
            String key
    ) {

        Object value = doc.get(key);

        if (value instanceof Long) {

            return ((Long) value).doubleValue();

        } else if (value instanceof Double) {

            return (Double) value;
        }

        return 0;
    }

    private void loadFarmerInfo(
            ViewHolder holder,
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

                    int farmerId =
                            farmerIdLong != null
                                    ? farmerIdLong.intValue()
                                    : 0;

                    holder.farmerNameText.setVisibility(
                            View.VISIBLE
                    );

                    holder.farmerIdText.setVisibility(
                            View.VISIBLE
                    );

                    holder.farmerNameText.setText(
                            (firstName != null ? firstName : "")
                                    + " "
                                    + (lastName != null ? lastName : "")
                    );

                    holder.farmerIdText.setText(
                            "Farmer ID: "
                                    + String.format(
                                    "%04d",
                                    farmerId
                            )
                    );
                });
    }

    @Override
    public int getItemCount() {

        return milkList != null
                ? milkList.size()
                : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView quantityText,
                amountText,
                sessionText,
                animalText,
                timeText,
                farmerNameText,
                farmerIdText;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            quantityText =
                    itemView.findViewById(
                            R.id.quantityText
                    );

            amountText =
                    itemView.findViewById(
                            R.id.amountText
                    );

            sessionText =
                    itemView.findViewById(
                            R.id.sessionText
                    );

            animalText =
                    itemView.findViewById(
                            R.id.animalText
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

        String date =
                doc.getString("date");

        String session =
                doc.getString("session");

        double quantity =
                getDoubleValue(doc, "quantity");

        double amount =
                getDoubleValue(doc, "totalAmount");

        double fat =
                getDoubleValue(doc, "fat");

        double snf =
                getDoubleValue(doc, "snf");

        double rate =
                getDoubleValue(doc, "rate");

        String animal =
                doc.getString("animalType");

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

                "🥛 Milk Quantity: "
                        + quantity
                        + " L"

                        + "\n\n💰 Amount: ₹"
                        + amount

                        + "\n\n📅 Date: "
                        + (date != null ? date : "-")

                        + "\n\n🕒 Time: "
                        + time

                        + "\n\n🌅 Session: "
                        + (session != null ? session : "-")

                        + "\n\n🐄 Animal: "
                        + (animal != null ? animal : "-")

                        + "\n\n🧪 Fat: "
                        + fat

                        + "\n\n🧪 SNF: "
                        + snf

                        + "\n\n📈 Rate: ₹"
                        + rate;

        new AlertDialog.Builder(context)
                .setTitle("Milk Entry Details")
                .setMessage(details)
                .setPositiveButton("OK", null)
                .show();
    }
}