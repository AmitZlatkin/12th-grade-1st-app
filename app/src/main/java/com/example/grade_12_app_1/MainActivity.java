package com.example.grade_12_app_1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;

    ActivityResultLauncher activityResultLauncher;
    ImageView DogImage;
    Bitmap bitmap;
    Map<String, Object> dbNewData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        dbNewData= new HashMap<>();
        DogImage = findViewById(R.id.DogImage);

        AddCameraListener();
    }

    private void AddCameraListener()
    {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback() {
            @Override
            public void onActivityResult(Object result) {
                ActivityResult res = (ActivityResult)result;
                if (res.getResultCode() == RESULT_OK && res.getData() != null) {
                    bitmap = (Bitmap) res.getData().getExtras().get("data");
                    DogImage.setImageBitmap(bitmap);
                }
            }
        });
    }


    public void Save(View view) {
        String dogName = ((EditText)findViewById(R.id.editDogName)).getText().toString();
        String dogColor = ((EditText)findViewById(R.id.editDogColor)).getText().toString();

        if (dogName.isEmpty() || dogColor.isEmpty()) {
            SetStatus("Please type dog's name + color");
        }

        dbNewData.put("DogName", dogName);
        dbNewData.put("DogColor", dogColor);

        if(bitmap != null) {
            UploadImage(dogName);
        } else {
            dbNewData.put("DogPhoto", "");
            AddNewDocument("Dogs", dbNewData);
        }
    }

    private void UploadImage(String dogName) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();


        StorageReference storageRef;
        storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference ref = storageRef.child( "dogImages/"+dogName+".jpg");
        UploadTask uploadTask = ref.putBytes(data);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                SetStatus("Failed:"+e.getMessage());
            }
        })
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        dbNewData.put("Uri", uri.toString());
                        AddNewDocument("Dogs", dbNewData);
                    }
                });
                SetStatus("Image uploaded successfully");
            }
        });

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

    public void OpenCamera(View view) {
        Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLauncher.launch(iCamera);

    }
}