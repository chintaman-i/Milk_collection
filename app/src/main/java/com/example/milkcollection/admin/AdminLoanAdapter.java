package com.example.milkcollection.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.milkcollection.R;
import com.example.milkcollection.model.LoanRepayment;
import com.example.milkcollection.model.LoanRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminLoanAdapter extends RecyclerView.Adapter<AdminLoanAdapter.LoanVH> {

    private final List<LoanRequest> list;
    private final Context context;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AdminLoanAdapter(List<LoanRequest> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public LoanVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_loan, parent, false);

        return new LoanVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoanVH holder, int position) {

        LoanRequest loan = list.get(position);

        String status =
                loan.getLoanStatus() == null
                        ? "pending"
                        : loan.getLoanStatus();

        String info =
                "Requested: ₹" + value(loan.getLoanAmount());

        if (loan.getApprovedAmount() != null &&
                loan.getApprovedAmount() > 0) {

            info += "\nApproved: ₹" + loan.getApprovedAmount();
        }

        info += "\nPaid: ₹" + value(loan.getPaidAmount());
        info += "\nRemaining: ₹" + value(loan.getRemainingAmount());

        holder.nameTv.setText(
                loan.getFarmerName() == null
                        ? "Unknown Farmer"
                        : loan.getFarmerName()
        );

        holder.amountTv.setText(info);

        holder.statusTv.setText(
                "Status: " + status.toUpperCase()
        );

        if ("approved".equalsIgnoreCase(status)) {

            holder.statusTv.setTextColor(
                    Color.parseColor("#2E7D32"));

            holder.approveBtn.setText("Add Repayment");
            holder.approveBtn.setEnabled(true);

            holder.rejectBtn.setVisibility(View.GONE);

            holder.approveBtn.setOnClickListener(v ->
                    showRepaymentDialog(loan, position)
            );

        }
        else if ("completed".equalsIgnoreCase(status)) {

            holder.statusTv.setTextColor(
                    Color.parseColor("#1565C0"));

            holder.approveBtn.setEnabled(false);
            holder.approveBtn.setText("Loan Closed");

            holder.rejectBtn.setVisibility(View.GONE);
        }
        else if ("rejected".equalsIgnoreCase(status)) {

            holder.statusTv.setTextColor(
                    Color.parseColor("#C62828"));

            holder.approveBtn.setEnabled(false);
            holder.approveBtn.setText("Rejected");

            holder.rejectBtn.setVisibility(View.GONE);
        }
        else {

            holder.statusTv.setTextColor(
                    Color.parseColor("#F9A825"));

            holder.approveBtn.setText("Approve");
            holder.approveBtn.setEnabled(true);

            holder.rejectBtn.setVisibility(View.VISIBLE);

            holder.approveBtn.setOnClickListener(v ->
                    showApproveDialog(loan, position)
            );

            holder.rejectBtn.setOnClickListener(v ->
                    rejectLoan(loan, position)
            );
        }
    }

    private double value(Double d) {
        return d == null ? 0 : d;
    }

    private void rejectLoan(LoanRequest loan, int position) {

        Map<String,Object> map = new HashMap<>();
        map.put("loanStatus","rejected");

        db.collection("loan_requests")
                .document(loan.getLoanId())
                .update(map)
                .addOnSuccessListener(unused -> {

                    loan.setLoanStatus("rejected");

                    notifyItemChanged(position);

                    Toast.makeText(
                            context,
                            "Loan Rejected",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void showApproveDialog(
            LoanRequest loan,
            int position
    ) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_approve_loan,null);

        EditText amountInput =
                view.findViewById(R.id.amountInput);

        EditText remarkInput =
                view.findViewById(R.id.remarkInput);

        amountInput.setText(
                String.valueOf(
                        value(loan.getLoanAmount())
                )
        );

        AlertDialog dialog =
                new AlertDialog.Builder(context)
                        .setView(view)
                        .create();

        Button approveBtn =
                view.findViewById(R.id.approveBtn);

        Button cancelBtn =
                view.findViewById(R.id.cancelBtn);

        cancelBtn.setOnClickListener(v ->
                dialog.dismiss()
        );

        approveBtn.setOnClickListener(v -> {

            String amountStr =
                    amountInput.getText()
                            .toString()
                            .trim();

            if (amountStr.isEmpty()) {
                return;
            }

            double approvedAmount =
                    Double.parseDouble(amountStr);

            String remark =
                    remarkInput.getText()
                            .toString()
                            .trim();

            Map<String,Object> map =
                    new HashMap<>();

            map.put("loanStatus","approved");
            map.put("approvedAmount",approvedAmount);
            map.put("remainingAmount",approvedAmount);
            map.put("paidAmount",0);
            map.put("adminRemark",remark);
            map.put("approvalDate",
                    System.currentTimeMillis());

            db.collection("loan_requests")
                    .document(loan.getLoanId())
                    .update(map)
                    .addOnSuccessListener(unused -> {

                        loan.setLoanStatus("approved");
                        loan.setApprovedAmount(
                                approvedAmount);

                        loan.setRemainingAmount(
                                approvedAmount);

                        loan.setPaidAmount(0.0);

                        notifyItemChanged(position);

                        Toast.makeText(
                                context,
                                "Loan Approved",
                                Toast.LENGTH_SHORT
                        ).show();

                        dialog.dismiss();
                    });
        });

        dialog.show();
    }

    private void showRepaymentDialog(
            LoanRequest loan,
            int position
    ) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.dialog_add_repayment,
                        null
                );

        TextView remainingTv =
                view.findViewById(
                        R.id.remainingAmountTv
                );

        EditText paymentEt =
                view.findViewById(
                        R.id.paymentAmountEt
                );

        Button saveBtn =
                view.findViewById(
                        R.id.saveBtn
                );

        Button cancelBtn =
                view.findViewById(
                        R.id.cancelBtn
                );

        double remaining =
                value(loan.getRemainingAmount());

        remainingTv.setText(
                "Remaining: ₹" + remaining
        );

        AlertDialog dialog =
                new AlertDialog.Builder(context)
                        .setView(view)
                        .create();

        cancelBtn.setOnClickListener(v ->
                dialog.dismiss()
        );

        saveBtn.setOnClickListener(v -> {

            String paymentStr =
                    paymentEt.getText()
                            .toString()
                            .trim();

            if (paymentStr.isEmpty()) {

                Toast.makeText(
                        context,
                        "Enter payment amount",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            double payment =
                    Double.parseDouble(paymentStr);

            if (payment <= 0) {

                Toast.makeText(
                        context,
                        "Invalid amount",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (payment > remaining) {

                Toast.makeText(
                        context,
                        "Payment exceeds balance",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            double paid =
                    value(loan.getPaidAmount());

            double newPaid =
                    paid + payment;

            double newRemaining =
                    remaining - payment;

            String status =
                    newRemaining <= 0
                            ? "completed"
                            : "approved";

            Map<String,Object> update =
                    new HashMap<>();

            update.put("paidAmount",newPaid);
            update.put("remainingAmount",
                    newRemaining);
            update.put("loanStatus",status);

            db.collection("loan_requests")
                    .document(loan.getLoanId())
                    .update(update)
                    .addOnSuccessListener(unused -> {

                        String repaymentId =
                                db.collection(
                                                "loan_repayments")
                                        .document()
                                        .getId();

                        LoanRepayment repayment =
                                new LoanRepayment();

                        repayment.setRepaymentId(
                                repaymentId);

                        repayment.setLoanId(
                                loan.getLoanId());

                        repayment.setUid(
                                loan.getUid());

                        repayment.setFarmerName(
                                loan.getFarmerName());

                        repayment.setAmountPaid(
                                payment);

                        repayment.setPreviousRemaining(
                                remaining);

                        repayment.setNewRemaining(
                                newRemaining);

                        repayment.setPaymentDate(
                                System.currentTimeMillis());

                        repayment.setEnteredBy(
                                "admin");

                        db.collection("loan_repayments")
                                .document(repaymentId)
                                .set(repayment);

                        loan.setPaidAmount(
                                newPaid);

                        loan.setRemainingAmount(
                                newRemaining);

                        loan.setLoanStatus(
                                status);

                        notifyItemChanged(position);

                        Toast.makeText(
                                context,
                                "Repayment Added",
                                Toast.LENGTH_SHORT
                        ).show();

                        dialog.dismiss();
                    });
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class LoanVH
            extends RecyclerView.ViewHolder {

        TextView nameTv;
        TextView amountTv;
        TextView statusTv;

        Button approveBtn;
        Button rejectBtn;

        public LoanVH(@NonNull View itemView) {
            super(itemView);

            nameTv =
                    itemView.findViewById(R.id.nameTv);

            amountTv =
                    itemView.findViewById(R.id.amountTv);

            statusTv =
                    itemView.findViewById(R.id.statusTv);

            approveBtn =
                    itemView.findViewById(R.id.approveBtn);

            rejectBtn =
                    itemView.findViewById(R.id.rejectBtn);
        }
    }
}