package es.uniovi.eii.favmovies;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import es.uniovi.eii.favmovies.modelos.Categoria;
import es.uniovi.eii.favmovies.modelos.Pelicula;

public class MainRecyclerActivity extends AppCompatActivity {

    public static int NEW_FILM_CODE = 1;

    public static final String SELECTED_FILM = "selected_film";
    public static final String EDITION_MODE = "edition_mode";
    private List<Pelicula> filmsList;
    private RecyclerView listFilmView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recycler);

        fillFilmsList();

        FloatingActionButton addNewFilmBtn = findViewById(R.id.new_film_btn);
        addNewFilmBtn.setOnClickListener(v -> addNewFilm());



        listFilmView = findViewById(R.id.recyclerView);
        listFilmView.setHasFixedSize(true);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        listFilmView.setLayoutManager(layoutManager);

        ListaPeliculasAdapter listFilmAdapter = new ListaPeliculasAdapter(filmsList, new ListaPeliculasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Pelicula film) {
                clickOnItem(film);
            }
        });

        listFilmView.setAdapter(listFilmAdapter);
    }

    private void clickOnItem(Pelicula film) {
        Intent mainActivity = new Intent(MainRecyclerActivity.this, MainActivity.class);

        mainActivity.putExtra(SELECTED_FILM, film);
        mainActivity.putExtra(EDITION_MODE, false);

        startActivity(mainActivity);
    }

    private void fillFilmsList() {
        filmsList = new ArrayList<>();

        Categoria actionCategory = new Categoria("Acción", "Pelis de acción");

        Pelicula film = new Pelicula("Tenet", "Película que juega con el Tiempo", actionCategory, "165","10/05/2019");
        Pelicula film2 = new Pelicula("Avatar", "Película de bichos azules", actionCategory, "201","20/07/2011");

        filmsList.add(film);
        filmsList.add(film2);
    }

    public void addNewFilm() {

        Intent mainActivity = new Intent(MainRecyclerActivity.this, MainActivity.class);

        startActivityForResult(mainActivity, NEW_FILM_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_FILM_CODE) {
            if (resultCode == RESULT_OK){
                Pelicula film = data.getParcelableExtra(MainActivity.FILM_CREATED);

                //filmsList.add(film);
            }

        }
    }
}