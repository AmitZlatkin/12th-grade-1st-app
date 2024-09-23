package com.example.grade_12_app_1;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DetailsActivity extends AppCompatActivity {

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        String dogName = getIntent().getStringExtra("DogName");
        ((TextView)findViewById(R.id.dtlDogName)).setText(dogName);

        db = FirebaseFirestore.getInstance();
        GetDataFromDB(dogName);
    }

    private void SetStatus(String msg) {
        ((TextView)findViewById(R.id.dtlStatus)).setText(msg);
    }


    private void GetDataFromDB(String dogName) {
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
            String color = document.get("DogColor").toString();
            ((TextView)findViewById(R.id.dtlDogColor)).setText(color);

            SetStatus("Found it!");
            return;
        }
        SetStatus("Can not find dog :(");
    }

/*
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
            photo.setImageBitmap(bm);
            bis.close();
            inp.close();
        } catch (IOException e) {
            SetStatus("Error getting bitmap: "+e.getMessage());
        }
    }
*/
}