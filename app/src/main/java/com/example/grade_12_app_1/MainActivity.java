package com.example.grade_12_app_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
    }

    public void Save(View view) {
        String dogName = ((EditText)findViewById(R.id.editDogName)).getText().toString();
        String dogColor = ((EditText)findViewById(R.id.editDogColor)).getText().toString();

        if (dogName.isEmpty() || dogColor.isEmpty()) {
            SetStatus("Please type dog's name + color");
        }

        Map<String, Object> dbNewData = new HashMap<>();
        dbNewData.put("DogName", dogName);
        dbNewData.put("DogColor", dogColor);

        AddNewDocument("Dogs", dbNewData);
    }

    private void AddNewDocument(String collection, Map<String, Object> newData) {

        /*
         * How the function works:
         *     Take the relevant collection - db.collection(collection)
         *     Add the new data.
         *     Add an OnSuccess listener which prints success message.
         *     Add an OnFailure listener which prints failure message.
        */

        db.collection(collection)
            .add(newData)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    SetStatus("DocumentSnapshot successfully written!");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    SetStatus( "Error writing document"+e.getMessage());
                }
            });
    }

    private void SetStatus(String s) {
        ((TextView)findViewById(R.id.statusTextView)).setText(s);
    }

    public void GoToList(View view) {
        Intent i = new Intent(this, ListActivity.class);
        startActivity(i);
    }
}