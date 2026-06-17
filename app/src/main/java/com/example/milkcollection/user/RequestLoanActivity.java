package com.example.milkcollection.user;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.milkcollection.R;
import com.example.milkcollection.model.LoanRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RequestLoanActivity extends AppCompatActivity {


    private EditText amountEt, purposeEt, durationEt;
    private Button submitBtn;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_loan);

        amountEt = findViewById(R.id.amountEt);
        purposeEt = findViewById(R.id.purposeEt);
        durationEt = findViewById(R.id.durationEt);
        submitBtn = findViewById(R.id.submitBtn);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        submitBtn.setOnClickListener(v -> submitLoan());
    }

    private void submitLoan() {

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String amountStr = amountEt.getText().toString().trim();
        String purpose = purposeEt.getText().toString().trim();
        String durationStr = durationEt.getText().toString().trim();

        if (amountStr.isEmpty() || purpose.isEmpty() || durationStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        int duration = Integer.parseInt(durationStr);

        String uid = auth.getCurrentUser().getUid();

        String loanId = db.collection("loan_requests").document().getId();

        String requestDate = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(userDoc -> {

                    String firstName = userDoc.getString("firstName");
                    String lastName = userDoc.getString("lastName");

                    String farmerName =
                            (firstName == null ? "" : firstName) + " " +
                                    (lastName == null ? "" : lastName);

                    LoanRequest loan = new LoanRequest();

                    loan.setLoanId(loanId);
                    loan.setUid(uid);
                    loan.setFarmerName(farmerName.trim());

                    loan.setLoanAmount(amount);
                    loan.setPurpose(purpose);
                    loan.setDurationMonths((long) duration);

                    loan.setLoanStatus("pending");

                    loan.setApprovedAmount(0.0);
                    loan.setPaidAmount(0.0);
                    loan.setRemainingAmount(amount);

                    loan.setInterestEnabled(false);
                    loan.setInterestRate(0.0);

                    loan.setAdminRemark("");

                    loan.setRequestDate(requestDate);
                    loan.setCreatedAt(System.currentTimeMillis());

                    loan.setApprovalDate(0L);
                    loan.setNextDueDate(0L);

                    db.collection("loan_requests")
                            .document(loanId)
                            .set(loan)
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(
                                        RequestLoanActivity.this,
                                        "Loan Requested Successfully",
                                        Toast.LENGTH_SHORT
                                ).show();

                                amountEt.setText("");
                                purposeEt.setText("");
                                durationEt.setText("");
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(
                                            RequestLoanActivity.this,
                                            e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                RequestLoanActivity.this,
                                "User data not found",
                                Toast.LENGTH_LONG
                        ).show()
                );
    }


}
