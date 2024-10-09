package com.example.grade_12_app_1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DetailsActivity extends AppCompatActivity {

    FirebaseFirestore db;
    String dogName;
    ImageView dogPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        dogName = getIntent().getStringExtra("DogName");

        dogPhoto = findViewById(R.id.DogImage);

        db = FirebaseFirestore.getInstance();
        BuildQuery(dogName);
    }



    private void SetStatus(String msg) {
        ((TextView)findViewById(R.id.dtlStatus)).setText(msg);
    }


    private void BuildQuery(String dogName) {
        SetStatus("Reading Firebase:");
        db.collection("Dogs")
                .whereEqualTo("DogName",dogName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            SetStatus("Error: " + task.getException());
                            return;
                        }
                        GetDogDetails(task);
                    }
                });
    }


    private void GetDogDetails(Task<QuerySnapshot> task) {
        for (QueryDocumentSnapshot document : task.getResult()) {
            if (document.getMetadata().isFromCache()) {
                SetStatus("The Data is from cache. sorry");
                return;
            }
            DisplayDetails(document);
            return;
        }
        SetStatus("Can not find dog :(");
    }

    private void DisplayDetails(QueryDocumentSnapshot document) {
        ((TextView)findViewById(R.id.dtlDogName)).setText(dogName);
        String color = document.get("DogColor").toString();
        ((TextView)findViewById(R.id.dtlDogColor)).setText(color);

        String photoUri = document.get("Uri").toString();
        DownloadImage(photoUri);


        SetStatus("Found it!");
    }


    private void DownloadImage(String url) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream inp = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(inp);
            bm = BitmapFactory.decodeStream(bis);
            dogPhoto.setImageBitmap(bm);
            bis.close();
            inp.close();
        } catch (IOException e) {
            SetStatus("Error getting bitmap: "+e.getMessage());
        }
    }

}