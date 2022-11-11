package es.uniovi.eii.favmovies;

import static es.uniovi.eii.favmovies.datos.server.ServerDataMapper.convertMovieListToDomain;
import static es.uniovi.eii.favmovies.remote.ApiUtils.API_KEY;
import static es.uniovi.eii.favmovies.remote.ApiUtils.LANGUAGE;

import android.app.ActivityOptions;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import es.uniovi.eii.favmovies.datos.bd.ActorsDataSource;
import es.uniovi.eii.favmovies.datos.bd.PeliculasDataSource;
import es.uniovi.eii.favmovies.datos.bd.RepartoPeliculaDataSource;
import es.uniovi.eii.favmovies.datos.server.MovieDetails;
import es.uniovi.eii.favmovies.datos.server.MovieListResult;
import es.uniovi.eii.favmovies.modelos.Actor;
import es.uniovi.eii.favmovies.modelos.Categoria;
import es.uniovi.eii.favmovies.modelos.Pelicula;
import es.uniovi.eii.favmovies.modelos.RepartoPelicula;
import es.uniovi.eii.favmovies.remote.ApiUtils;
import es.uniovi.eii.favmovies.remote.ThemoviedbApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainRecyclerActivity extends AppCompatActivity {

    //Objetos para las notificaciones
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotificationManager;
    private ProgressBar progressBar;

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
    private int numeroLineasLeidas;
    private float lineasALeer;
    private boolean result;
    private ThemoviedbApi cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recycler);
        progressBar = findViewById(R.id.progressBar);

        //Las tareas asíncronas van a hacer uso de las notificaciones, aquí simplemente las construimos con un nombre y un contenido.
        //Ojo, esta notificación se ha lanzado, pero no se activará hasta que no se indique al builder que ya puede hacerlo
        //Y eso será dentro de la tarea asíncrona.


//        construirNotificacion(getString(R.string.app_name), "Acceso a la BD de peliculas");
//        //Lanzamos la tarea asíncrona en segundo término
//        DownLoadFilesTask task = new DownLoadFilesTask();
//        task.execute();

        listFilmView = findViewById(R.id.recyclerView);
        listFilmView.setHasFixedSize(true);

        cliente = ApiUtils.createThemoviedbApi();
        realizarPeticionPeliculasPopulares(cliente);
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

    /**
     Podríamos simplemente borrar este método. La carga de la base de datos (y el resto de elementos de la interfaz) se realiza ahora en DownLoadFilesTask
     */
    @Override
    protected void onResume(){
        super.onResume();
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

    /**
 * Clase asíncrona interna:  DownLoadFilesTask*/
private class DownLoadFilesTask {


    private boolean result;
    /**
     * Variables necesarias para llevar la cuenta del progreso de la carga.
     * ¡Ojo! La fórmula que vamos a utilizar para calcular el porcentaje leído es:
     * (numeroLineasLeidas/lineasALeer)*100
     * Esa división requiere que el tipo de las variables sea flotante para poder obtener decimales
     * que al multiplicar por 100 nos den el porcentaje.
     *
     * En caso de usar enteros pasaría de 0 a 100 sin los intermedios, llevando una cuenta no válida.
     */
    private float lineasALeer = 0.0f;
    float numeroLineasLeidas = 0.0f;

    public void execute() {
        final Executor EXECUTOR = Executors.newSingleThreadExecutor();
        final Handler HANDLER = new Handler(Looper.getMainLooper());


        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {

                //PreExecute--Interacciona con el UI
                HANDLER.post(new Runnable() {

                    @Override
                    public void run() {

                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress(0);
                        progressBar.setIndeterminate(false);

                        //Inicializamos el lineasALeer, con un repaso a la cantidad de líneas que tienen los ficheros.
                        lineasALeer = (float) (lineasFichero("peliculas.csv"));
                        lineasALeer = (float)(lineasALeer + lineasFichero("peliculas-reparto.csv"));
                        lineasALeer = (float)(lineasALeer + lineasFichero("reparto.csv"));
                    }
                });

                //doInBackground- Tarea en segundo plando
                try {
                    //Cargamos la base de datos.
                    cargarPeliculas();
                    cargarReparto();
                    cargarRepartoPelicula();


                    result=true;
                    //Si la carga no da ningún error inesperado...
                    // mensaje = "Lista de películas actualizada";

                } catch (Exception e) {
                    //Si la carga da algún error
                    //mensaje = "Error en la actualización de la lista de películas";
                    result=false;
                }

                //Lanzamos notificación.
                mNotificationManager.notify(001, mBuilder.build());


                //PostExecute- Interacciona con el UI
                HANDLER.post(new Runnable() {
                    @Override
                    public void run() {


                        //Avisamos que la base de datos se cargó satisfactoriamente (o hubo error, según lo que haya ocurrido)
                        if (result) {
                            cargarView();
                            Toast.makeText(getApplicationContext(), "Lista de películas actualizada", Toast.LENGTH_LONG).show();
                        }
                        else
                            Toast.makeText(getApplicationContext(), "Error en la actualización de la lista de películas", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

            } //Run
        }); //EXECUTOR.execute
    }//execute


    /**
     * Lee lista de películas desde el fichero csv en assets
     * Crea listaPeli como un ArrayList<Pelicula>
     * Y además añade pelis a la tabla de pelis en la BD
     */
    protected void cargarPeliculas() {
        Pelicula peli = null;
        InputStream file = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        PeliculasDataSource peliculasDataSource=null;

        try {
            file = getAssets().open("peliculas.csv");
            reader = new InputStreamReader(file);
            bufferedReader = new BufferedReader(reader);
            peliculasDataSource = new PeliculasDataSource(getApplicationContext());
            peliculasDataSource.open();
            String line = null;

            //Leemos la primera línea que es encabezado y por tanto no nos aporta información útil.
            bufferedReader.readLine();

            numeroLineasLeidas++;
            progressBar.setProgress((int)((numeroLineasLeidas /lineasALeer)*100));



            //A partir de aquí leemos a partir de la segunda línea.
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(";");
                if (data != null && data.length >= 5) { //El segundo condicional se va a cumplir siempre. Podemos quitarlo
                    if (data.length==9) {
                        //Tengamos en cuenta que hemos añadido un id al primer parámetro
                        peli = new Pelicula(Integer.parseInt(data[0]), data[1], data[2], new Categoria(data[3], ""), data[4],
                                data[5], data[6], data[7], data[8]);
                    } else {
                        peli = new Pelicula(Integer.parseInt(data[0]), data[1], data[2], new Categoria(data[3], ""), data[4],
                                data[5], "", "", "");
                    }
                    Log.d("cargarPeliculas", peli.toString());
                    //Metemos la película en la base de datos:

                    peliculasDataSource.createpelicula(peli);
                    numeroLineasLeidas++;
                    progressBar.setProgress((int)((numeroLineasLeidas /lineasALeer)*100));

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                    peliculasDataSource.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    protected void cargarReparto() {
        Actor actor = null;
        InputStream file = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        ActorsDataSource actoresDataSource = null;


        try {
            file = getAssets().open("reparto.csv");
            reader = new InputStreamReader(file);
            bufferedReader = new BufferedReader(reader);
            actoresDataSource = new ActorsDataSource(getApplicationContext());
            actoresDataSource.open();

            String line = null;

            //Leemos la primera línea que es encabezado y por tanto no nos aporta información útil.
            bufferedReader.readLine();

            numeroLineasLeidas++;
            progressBar.setProgress((int) ((numeroLineasLeidas / lineasALeer) * 100));


            //A partir de aquí leemos a partir de la segunda línea.
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(";");
                if (data != null) {
                    if (data.length == 4) {
                        actor = new Actor(Integer.parseInt(data[0]), data[1], data[2], data[3]);
                    }

                    //Metemos la película en la base de datos:

                    actoresDataSource.createactor(actor);
                    numeroLineasLeidas++;
                    progressBar.setProgress((int) ((numeroLineasLeidas / lineasALeer) * 100));


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                    actoresDataSource.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void cargarRepartoPelicula() {
        RepartoPelicula rel = null;
        InputStream file = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        RepartoPeliculaDataSource relDataSource = null;

        try {
            file = getAssets().open("peliculas-reparto.csv");
            reader = new InputStreamReader(file);
            bufferedReader = new BufferedReader(reader);
            String line = null;
            relDataSource = new RepartoPeliculaDataSource(getApplicationContext());
            relDataSource.open();

            //Leemos la primera línea que es encabezado y por tanto no nos aporta información útil.
            bufferedReader.readLine();

            //A partir de aquí leemos a partir de la segunda línea.
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(";");
                if (data != null) {
                    if (data.length == 3) {
                        rel = new RepartoPelicula(Integer.parseInt(data[0]), Integer.parseInt(data[1]), data[2]);
                    }

                    //Metemos la película en la base de datos:
                    relDataSource.createrepartoPelicula(rel);
                    numeroLineasLeidas++;
                    progressBar.setProgress((int) ((numeroLineasLeidas / lineasALeer) * 100));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                    relDataSource.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

    protected int lineasFichero(String nombreFichero) {
        InputStream file = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        int lineas = 0;

        try {
            file = getAssets().open(nombreFichero);
            reader = new InputStreamReader(file);
            bufferedReader = new BufferedReader(reader);

            //Pase rápido mirando el total de líneas,
            // sin perder tiempo de procesamiento en nada más
            //Necesario para el progressbar
            while (bufferedReader.readLine() != null)
                lineas++;

            bufferedReader.close();
        }catch(Exception e){};
        return lineas;
    }

    /***********Métodos de creación de notificaciones*******************
     *
     *
     */
    private void crearNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "CANAL";
            String description = "DESCRIPCION CANAL";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("M_CH_ID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void construirNotificacion(String titulo, String contenido) {

        crearNotificationChannel(); //Para la versión Oreo es necesario primero crear el canal
        //Instancia del servicio de notificaciones
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //construcción de la notificación
        mBuilder = new NotificationCompat.Builder(getApplicationContext(), "M_CH_ID");
        mBuilder.setSmallIcon(androidx.preference.R.drawable.notification_icon_background)
                .setContentTitle(titulo)
                .setContentText(contenido);

    }

    /**
     * Usaremos este método para cargar el RecyclerView, la lista de películas y el Adapter.
     * Este método se invoca desde onResume (especialmente
     */
    protected void cargarView() {

        listFilmView = (RecyclerView) findViewById(R.id.recyclerView);
        listFilmView.setHasFixedSize(true);

        //Pillamos la SharedPreference para hacer el filtro establecido
        sharedPreferencesMainRecycler =
                PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        categoryFilter = sharedPreferencesMainRecycler.getString("keyCategoria", "");


        //Cargamos en listaPeli las películas con o sin filtro.
        PeliculasDataSource peliculasDataSource = new PeliculasDataSource(getApplicationContext());

        //Abrir
        peliculasDataSource.open();
        if (categoryFilter == null || categoryFilter == "")
            filmsList = peliculasDataSource.getAllValorations();
        else //A través de este método introducimos el filtro pero mediante lenguaje SQL en la extracción de la base de datos.
            filmsList = peliculasDataSource.getFilteredValorations(categoryFilter);
        //Cerrar
        peliculasDataSource.close();


        listFilmView = (RecyclerView) findViewById(R.id.recyclerView);
        listFilmView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        listFilmView.setLayoutManager(layoutManager);
        ListaPeliculasAdapter lpAdapter = new ListaPeliculasAdapter(filmsList,
                this::clickOnItem);

        listFilmView.setAdapter(lpAdapter);

    }

    private void realizarPeticionPeliculasPopulares(ThemoviedbApi clienteThemoviedbApi) {
        Call<MovieListResult> call=
                clienteThemoviedbApi.getListMovies("popular",API_KEY,LANGUAGE,1);

        // Petición asíncrona a la API
        call.enqueue(new Callback<MovieListResult>() {
            @Override
            public void onResponse(Call<MovieListResult> call, Response<MovieListResult> response) {
                switch (response.code()) {
                    case 200:
                        MovieListResult data= response.body();
                        List<MovieDetails> listaDatosPeliculas= data.getResults();
                        Log.d("PeticionPelPopulares","ListaDatosPeliculas: "+listaDatosPeliculas);

                        // Convertir datos de la API a clase Pelicula del dominio
                        filmsList= convertMovieListToDomain(listaDatosPeliculas);

                        Log.d("NOSE", "ESTAMOS CARGANDO LA API");

                        // cargar de RecyclerView con los datos
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        filmsListView.setLayoutManager(layoutManager);
                        ListaPeliculasAdapter lpAdapter = new ListaPeliculasAdapter(filmsList,
                                peli -> clickOnItem(peli));

                        filmsListView.setAdapter(lpAdapter);


                        break;
                    default:
                        call.cancel();
                        break;
                }
            }

            @Override
            public void onFailure(Call<MovieListResult> call, Throwable t) {
                Log.e("Lista - error", t.toString());
            }
        });
    }
}