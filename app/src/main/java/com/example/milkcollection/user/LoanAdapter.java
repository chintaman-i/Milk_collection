package com.example.milkcollection.user;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkcollection.R;
import com.example.milkcollection.model.LoanRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.ViewHolder> {

    private final List<LoanRequest> list;
    private final Context context;

    public LoanAdapter(List<LoanRequest> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.loan_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        LoanRequest loan = list.get(position);

        holder.amount.setText(
                "Loan Amount : ₹" +
                        (loan.getLoanAmount() != null
                                ? loan.getLoanAmount()
                                : 0)
        );

        String status = loan.getLoanStatus();

        holder.status.setText(
                "Status : " +
                        (status != null ? status : "Pending")
        );

        if ("Approved".equalsIgnoreCase(status)) {
            holder.status.setTextColor(Color.GREEN);
        }
        else if ("Rejected".equalsIgnoreCase(status)) {
            holder.status.setTextColor(Color.RED);
        }
        else {
            holder.status.setTextColor(Color.parseColor("#FFA500"));
        }

        holder.remaining.setText(
                "Outstanding : ₹" +
                        (loan.getRemainingAmount() != null
                                ? loan.getRemainingAmount()
                                : 0)
        );

        holder.paid.setText(
                "Paid : ₹" +
                        (loan.getPaidAmount() != null
                                ? loan.getPaidAmount()
                                : 0)
        );



        String formattedDate = "N/A";

        if (loan.getCreatedAt() != null) {

            formattedDate = new SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
            ).format(new Date(loan.getCreatedAt()));
        }

        holder.date.setText(
                "Requested On : " + formattedDate
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView amount;
        TextView status;
        TextView remaining;
        TextView paid;

        TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            amount = itemView.findViewById(R.id.amount);
            status = itemView.findViewById(R.id.status);
            remaining = itemView.findViewById(R.id.remaining);
            paid = itemView.findViewById(R.id.paid);

            date = itemView.findViewById(R.id.date);
        }
    }
}