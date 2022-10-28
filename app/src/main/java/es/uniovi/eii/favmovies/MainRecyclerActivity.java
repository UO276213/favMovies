package es.uniovi.eii.favmovies;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import es.uniovi.eii.favmovies.database.ActorsDataSource;
import es.uniovi.eii.favmovies.database.PeliculasDataSource;
import es.uniovi.eii.favmovies.database.RepartoPeliculaDataSource;
import es.uniovi.eii.favmovies.modelos.Actor;
import es.uniovi.eii.favmovies.modelos.Categoria;
import es.uniovi.eii.favmovies.modelos.Pelicula;
import es.uniovi.eii.favmovies.modelos.RepartoPelicula;
import es.uniovi.eii.favmovies.util.Conexion;

public class MainRecyclerActivity extends AppCompatActivity {

    //SharedPreference de la MainRecycler
    SharedPreferences sharedPreferencesMainRecycler;

    public static final String SELECTED_FILM = "selected_film";
    public static final String EDITION_MODE = "edition_mode";
    public static int NEW_FILM_CODE = 1;
    private List<Pelicula> filmsList;
    private RecyclerView listFilmView;

    public static String categoryFilter = null;
    private SharedPreferences sharedPreferences;
    private RecyclerView filmsListView;
    private PeliculasDataSource filmsDataSoure;

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
        filmsList = readFilmsFromFile("peliculas.csv");

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

        loadFilms();
        cargarReparto();
        cargarRepartoPelicula();

        Log.d("FILTRO_CATEGORIA: ", " " + categoryFilter);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        categoryFilter = sharedPreferences.getString("keyCategory", null);

        //Cargamos en listaPeli las películas con o sin filtro.
        PeliculasDataSource peliculasDataSource = new PeliculasDataSource(getApplicationContext());
        //Abrir
        peliculasDataSource.open();
        if(categoryFilter == null || categoryFilter.equals("")) {
            filmsList = peliculasDataSource.getAllValorations();
        } else //A través de este método introducimos el filtro pero mediante lenguaje SQL en la extracción de la base de datos.
            filmsList = peliculasDataSource.getFilteredValorations(categoryFilter);
        //Cerrar
        peliculasDataSource.close();


        /*
        Con la lista de películas iniciamos el RecyclerView
         */
        filmsListView = (RecyclerView) findViewById(R.id.recyclerView);
        filmsListView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        filmsListView.setLayoutManager(layoutManager);
        ListaPeliculasAdapter lpAdapter = new ListaPeliculasAdapter(filmsList,
                new ListaPeliculasAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Pelicula peli) {
                        clickOnItem(peli);
                    }
                });

        filmsListView.setAdapter(lpAdapter);


    }

    private void loadFilms(String categoryFilter) {

        List<Pelicula> films = readFilmsFromFile("lista_peliculas_url_utf8.csv");
        List<Pelicula> filtredFilms = new ArrayList<>(films.size());

        filmsDataSoure.open();

        for (Pelicula film : films) {
            if (film.getCategory().getNombre().equals(categoryFilter)){
                filtredFilms.add(film);
                filmsDataSoure.createpelicula(film);
            }
        }

        filmsDataSoure.close();

        filmsList = filtredFilms;

        ListaPeliculasAdapter listFilmAdapter = new ListaPeliculasAdapter(filmsList, new ListaPeliculasAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Pelicula film) {
                clickOnItem(film);
            }
        });


        listFilmView.setAdapter(listFilmAdapter);
    }


    protected void cargarReparto() {
        Actor actor = null;
        InputStream file = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;


        try {
            file = getAssets().open("reparto.csv");
            reader = new InputStreamReader(file);
            bufferedReader = new BufferedReader(reader);


            String line = null;

            //Leemos la primera línea que es encabezado y por tanto no nos aporta información útil.
            bufferedReader.readLine();

            //A partir de aquí leemos a partir de la segunda línea.
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(";");
                if (data != null) {
                    if (data.length==4) {
                        actor = new Actor(Integer.parseInt(data[0]), data[1], data[2], data[3]);
                    }

                    //Metemos la película en la base de datos:
                    ActorsDataSource actoresDataSource = new ActorsDataSource(getApplicationContext());
                    actoresDataSource.open();
                    actoresDataSource.createactor(actor);
                    actoresDataSource.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<Pelicula> readFilmsFromFile(String fileName) {
        List<Pelicula> films = new ArrayList<>();

        try {
            InputStream file = getAssets().open(fileName);
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

                    PeliculasDataSource filmDataSource = new PeliculasDataSource(getApplicationContext());

                    filmDataSource.open();
                    filmDataSource.createpelicula(film);
                    films.add(film);
                    filmDataSource.close();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return films;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recycled, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.recyled_settings) {
            Intent settingActivity = new Intent(MainRecyclerActivity.this, SettingsActivity.class);
            startActivity(settingActivity);
        }

        return super.onOptionsItemSelected(item);
    }


    protected void cargarRepartoPelicula() {
        RepartoPelicula rel = null;
        InputStream file = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;


        try {
            file = getAssets().open("peliculas-reparto.csv");
            reader = new InputStreamReader(file);
            bufferedReader = new BufferedReader(reader);
            String line = null;

            //Leemos la primera línea que es encabezado y por tanto no nos aporta información útil.
            bufferedReader.readLine();

            //A partir de aquí leemos a partir de la segunda línea.
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(";");
                if (data != null) {
                    if (data.length==3) {
                        rel = new RepartoPelicula(Integer.parseInt(data[0]), Integer.parseInt(data[1]), data[2]);
                    }


                    //Metemos la película en la base de datos:
                    RepartoPeliculaDataSource relDataSource = new RepartoPeliculaDataSource(getApplicationContext());
                    relDataSource.open();
                    relDataSource.createrepartoPelicula(rel);
                    relDataSource.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}