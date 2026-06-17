package com.example.milkcollection.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.milkcollection.R;
import com.example.milkcollection.admin.AdminDashboardActivity;
import com.example.milkcollection.core.LoadingActivity;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    EditText emailEdit, passwordEdit;
    Button loginBtn, googleBtn;
    TextView signupText, forgotPassword;

    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 100;

    private boolean isNavigating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEdit = findViewById(R.id.emailEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        loginBtn = findViewById(R.id.loginBtn);
        googleBtn = findViewById(R.id.googleBtn);
        signupText = findViewById(R.id.signupText);
        forgotPassword = findViewById(R.id.forgotPassword);

        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInClient.signOut();

        loginBtn.setOnClickListener(v -> loginUser());

        signupText.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class)));

        forgotPassword.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));

        googleBtn.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    private void loginUser() {

        String email = emailEdit.getText().toString().trim();
        String pass = passwordEdit.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdit.setError("Enter valid email");
            return;
        }

        if (pass.isEmpty()) {
            passwordEdit.setError("Enter password");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("admins")
                .document(email)
                .get()
                .addOnSuccessListener(adminDoc -> {

                    if (adminDoc.exists()) {

                        auth.signInWithEmailAndPassword(email, pass)
                                .addOnSuccessListener(authResult -> {

                                    if (isNavigating) return;
                                    isNavigating = true;

                                    startLoading();

                                })
                                .addOnFailureListener(e -> showResetPasswordDialog(email));

                    } else {

                        db.collection("users")
                                .whereEqualTo("email", email)
                                .get()
                                .addOnSuccessListener(query -> {

                                    if (query.isEmpty()) {
                                        showNoAccountDialog();
                                        return;
                                    }

                                    auth.signInWithEmailAndPassword(email, pass)
                                            .addOnCompleteListener(task -> {

                                                if (task.isSuccessful()) {

                                                    if (!auth.getCurrentUser().isEmailVerified()) {

                                                        startActivity(new Intent(this, VerifyEmailActivity.class));
                                                        finish();

                                                    } else {

                                                        if (isNavigating) return;
                                                        isNavigating = true;

                                                        startLoading();
                                                    }

                                                } else {
                                                    showResetPasswordDialog(email);
                                                }
                                            });

                                })
                                .addOnFailureListener(e -> showError("Something went wrong"));
                    }
                });
    }

    private void startLoading() {
        Intent intent = new Intent(this, LoadingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn
                        .getSignedInAccountFromIntent(data)
                        .getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());

            } catch (Exception e) {
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken){

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser user = auth.getCurrentUser();
                        if(user == null) return;

                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        db.collection("admins")
                                .document(user.getEmail())
                                .get()
                                .addOnSuccessListener(adminDoc -> {

                                    if(adminDoc.exists()){

                                        if (isNavigating) return;
                                        isNavigating = true;

                                        startActivity(new Intent(this, AdminDashboardActivity.class));
                                        finish();

                                    } else {

                                        if (isNavigating) return;
                                        isNavigating = true;

                                        startLoading();
                                    }
                                });

                    } else {
                        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showResetPasswordDialog(String email) {

        new AlertDialog.Builder(this)
                .setTitle("Login Failed")
                .setMessage("Incorrect password.\n\nYou can reset your password to continue.")
                .setPositiveButton("Reset Password", (d,w) -> {

                    Intent intent = new Intent(this, ForgotPasswordActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);

                })
                .setNegativeButton("Try Again", null)
                .show();
    }

    private void showNoAccountDialog() {

        new AlertDialog.Builder(this)
                .setTitle("No Account Found")
                .setMessage("No account exists with this email.\n\nPlease create a new account.")
                .setPositiveButton("Create Account", (d,w) -> {
                    startActivity(new Intent(this, SignupActivity.class));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showError(String msg) {

        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }
}