package es.uniovi.eii.favmovies;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import es.uniovi.eii.favmovies.modelos.Categoria;
import es.uniovi.eii.favmovies.modelos.Pelicula;

public class MainRecyclerActivity extends AppCompatActivity {

    public static final String SELECTED_FILM = "selected_film";
    public static final String EDITION_MODE = "edition_mode";
    public static int NEW_FILM_CODE = 1;
    private List<Pelicula> filmsList;
    private RecyclerView listFilmView;

    public static String categoryFilter = null;
    private SharedPreferences sharedPreferences;
    private RecyclerView filmsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recycler);

        loadFilms();

//        FloatingActionButton addNewFilmBtn = findViewById(R.id.new_film_btn);
//        addNewFilmBtn.setOnClickListener(v -> addNewFilm());


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
        Intent mainActivity = new Intent(MainRecyclerActivity.this, ShowMovie.class);

        Log.d("ESPAPDA", film.getCoverUrl());
        mainActivity.putExtra(SELECTED_FILM, film);
//        mainActivity.putExtra(EDITION_MODE, false);


        startActivity(mainActivity, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    private void loadFilms() {
        filmsList = new ArrayList<>();

        try {
            InputStream file = getAssets().open("lista_peliculas_url_utf8.csv");
            BufferedReader br = new BufferedReader(new InputStreamReader(file));

            while (br.ready()) {
                String[] data = br.readLine().split(";");
                if (data != null && data.length >= 5) {
                    Categoria category = new Categoria(data[2], "");
                    Pelicula film;
                    if (data.length == 8)
                        film = new Pelicula(data[0], data[1], category, data[3], data[4], data[5], data[6], data[7]);
                    else
                        film = new Pelicula(data[0], data[1], category, data[3], data[4]);

                    filmsList.add(film);
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Categoria actionCategory = new Categoria("Acción", "Pelis de acción");
//
//        Pelicula film = new Pelicula("Tenet", "Película que juega con el Tiempo", actionCategory, "165","10/05/2019");
//        Pelicula film2 = new Pelicula("Avatar", "Película de bichos azules", actionCategory, "201","20/07/2011");
//
//        filmsList.add(film);
//        filmsList.add(film2);
    }

    public void addNewFilm() {

        Intent mainActivity = new Intent(MainRecyclerActivity.this, NewMovie.class);

        startActivityForResult(mainActivity, NEW_FILM_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_FILM_CODE) {
            if (resultCode == RESULT_OK) {
                Pelicula film = data.getParcelableExtra(NewMovie.FILM_CREATED);

                //filmsList.add(film);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("FILTRO_CATEGORIA: ", " " + categoryFilter);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        MainRecyclerActivity.categoryFilter = sharedPreferences.getString("keyCategory", "");

        if (categoryFilter == "")
            loadFilms();
        else
            loadFilms(categoryFilter);

        filmsListView = findViewById(R.id.recyclerView);
        filmsListView.setHasFixedSize(true);

//TODO
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager();
    }

    private void loadFilms(String categoryFilter) {
    }


}