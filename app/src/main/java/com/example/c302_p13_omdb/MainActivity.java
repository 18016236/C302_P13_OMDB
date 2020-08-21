package com.example.c302_p13_omdb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.loopj.android.http.*;
import cz.msebera.android.httpclient.*;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Movie> list;
    private MovieAdapter adapter;
    private FirebaseFirestore db;
    private CollectionReference colRef;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listViewMovies);
        list = new ArrayList<Movie>();

		//TODO: retrieve all documents from the "movies" collection in Firestore (realtime)
		//populate the movie objects into the ListView

    }

    @Override
    protected void onResume() {
        super.onResume();
        db = FirebaseFirestore.getInstance();
        colRef = db.collection("movies");
        //TODO: retrieve all documents from the "movies" collection in Firestore (realtime)
        //populate the movie objects into the ListView
        colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                list.clear();
                for (QueryDocumentSnapshot doc : value) {
                    if (doc.get("title") != null) {
                        Movie movie = doc.toObject(Movie.class);
                        Log.d("Doc id",doc.getId());
                        Movie movie1 = new Movie(movie.getTitle(),
                                movie.getRating(),
                                movie.getReleased(),
                                movie.getRuntime(),
                                movie.getGenre(),
                                movie.getActors(),
                                movie.getPlot(),
                                movie.getLanguage(),
                                movie.getPoster());

                        movie1.setMovieId(doc.getId());
                        list.add(movie1);
                        adapter = new MovieAdapter(getApplicationContext(), R.layout.movie_row, list);
                        listView.setAdapter(adapter);
                    }
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Movie selectedContact = list.get(position);
                Intent i = new Intent(getBaseContext(), ViewMovieDetailsActivity.class);
                i.putExtra("movie_id", selectedContact.getMovieId());
                startActivity(i);

            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_add) {
            Intent intent = new Intent(getApplicationContext(), CreateMovieActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}