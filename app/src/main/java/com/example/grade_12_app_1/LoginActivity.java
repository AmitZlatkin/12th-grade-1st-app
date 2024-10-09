package com.example.grade_12_app_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = FirebaseFirestore.getInstance();
    }

    public void LogIn(View view) {
        String username = ((EditText)findViewById(R.id.logUsername)).getText().toString();
        String password = ((EditText)findViewById(R.id.logPassword)).getText().toString();
        BuildQuery(username, password);
    }

    private void BuildQuery(String dogName, String dogColor) {
        SetStatus("Reading Firebase:");
        db.collection("Dogs")
            .whereEqualTo("DogName", dogName)
            .whereEqualTo("DogColor", dogColor)
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

                    if(task.getResult().isEmpty()) { //check that such user exists (in our case - such dog)
                        SetStatus("no dog found");
                        return;
                    }

                    //go to details (I choose so)
                    //theoretically we can add the collection of users and then send them to the list
                    SetStatus("task succeeded");
                    Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
                    i.putExtra("DogName", dogName);
                    startActivity(i);
                }
            });
    }

    private void SetStatus(String s) {
        ((TextView)findViewById(R.id.statusTV)).setText(s);
    }
}