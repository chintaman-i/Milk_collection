package com.example.milkcollection.admin;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.milkcollection.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class PaymentEntryActivity extends AppCompatActivity {

    EditText farmerIdEdit, amountEdit, noteEdit;

    Spinner paymentMethodSpinner;

    Button saveBtn;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_entry);

        farmerIdEdit = findViewById(R.id.farmerIdEdit);
        amountEdit = findViewById(R.id.amountEdit);
        noteEdit = findViewById(R.id.noteEdit);

        paymentMethodSpinner =
                findViewById(R.id.paymentMethodSpinner);

        saveBtn = findViewById(R.id.saveBtn);

        db = FirebaseFirestore.getInstance();

        // PAYMENT METHODS
        String[] methods = {
                "Cash",
                "Online"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        methods
                );

        paymentMethodSpinner.setAdapter(adapter);

        saveBtn.setOnClickListener(v -> savePayment());
    }

    private void savePayment() {

        String farmerIdText =
                farmerIdEdit.getText().toString().trim();

        String amountText =
                amountEdit.getText().toString().trim();

        String note =
                noteEdit.getText().toString().trim();

        if (farmerIdText.isEmpty()
                || amountText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Fill required fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int farmerId =
                Integer.parseInt(farmerIdText);

        double amount =
                Double.parseDouble(amountText);

        String paymentMethod =
                paymentMethodSpinner
                        .getSelectedItem()
                        .toString();

        // FIND FARMER UID
        db.collection("users")
                .whereEqualTo("farmerId", farmerId)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (queryDocumentSnapshots.isEmpty()) {

                        Toast.makeText(
                                this,
                                "Farmer not found",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    QueryDocumentSnapshot userDoc =
                            (QueryDocumentSnapshot)
                                    queryDocumentSnapshots
                                            .getDocuments()
                                            .get(0);

                    String uid = userDoc.getId();

                    Map<String, Object> payment =
                            new HashMap<>();

                    payment.put("uid", uid);

                    payment.put("farmerId", farmerId);

                    payment.put("amount", amount);

                    payment.put(
                            "paymentMethod",
                            paymentMethod
                    );

                    payment.put("note", note);

                    payment.put(
                            "createdAt",
                            Timestamp.now()
                    );

                    db.collection("payments")
                            .add(payment)
                            .addOnSuccessListener(documentReference -> {

                                Toast.makeText(
                                        this,
                                        "Payment Saved",
                                        Toast.LENGTH_SHORT
                                ).show();

                                clearFields();

                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        this,
                                        e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();

                            });

                });
    }

    private void clearFields() {

        farmerIdEdit.setText("");

        amountEdit.setText("");

        noteEdit.setText("");
    }
}