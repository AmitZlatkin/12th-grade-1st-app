package com.example.grade_12_app_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.ReturnThis;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    FirebaseFirestore db;
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = FirebaseFirestore.getInstance();
    }

    public void LogIn(View view) {
        username = ((EditText)findViewById(R.id.logUsername)).getText().toString();
        password = ((EditText)findViewById(R.id.logPassword)).getText().toString();
        try {
            BuildQuery();
        } catch (NoSuchAlgorithmException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void BuildQuery() throws NoSuchAlgorithmException {
        SetStatus("Reading Firebase:");
        db.collection("Users")
            .whereEqualTo("Username", username)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (!task.isSuccessful()) { // check success of task
                        SetStatus("task failed");
                        return;
                    }

                    for (QueryDocumentSnapshot document : task.getResult()) { //check cache
                        if (document.getMetadata().isFromCache()) {
                            SetStatus("The Data is from cache. sorry");
                            return;
                        }
                    }

                    if(task.getResult().size() != 1) { //check that such user exists and is unique
                        SetStatus("wrong username/password");
                        return;
                    }

                    if(!validPassword(task)) { //check password
                        SetStatus("wrong username/password");
                        return;
                    }

                    //go to list
                    SetStatus("task succeeded");
                    Intent i = new Intent(getApplicationContext(), ListActivity.class);
                    startActivity(i);
                }
            });
    }

    private boolean validPassword(Task<QuerySnapshot> task) {
        for (QueryDocumentSnapshot document : task.getResult()) {
            if(document.get("Sha256_password") == null) {
                Toast.makeText(getApplicationContext(), "enc_password not found", Toast.LENGTH_LONG).show();
                return false;
            }
            if(document.get("Salt") == null) {
                Toast.makeText(getApplicationContext(), "salt not found", Toast.LENGTH_LONG).show();
                return false;
            }

            String password_enc = document.get("Sha256_password").toString();
            String salt = document.get("Salt").toString();

            String logged_pass = "";

            try {
                logged_pass = getHexString_SHA256(password + salt);
            } catch (NoSuchAlgorithmException e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }

            if (!password_enc.equals(logged_pass)) {
                return false;
            }
        }

        return true;
    }

    private String getHexString_SHA256(String input) throws NoSuchAlgorithmException
    {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called to calculate message digest of an input
        byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));

        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 64)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    private void SetStatus(String s) {
        ((TextView)findViewById(R.id.statusTV)).setText(s);
    }
}