package com.example.c302_p13_omdb;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.loopj.android.http.*;
import cz.msebera.android.httpclient.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class CreateMovieActivity extends AppCompatActivity {

    private EditText etTitle, etRated, etReleased, etRuntime, etGenre, etActors, etPlot, etLanguage, etPoster;
    private Button btnCreate, btnSearch;
    private ImageButton btnCamera;
    private String apikey,loginID;
    private static final String TAG = "CreateMovieActivity";


    private FirebaseFirestore db;
    private CollectionReference colRef;
    private DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_movie);

        etTitle = findViewById(R.id.etTitle);
        etRated = findViewById(R.id.etRated);
        etReleased = findViewById(R.id.etReleased);
        etRuntime = findViewById(R.id.etRuntime);
        etGenre = findViewById(R.id.etGenre);
        etActors = findViewById(R.id.etActors);
        etPlot = findViewById(R.id.etPlot);
        etLanguage = findViewById(R.id.etLanguage);
        etPoster = findViewById(R.id.etPoster);
        btnCreate = findViewById(R.id.btnCreate);
        btnSearch = findViewById(R.id.btnSearch);
        btnCamera = findViewById(R.id.btnCamera);


        //TODO: Retrieve the apikey from SharedPreferences
		//If apikey is empty, redirect back to LoginActivity

       // SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        //loginID = pref.getString("loginID","");
        //apikey = pref.getString("apiKey","");

        // TODO: if loginId and apikey is empty, go back to LoginActivity
        //if (loginID.equalsIgnoreCase("") || apikey.equalsIgnoreCase("")){
          //  Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            //startActivity(intent);




        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCreateOnClick(v);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSearchOnClick(v);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCameraOnClick(v);
            }
        });

    }//end onCreate

	//TODO: extract the fields and populate into a new instance of Movie class
	// Add the new movie into Firestore
    private void btnCreateOnClick(View v) {

        //TODO: Task 3: Retrieve name and age from EditText and instantiate a new Student object
        String title = etTitle.getText().toString();
        String rated = etRated.getText().toString();
        String released = etReleased.getText().toString();
        String runtime = etRuntime.getText().toString();
        String Genre = etGenre.getText().toString();
        String Actors = etActors.getText().toString();
        String Plot = etPlot.getText().toString();
        String Language = etLanguage.getText().toString();
        String Poster = etPoster.getText().toString();

        Movie student = new Movie();
        student.setTitle(title);
        student.setRating(rated);
        student.setReleased(released);
        student.setRuntime(runtime);
        student.setGenre(Genre);
        student.setActors(Actors);
        student.setPlot(Plot);
        student.setLanguage(Language);
        student.setPoster(Poster);


        //TODO: Task 4: Add student to database and go back to main screen
        db.collection("movies")
                .add(student)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG,"DocumentSnapshot written with ID: "+ documentReference.getId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Error adding document",e);
            }
        });


        finish();
		
    }

	//TODO: Call www.omdbapi.com passing the title and apikey as parameters
	// extract from JSON response and set into the edit fields
    private void btnSearchOnClick(View v) {

    }


    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void btnCameraOnClick(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //TODO: feed imageBitmap into FirebaseVisionImage for text recognizing

            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
            FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            Task<FirebaseVisionText> task = textRecognizer.processImage(image);
            task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    etTitle.setText("");

                    // Task completed successfully
                    // ...
                    for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                        String blockText = block.getText();
                        etTitle.setText(blockText);
                    }
                }
            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    // ...
                                }
                            });

        }
    }
}