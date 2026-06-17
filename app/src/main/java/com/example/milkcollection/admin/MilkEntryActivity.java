package com.example.milkcollection.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.milkcollection.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MilkEntryActivity extends AppCompatActivity {

    Spinner sessionSpinner, animalSpinner;

    EditText farmerIdEdit, quantityEdit, fatEdit, snfEdit;

    TextView totalText;

    Button saveBtn;

    FirebaseFirestore db;

    double rate = 32;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milk_entry);

        // INPUT FIELDS
        farmerIdEdit = findViewById(R.id.farmerIdEdit);
        quantityEdit = findViewById(R.id.quantityEdit);
        fatEdit = findViewById(R.id.fatEdit);
        snfEdit = findViewById(R.id.snfEdit);

        // TEXTVIEW
        totalText = findViewById(R.id.totalText);

        // BUTTON
        saveBtn = findViewById(R.id.saveBtn);

        // FIRESTORE
        db = FirebaseFirestore.getInstance();

        // SPINNERS
        sessionSpinner = findViewById(R.id.sessionSpinner);
        animalSpinner = findViewById(R.id.animalSpinner);

        // SESSION DATA
        String[] sessions = {"Morning", "Evening"};

        ArrayAdapter<String> sessionAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        sessions
                );

        sessionSpinner.setAdapter(sessionAdapter);

        // ANIMAL DATA
        String[] animals = {"Cow", "Buffalo"};

        ArrayAdapter<String> animalAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        animals
                );

        animalSpinner.setAdapter(animalAdapter);

        // AUTO CALCULATION
        quantityEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // SAVE BUTTON
        saveBtn.setOnClickListener(v -> saveEntry());
    }

    private void calculateTotal() {

        String qtyText = quantityEdit.getText().toString().trim();

        if (qtyText.isEmpty()) {

            totalText.setText("Total Amount: ₹0");
            return;
        }

        double quantity = Double.parseDouble(qtyText);

        double total = quantity * rate;

        totalText.setText("Total Amount: ₹" + total);
    }

    private void saveEntry() {

        String farmerIdText = farmerIdEdit.getText().toString().trim();
        String qtyText = quantityEdit.getText().toString().trim();
        String fatText = fatEdit.getText().toString().trim();
        String snfText = snfEdit.getText().toString().trim();

        if (farmerIdText.isEmpty()
                || qtyText.isEmpty()
                || fatText.isEmpty()
                || snfText.isEmpty()) {

            Toast.makeText(this,
                    "Fill all fields",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        int farmerId = Integer.parseInt(farmerIdText);

        double quantity = Double.parseDouble(qtyText);
        double fat = Double.parseDouble(fatText);
        double snf = Double.parseDouble(snfText);

        String session =
                sessionSpinner.getSelectedItem().toString();

        String animal =
                animalSpinner.getSelectedItem().toString();

        double total = quantity * rate;

        // DATE
        String currentDate = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());

        Timestamp timestamp = Timestamp.now();

        // FIND USER UID USING farmerId
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

                    Map<String, Object> entry = new HashMap<>();

                    entry.put("uid", uid);

                    entry.put("farmerId", farmerId);

                    entry.put("quantity", quantity);
                    entry.put("fat", fat);
                    entry.put("snf", snf);

                    entry.put("session", session);
                    entry.put("animalType", animal);

                    entry.put("rate", rate);
                    entry.put("totalAmount", total);

                    entry.put("date", currentDate);
                    entry.put("createdAt", timestamp);

                    db.collection("milk_entries")
                            .add(entry)
                            .addOnSuccessListener(documentReference -> {

                                Toast.makeText(this,
                                        "Entry Saved",
                                        Toast.LENGTH_SHORT).show();

                                clearFields();

                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(this,
                                        e.getMessage(),
                                        Toast.LENGTH_LONG).show();

                            });

                });
    }

    private void clearFields() {

        farmerIdEdit.setText("");
        quantityEdit.setText("");
        fatEdit.setText("");
        snfEdit.setText("");

        totalText.setText("Total Amount: ₹0");
    }
}