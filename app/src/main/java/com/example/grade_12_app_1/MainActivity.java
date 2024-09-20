package com.example.grade_12_app_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    public void onSave(View view) {
        String name = ((EditText)findViewById(R.id.editDog)).getText().toString();
        String color = ((EditText)findViewById(R.id.editColor)).getText().toString();

        if (name.equals("") || color.equals(""))
        {
            SetStatus("Please type dog's name + color");
        }
        Map<String, Object> dbData;

        dbData = new HashMap<>();
        dbData.put("DogName", name);
        dbData.put("DogColor", color);
        AddNew("Dogs", dbData);

    }

    private void AddNew(String collection, Map<String, Object> data) {
        db.collection(collection)
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {                    @Override
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
        ((TextView)findViewById(R.id.textStatus)).setText(s);
    }
}