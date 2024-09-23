package com.example.grade_12_app_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    FirebaseFirestore db;
    ListView lvDogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        db = FirebaseFirestore.getInstance();
        lvDogs = findViewById(R.id.lvDogs);

        InitListListener();
        GetFromDataSet();
    }

    private void SetStatus(String s) {
        ((TextView)findViewById(R.id.tvStatus)).setText(s);
    }

    private void InitListListener(){
        SetStatus("List Listener");
        lvDogs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dog = lvDogs.getItemAtPosition(position).toString();
                String name = dog.split("\n")[0];
                Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
                i.putExtra("DogName", name);
                startActivity(i);
            }
        });
    }


    private void GetFromDataSet()
    {
        SetStatus("Reading Firebase:");
        db.collection("Dogs")
                //.whereEqualTo("DogColor", "Blue")
                //.orderBy("DogName")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            SetStatus("Error: " + task.getException());
                            return;
                        }
                        DataToAdapter(task);
                    }
                });
    }
    private void DataToAdapter(Task<QuerySnapshot> task) {
        int cnt = 0;
        String dog;
        ArrayList<String> listItem = new ArrayList<>();


        for (QueryDocumentSnapshot document : task.getResult()) {
            if (document.getMetadata().isFromCache()) {
                SetStatus("The Data is from cache. sorry");
                return;
            }
            cnt++;
            dog = document.get("DogName") +"\n"+ document.get("DogColor");
            listItem.add(dog);
        }
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItem);
        lvDogs.setAdapter(adapter);
        SetStatus("Records found: " + cnt);
    }

}