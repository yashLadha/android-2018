package com.example.yash.cerebro_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.yash.cerebro_android.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ProfileInfo extends AppCompatActivity {

    private static final String TAG = "ProfileInfo";
    private EditText name;
    private EditText number;
    private Button submit;
    private Context mContext;
    private ProgressBar mProgress;

    private FirebaseUser mCurrentUser;
    private String userID;
    private String userEmail;
    private DatabaseReference mDatabase;
    private DatabaseReference userRef;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onStart() {
        super.onStart();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mCurrentUser != null) {
            userID = mCurrentUser.getUid();
            checkExistence();
            userEmail = mCurrentUser.getEmail();
        }
    }

    private void updateUI() {
        Intent intent = new Intent(this, Index.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        userRef = mDatabase.child("Users");
        mContext = getApplicationContext();
        sharedPref = mContext.getSharedPreferences(getString(R.string.userPrefKey), Context.MODE_PRIVATE);
        name = findViewById(R.id.et_full_name);
        number = findViewById(R.id.et_number);
        submit = findViewById(R.id.btn_submit);
        mProgress = findViewById(R.id.profile_progress);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = name.getText().toString();
                String phoneNumber = number.getText().toString();

                if (phoneNumber.length() == 0) {
                    Toast.makeText(mContext, "Phone Number is not set.", Toast.LENGTH_SHORT).show();
                }
                if (fullName.trim().length() == 0) {
                    Toast.makeText(mContext, "Please enter valid name.", Toast.LENGTH_SHORT).show();
                }
                if (phoneNumber.length() == 10 && fullName.trim().length() > 4) {
                    mProgress.setVisibility(View.VISIBLE);
                    User user = new User(userID, userEmail, fullName, phoneNumber);
                    userRef.child(userID).setValue(user)
                            .addOnCompleteListener(ProfileInfo.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User push succeed");

                                        // Save the current user data in SharedPreferences
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Moves the current Thread into the background
                                                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                                                editor = sharedPref.edit();
                                                editor.putString(getString(R.string.userKey), userID);
                                                editor.commit();
                                            }
                                        }).start();

                                        mProgress.setVisibility(View.GONE);
                                        updateUI();
                                    } else {
                                        Log.e(TAG, "User push failed.");
                                    }
                                }
                            });
                }
            }
        });
    }

    private void checkExistence() {
        String id = sharedPref.getString(getString(R.string.userKey), "NULL");
        Log.d("checkExistence", id);
        if (Objects.equals(id, userID)) {
            updateUI();
        }
    }
}
