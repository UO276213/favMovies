package es.uniovi.eii.sdm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.uniovi.eii.sdm.datos.db.ActorsDataSource;
import es.uniovi.eii.sdm.datos.db.PeliculasDataSource;
import es.uniovi.eii.sdm.datos.db.RepartoPeliculaDataSource;
import es.uniovi.eii.sdm.datos.server.movielist.MovieData;
import es.uniovi.eii.sdm.datos.server.movielist.MovieListResult;
import es.uniovi.eii.sdm.datos.server.ServerDataMapper;
import es.uniovi.eii.sdm.modelo.Actor;
import es.uniovi.eii.sdm.modelo.Categoria;
import es.uniovi.eii.sdm.modelo.Pelicula;
import es.uniovi.eii.sdm.modelo.RepartoPelicula;
import es.uniovi.eii.sdm.remote.ApiUtils;
import es.uniovi.eii.sdm.remote.ThemoviedbApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ActivityOptions;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static es.uniovi.eii.sdm.remote.ApiUtils.API_KEY;
import static es.uniovi.eii.sdm.remote.ApiUtils.LANGUAGE;

public class MainRecycler extends AppCompatActivity {

    // identificador de intent
    public static final String PELICULA_SELECCIONADA = "pelicula_seleccionada";
    public static final String PELICULA_CREADA = "pelicula_creada";

    public static String filtrocategoria = null;

    // identificador de activity de nueva pelicula
    private static final int GESTION_ACTIVITY = 1;

    //Modelo datos
    List<Pelicula> listaPeli;                       // lista peliculas general de la API
    List<Pelicula> listaPeliFavoritas;      // lista favoritas de la BD
    Pelicula peli;

    RecyclerView listaPeliView;

    //SharedPreference de la MainRecycler
    SharedPreferences sharedPreferencesMainRecycler;

    //Objetos para las notificaciones
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotificationManager;

    // API
    private ThemoviedbApi clienteThemoviedbApi; // Cliente API

    /**
     * Boolean que indica si es la primera ejecuci??n. Sirve para evitar que el onResume cargue el RecyclerView antes que la base de datos.
     * Una vez cargada la base de datos, primeraEjecucion toma el valor false.
     */
  //  private boolean primeraEjecucion = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recycler);

        listaPeliView = (RecyclerView) findViewById(R.id.reciclerView);
        listaPeliView.setHasFixedSize(true);

//        //Las tareas as??ncronas van a hacer uso de las notificaciones, aqu?? simplemente las construimos con un nombre y un contenido.
//        //Ojo, esta notificaci??n se ha lanzado, pero no se activar?? hasta que no se indique al builder que ya puede hacerlo
//        //Y eso ser?? dentro de la tarea as??ncrona.
//        ConstruirNotificacion(getString(R.string.app_name), "Acceso a la BD de peliculas");
//        //Lanzamos la tarea as??ncrona en segundo t??rmino
//        DownLoadFilesTask task = new DownLoadFilesTask();
//        task.execute();

        ///////////////////////// recuperaci??n de datos del servicio //////////////////////////
        // cliente para hacer peticiones
        clienteThemoviedbApi= ApiUtils.createThemoviedbApi();

        // Recuperar datos para mostrarlos
        realizarPeticionPeliculasPopulares(clienteThemoviedbApi);

        ///////////////////////// recuperaci??n de datos de la BD ///////////////////////////////
        recuperarPeliculasFavoritasDb();

    }


    /**
     * Realiza una petici??n a la API: lista de pel??culas populares
     * de forma as??ncrona y procesa el resultado
     * @param clienteThemoviedbApi
     */
    private void realizarPeticionPeliculasPopulares(ThemoviedbApi clienteThemoviedbApi) {
        Call<MovieListResult> call=
                clienteThemoviedbApi.getListMovies("popular",API_KEY,LANGUAGE,1);

        // Petici??n as??ncrona a la API
        call.enqueue(new Callback<MovieListResult>() {
            @Override
            public void onResponse(Call<MovieListResult> call, Response<MovieListResult> response) {
                switch (response.code()) {
                    case 200:
                        MovieListResult data= response.body();
                        List<MovieData> listaDatosPeliculas= data.getMovieData();
                        Log.d("Peticion PelPopular","ListaDatosPeliculas: "+listaDatosPeliculas);

                        // convierte desde los objetos de data a los objetos modelo MovieData --> Pelicula
                        listaPeli= ServerDataMapper.convertMovieListToDomain(listaDatosPeliculas);

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        listaPeliView.setLayoutManager(layoutManager);

                        // Instanciamos el adapter con los datos de la petici??n y lo asignamos a RecyclerView
                        // Generar el adaptador, le pasamos la lista de usuarios
                        // y el manejador para el evento click sobre un elemento
                        ListaPeliculasAdapter lpAdapter = new ListaPeliculasAdapter(listaPeli,
                                new ListaPeliculasAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(Pelicula peli) {
                                        clikonIntem(peli);
                                    }
                                });
                        /*Le coloco el adapter*/
                        listaPeliView.setAdapter(lpAdapter);


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

    /**
     * Usaremos este m??todo para cargar el RecyclerView, la lista de pel??culas y el Adapter.
     * Este m??todo se invoca desde onResume (especialmente
     */
    protected void cargarView() {

        //Pillamos la SharedPreference para hacer el filtro establecido
        sharedPreferencesMainRecycler =
                PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        filtrocategoria =  sharedPreferencesMainRecycler.getString("keyCategoria", "");

        recuperarPeliculasFavoritasDb();

        // repetido
//        listaPeliView = (RecyclerView) findViewById(R.id.reciclerView);
//        listaPeliView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        listaPeliView.setLayoutManager(layoutManager);
        ListaPeliculasAdapter lpAdapter = new ListaPeliculasAdapter(listaPeli,
                new ListaPeliculasAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Pelicula peli) {
                        clikonIntem(peli);
                    }
                });

        listaPeliView.setAdapter(lpAdapter);
    }

    /**
     * Recupera todas ls peliculas de la tabla peliculas (favoritas) de la BD
     * y las carga en la lista: listaPeliFavoritas
     */
    private void recuperarPeliculasFavoritasDb() {
        // Recuperaci??n desde la base de datos
        //Cargamos en listaPeli las pel??culas con o sin filtro.
        PeliculasDataSource peliculasDataSource = new PeliculasDataSource(getApplicationContext());

        //Abrir
        peliculasDataSource.open();
//        if(filtrocategoria == null || filtrocategoria=="")
        listaPeliFavoritas = peliculasDataSource.getAllValorations();
//        else //A trav??s de este m??todo introducimos el filtro pero mediante lenguaje SQL en la extracci??n de la base de datos.
//            listaPeliFavoritas = peliculasDataSource.getFilteredValorations(filtrocategoria);
        Log.d("BD recupera favoritas","listaPeliFavoritas: "+ listaPeliFavoritas);
        //Cerrar
        peliculasDataSource.close();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Comprobamos a qu?? petici??n se est?? respondiendo
        if (requestCode == GESTION_ACTIVITY) {
            // Nos aseguramos que el resultado fue OK
            if (resultCode == RESULT_OK) {
                peli = data.getParcelableExtra(PELICULA_CREADA);


                // Refrescar el ReciclerView
                //A??adimos a la lista de peliculas la peli nueva
                listaPeli.add(peli);

                //creamos un nuevo adapter que le pasamos al recyclerView
                ListaPeliculasAdapter listaPeliculasAdapter = new ListaPeliculasAdapter(listaPeli,
                        new ListaPeliculasAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Pelicula peli) {
                                clikonIntem(peli);
                            }
                        });


                listaPeliView.setAdapter(listaPeliculasAdapter);


            }
        }



    }

    //Gesti??n del men??
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       if (id == R.id.settings) {

           //Llamamos a la activity settings de ajustes

           Intent intentSettingsActivity=new Intent(MainRecycler.this, SettingsActivity.class);
           startActivity(intentSettingsActivity);





            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Click del item del adapter
    public void clikonIntem(Pelicula peli) {
        Log.i("Click adapter", "Item Clicked " + peli.getCategoria().getNombre());
        //Toast.makeText(MainActivity.this, "Item Clicked "+user.getId(), Toast.LENGTH_LONG).show();

        //Paso el modo de apertura
        Intent intent = new Intent(MainRecycler.this, ShowMovie.class);
        intent.putExtra(PELICULA_SELECCIONADA, peli);
        //Transacion de barrido
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }


    // Creamos la lista de peliculas
    // EN la versi??on de la BD, desaparace*/
   /* private void rellenarLista() {
        listaPeli = new ArrayList<Pelicula>();
        Categoria cataccion = new Categoria("Acci??n", "PelisAccion");
        Pelicula peli = new Pelicula("Tenet", "Una acci??n ??pica que gira en torno al espionaje internacional, los viajes en el tiempo y la evoluci??n, en la que un agente secreto debe prevenir la Tercera Guerra Mundial.",
                cataccion, "150", "26/8/2020","","","");
        listaPeli.add(peli);

    }*/


    /*Listener sobre el Fab**/
    public void crearPeliNuevaFab(View v) {
        Log.d("CrearPeli", "crearPeil");
        Intent intent = new Intent(MainRecycler.this, NewMovie.class);
        startActivityForResult(intent, GESTION_ACTIVITY);

    }








/*****************Clase interna de la tarea as??ncrona**************************/

/**
 * Clase as??ncrona interna: AsyncTask <Par??metro de entrada, Tipo de valor del progreso, Tipo del return>
 */

private class DownLoadFilesTask extends AsyncTask <Void, Integer, String>{
    //Barra de progreso
    private ProgressDialog progressDialog;

    /**
     * Variables necesarias para llevar la cuenta del progreso de la carga.
     * ??Ojo! La f??rmula que vamos a utilizar para calcular el porcentaje le??do es:
     * (numeroLineasLeidas/lineasALeer)*100
     * Esa divisi??n requiere que el tipo de las variables sea flotante para poder obtener decimales
     * que al multiplicar por 100 nos den el porcentaje.
     *
     * En caso de usar enteros pasar??a de 0 a 100 sin los intermedios, llevando una cuenta no v??lida.
     */
    private float lineasALeer = 0.0f;
    float numeroLineasLeidas = 0.0f;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.progressDialog = new ProgressDialog(MainRecycler.this);
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();

        //Inicializamos el lineasALeer, con un repaso a la cantidad de l??neas que tienen los ficheros.
        lineasALeer = (float)(lineasFichero("peliculas.csv"));
        lineasALeer = (float)(lineasALeer + lineasFichero("peliculas-reparto.csv"));
        lineasALeer = (float)(lineasALeer + lineasFichero("reparto.csv"));

    }

    //M??todo principal que se ejecutar?? en segundo plano.
    //El Void se corresponde al par??metro indicado en el encabezado de la clase.
    @Override
    protected String doInBackground(Void... voids) {
        //El mensaje que vamos a mostrar como notificaci??n
        String mensaje = "";

        try {
            //Cargamos la base de datos.
            cargarPeliculas();
            cargarReparto();
            cargarRepartoPelicula();

            //Si la carga no da ning??n error inesperado...
            mensaje = "Lista de pel??culas actualizada";

        } catch(Exception e) {
            //Si la carga da alg??n error
            mensaje = "Error en la actualizaci??n de la lista de pel??culas";
        }

        //Lanzamos notificaci??n.
        mNotificationManager.notify(001,mBuilder.build());
        return mensaje;

    }
    //Este m??todo actualiza la barra de progreso..
    //El Integer se corresponde al par??metro indicado en el encabezado de la clase.
    protected void onProgressUpdate(Integer... progress) {
        progressDialog.setProgress(progress[0]);
    }

    //M??todo que se ejecuta tras doInBackground.
    //El mensaje que recibe es el que devolvemos en la ejecuci??n principal.
    protected void onPostExecute(String message) {
        //descartar el mensaje despu??s de que la base de datos haya sido actualizada
        this.progressDialog.dismiss();
        //Avisamos que la base de datos se carg?? satisfactoriamente (o hubo error, seg??n lo que haya ocurrido)
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
        //Y cargamos el recyclerview por primera vez.
        //Este m??todo ya no tiene sentido llamarlo desde el onCreate u onResume, pues necesitamos asegurarnos
        //de haber cargado la base de datos antes de lanzarlo.
        cargarView();
    }

    /**
     * Devuelve un entero con las l??neas que contiene un fichero,
     * cuyo nombre recibe por par??metro.
     */
    protected int lineasFichero(String nombreFichero) {
        InputStream file = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;
        int lineas = 0;

        try {
            file = getAssets().open(nombreFichero);
            reader = new InputStreamReader(file);
            bufferedReader = new BufferedReader(reader);

            //Pase r??pido mirando el total de l??neas,
            // sin perder tiempo de procesamiento en nada m??s
            //Necesario para el progressbar
            while (bufferedReader.readLine() != null)
                lineas++;

            bufferedReader.close();
        }catch(Exception e){};
        return lineas;
    }


    /**
     * Lee lista de pel??culas desde el fichero csv en assets
     * Crea listaPeli como un ArrayList<Pelicula>
     * Y adem??s a??ade pelis a la tabla de pelis en la BD
     */
    protected void cargarPeliculas() {
        Pelicula peli = null;
        InputStream file = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;

        try {
            file = getAssets().open("peliculas.csv");
            reader = new InputStreamReader(file);
            bufferedReader = new BufferedReader(reader);
            String line = null;

            //Leemos la primera l??nea que es encabezado y por tanto no nos aporta informaci??n ??til.
            bufferedReader.readLine();

            //ACTUALIZAMOS EL PROGRESSBAR
            numeroLineasLeidas++;
            publishProgress((int)((numeroLineasLeidas /lineasALeer)*100));


            //A partir de aqu?? leemos a partir de la segunda l??nea.
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(";");
                if (data != null && data.length >= 5) { //El segundo condicional se va a cumplir siempre. Podemos quitarlo
                    if (data.length==9) {
                        //Tengamos en cuenta que hemos a??adido un id al primer par??metro
                        peli = new Pelicula(Integer.parseInt(data[0]), data[1], data[2], new Categoria(data[3], ""), data[4],
                                data[5], data[6], data[7], data[8]);
                    } else {
                        peli = new Pelicula(Integer.parseInt(data[0]), data[1], data[2], new Categoria(data[3], ""), data[4],
                                data[5], "", "", "");
                    }
                    Log.d("cargarPeliculas", peli.toString());




                    //Metemos la pel??cula en la base de datos:
                    PeliculasDataSource peliculasDataSource = new PeliculasDataSource(getApplicationContext());
                    peliculasDataSource.open();
                    peliculasDataSource.createpelicula(peli);
                    peliculasDataSource.close();

                    //ACTUALIZAMOS EL PROGRESSBAR
                    numeroLineasLeidas++;
                    publishProgress((int)((numeroLineasLeidas /lineasALeer)*100));

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

            //Leemos la primera l??nea que es encabezado y por tanto no nos aporta informaci??n ??til.
            bufferedReader.readLine();

            //ACTUALIZAMOS EL PROGRESSBAR
            numeroLineasLeidas++;
            publishProgress((int)((numeroLineasLeidas /lineasALeer)*100));


            //A partir de aqu?? leemos a partir de la segunda l??nea.
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(";");
                if (data != null) {
                    if (data.length==4) {
                        actor = new Actor(Integer.parseInt(data[0]), data[1], data[2], data[3]);
                    }

                    //Metemos la pel??cula en la base de datos:
                    ActorsDataSource actoresDataSource = new ActorsDataSource(getApplicationContext());
                    actoresDataSource.open();
                    actoresDataSource.createactor(actor);
                    actoresDataSource.close();

                    //ACTUALIZAMOS EL PROGRESSBAR
                    numeroLineasLeidas++;
                    publishProgress((int)((numeroLineasLeidas /lineasALeer)*100));

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

            //Leemos la primera l??nea que es encabezado y por tanto no nos aporta informaci??n ??til.
            bufferedReader.readLine();

            //ACTUALIZAMOS EL PROGRESSBAR
            numeroLineasLeidas++;
            publishProgress((int)((numeroLineasLeidas /lineasALeer)*100));


            //A partir de aqu?? leemos a partir de la segunda l??nea.
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(";");
                if (data != null) {
                    if (data.length==3) {
                        rel = new RepartoPelicula(Integer.parseInt(data[0]), Integer.parseInt(data[1]), data[2]);
                    }


                    //Metemos la pel??cula en la base de datos:
                    RepartoPeliculaDataSource relDataSource = new RepartoPeliculaDataSource(getApplicationContext());
                    relDataSource.open();
                    relDataSource.createrepartoPelicula(rel);
                    relDataSource.close();

                    //ACTUALIZAMOS EL PROGRESSBAR
                    numeroLineasLeidas++;
                    publishProgress((int)((numeroLineasLeidas /lineasALeer)*100));

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

/***********M??todos de creaci??n de notificaciones*******************
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
public void ConstruirNotificacion(String titulo, String contenido){

        crearNotificationChannel(); //Para la versi??n Oreo es necesario primero crear el canal
        //Instancia del servicio de notificaciones
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //construcci??n de la notificaci??n
        mBuilder = new NotificationCompat.Builder(getApplicationContext(), "M_CH_ID");
        mBuilder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(titulo)
                .setContentText(contenido);


    }

}


