package com.example.milkcollection.core;

import android.app.Activity;
import android.content.Intent;

import com.example.milkcollection.admin.AdminDashboardActivity;
import com.example.milkcollection.auth.LoginActivity;
import com.example.milkcollection.auth.VerifyEmailActivity;
import com.example.milkcollection.user.BlockedActivity;
import com.example.milkcollection.user.RejectedActivity;
import com.example.milkcollection.user.UserDashboardActivity;
import com.example.milkcollection.user.WaitingApprovalActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class NavigationController {

    private static boolean isNavigating = false;

    public static void goToNextScreen(Activity activity){

        if (isNavigating) return;
        isNavigating = true;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        if(user == null){
            start(activity, new Intent(activity, LoginActivity.class));
            return;
        }

        user.reload().addOnCompleteListener(task -> {

            if(!user.isEmailVerified()){
                start(activity, new Intent(activity, VerifyEmailActivity.class));
                return;
            }

            checkAdmin(activity, user);
        });
    }

    private static void checkAdmin(Activity activity, FirebaseUser user){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("admins")
                .document(user.getEmail())
                .get()
                .addOnSuccessListener(adminDoc -> {

                    if(adminDoc.exists()){
                        start(activity, new Intent(activity, AdminDashboardActivity.class));
                    } else {
                        checkUserStatus(activity);
                    }
                })
                .addOnFailureListener(e -> checkUserStatus(activity));
    }

    private static void checkUserStatus(Activity activity){

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    Intent intent;

                    if(!doc.exists()){
                        intent = new Intent(activity, WaitingApprovalActivity.class);
                    } else {

                        String status = doc.getString("accountStatus");
                        if(status == null) status = "pending";

                        switch(status){
                            case "approved":
                                intent = new Intent(activity, UserDashboardActivity.class);
                                break;
                            case "rejected":
                                intent = new Intent(activity, RejectedActivity.class);
                                break;
                            case "blocked":
                                intent = new Intent(activity, BlockedActivity.class);
                                break;
                            default:
                                intent = new Intent(activity, WaitingApprovalActivity.class);
                                break;
                        }
                    }

                    start(activity, intent);
                });
    }

    // 🔥 FINAL PERFECT FIX
    private static void start(Activity activity, Intent intent){

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        activity.startActivity(intent);

        // remove animation flicker
        activity.overridePendingTransition(0, 0);

        // 🔥 CRITICAL: delay finish by 1 frame
        activity.getWindow().getDecorView().post(() -> {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        });

        isNavigating = false;
    }
}