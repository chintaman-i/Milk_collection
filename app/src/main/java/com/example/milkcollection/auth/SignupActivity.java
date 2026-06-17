package com.example.milkcollection.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.milkcollection.R;
import com.example.milkcollection.user.WaitingApprovalActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText firstName,
            middleName,
            lastName,
            mobile,
            email,
            password;

    Button createAccountBtn,
            backToLoginBtn;

    FirebaseAuth auth;

    FirebaseFirestore db;

    boolean isGoogleUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        firstName =
                findViewById(R.id.firstName);

        middleName =
                findViewById(R.id.middleName);

        lastName =
                findViewById(R.id.lastName);

        mobile =
                findViewById(R.id.mobile);

        email =
                findViewById(R.id.email);

        password =
                findViewById(R.id.password);

        createAccountBtn =
                findViewById(R.id.createAccountBtn);

        backToLoginBtn =
                findViewById(R.id.backToLoginBtn);

        auth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();

        String googleEmail =
                getIntent().getStringExtra("googleEmail");

        if (googleEmail != null) {

            isGoogleUser = true;

            email.setText(googleEmail);

            email.setEnabled(false);
        }

        createAccountBtn.setOnClickListener(v -> createAccount());

        backToLoginBtn.setOnClickListener(v -> finish());
    }

    private void createAccount() {

        String first =
                firstName.getText().toString().trim();

        String middle =
                middleName.getText().toString().trim();

        String last =
                lastName.getText().toString().trim();

        String mobileText =
                mobile.getText().toString().trim();

        String emailText =
                email.getText().toString().trim();

        String passwordText =
                password.getText().toString().trim();

        boolean isValid = true;

        if (first.isEmpty()
                || !first.matches("^[a-zA-Z]+$")) {

            firstName.setError(
                    "Only letters allowed (no spaces)"
            );

            isValid = false;
        }

        if (middle.isEmpty()
                || !middle.matches("^[a-zA-Z]+$")) {

            middleName.setError(
                    "Middle name is required (letters only)"
            );

            isValid = false;
        }

        if (last.isEmpty()
                || !last.matches("^[a-zA-Z]+$")) {

            lastName.setError(
                    "Only letters allowed (no spaces)"
            );

            isValid = false;
        }

        if (!mobileText.matches("\\d{10}")) {

            mobile.setError(
                    "Enter valid 10-digit mobile number"
            );

            isValid = false;
        }

        if (emailText.isEmpty()
                || !Patterns.EMAIL_ADDRESS
                .matcher(emailText)
                .matches()) {

            email.setError(
                    "Enter valid email"
            );

            isValid = false;
        }

        if (!isGoogleUser
                && passwordText.length() < 6) {

            password.setError(
                    "Password must be at least 6 characters"
            );

            isValid = false;
        }

        if (!isValid) return;

        Toast.makeText(
                this,
                "Creating account...",
                Toast.LENGTH_SHORT
        ).show();

        // GOOGLE USER
        if (isGoogleUser) {

            FirebaseUser user =
                    auth.getCurrentUser();

            if (user == null) {

                Toast.makeText(
                        this,
                        "Session expired. Please login again.",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            String uid =
                    user.getUid();

            Map<String, Object> userData =
                    new HashMap<>();

            userData.put("firstName", first);

            userData.put("middleName", middle);

            userData.put("lastName", last);

            userData.put("mobile", mobileText);

            userData.put("email", emailText);

            userData.put("accountStatus", "pending");

            // ROLE FIELD
            userData.put("role", "farmer");

            db.collection("users")
                    .document(uid)
                    .set(userData)
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(
                                this,
                                "Account created! Waiting for approval.",
                                Toast.LENGTH_LONG
                        ).show();

                        startActivity(
                                new Intent(
                                        this,
                                        WaitingApprovalActivity.class
                                )
                        );

                        finish();

                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(
                                this,
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });

        } else {

            // EMAIL/PASSWORD USER
            auth.createUserWithEmailAndPassword(
                            emailText,
                            passwordText
                    )
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            FirebaseUser user =
                                    auth.getCurrentUser();

                            if (user == null) {

                                Toast.makeText(
                                        this,
                                        "Unexpected error occurred",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            String uid =
                                    user.getUid();

                            Map<String, Object> userData =
                                    new HashMap<>();

                            userData.put("firstName", first);

                            userData.put("middleName", middle);

                            userData.put("lastName", last);

                            userData.put("mobile", mobileText);

                            userData.put("email", emailText);

                            userData.put("accountStatus", "pending");

                            // ROLE FIELD
                            userData.put("role", "farmer");

                            db.collection("users")
                                    .document(uid)
                                    .set(userData)
                                    .addOnSuccessListener(unused -> {

                                        user.sendEmailVerification();

                                        Toast.makeText(
                                                this,
                                                "Account created! Verify your email.",
                                                Toast.LENGTH_LONG
                                        ).show();

                                        startActivity(
                                                new Intent(
                                                        this,
                                                        VerifyEmailActivity.class
                                                )
                                        );

                                        finish();

                                    })
                                    .addOnFailureListener(e -> {

                                        Toast.makeText(
                                                this,
                                                "Firestore Error: "
                                                        + e.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    });

                        } else {

                            if (task.getException()
                                    instanceof FirebaseAuthUserCollisionException) {

                                handleExistingAccount(emailText);

                            } else {

                                String error =
                                        task.getException() != null
                                                ? task.getException().getMessage()
                                                : "Signup failed";

                                Toast.makeText(
                                        this,
                                        error,
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                    });
        }
    }

    // EXISTING ACCOUNT HANDLER
    private void handleExistingAccount(String email) {

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(query -> {

                    if (query.isEmpty()) {

                        showBasicDialog(email);

                        return;
                    }

                    String status =
                            query.getDocuments()
                                    .get(0)
                                    .getString("accountStatus");

                    if ("pending".equals(status)) {

                        new AlertDialog.Builder(this)
                                .setTitle("Account Pending Verification")
                                .setMessage(
                                        "This account is not verified yet.\n\nVerify your email or reset password."
                                )
                                .setPositiveButton(
                                        "Verify Email",
                                        (d, w) -> {

                                            startActivity(
                                                    new Intent(
                                                            this,
                                                            VerifyEmailActivity.class
                                                    )
                                            );
                                        }
                                )
                                .setNeutralButton(
                                        "Reset Password",
                                        (d, w) -> {

                                            Intent intent =
                                                    new Intent(
                                                            this,
                                                            ForgotPasswordActivity.class
                                                    );

                                            intent.putExtra(
                                                    "email",
                                                    email
                                            );

                                            startActivity(intent);
                                        }
                                )
                                .setNegativeButton(
                                        "Back",
                                        null
                                )
                                .show();

                    } else if ("approved".equals(status)) {

                        new AlertDialog.Builder(this)
                                .setTitle("Account Already Exists")
                                .setMessage(
                                        "This account is already active.\n\nPlease login or reset password."
                                )
                                .setPositiveButton(
                                        "Go to Login",
                                        (d, w) -> finish()
                                )
                                .setNeutralButton(
                                        "Reset Password",
                                        (d, w) -> {

                                            Intent intent =
                                                    new Intent(
                                                            this,
                                                            ForgotPasswordActivity.class
                                                    );

                                            intent.putExtra(
                                                    "email",
                                                    email
                                            );

                                            startActivity(intent);
                                        }
                                )
                                .setNegativeButton(
                                        "Cancel",
                                        null
                                )
                                .show();

                    } else {

                        showBasicDialog(email);
                    }
                })
                .addOnFailureListener(
                        e -> showBasicDialog(email)
                );
    }

    private void showBasicDialog(String email) {

        new AlertDialog.Builder(this)
                .setTitle("Account Exists")
                .setMessage(
                        "This email is already registered."
                )
                .setPositiveButton(
                        "Login",
                        (d, w) -> finish()
                )
                .setNeutralButton(
                        "Reset Password",
                        (d, w) -> {

                            Intent intent =
                                    new Intent(
                                            this,
                                            ForgotPasswordActivity.class
                                    );

                            intent.putExtra(
                                    "email",
                                    email
                            );

                            startActivity(intent);
                        }
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }
}