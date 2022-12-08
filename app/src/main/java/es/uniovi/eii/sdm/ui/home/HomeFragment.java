package es.uniovi.eii.sdm.ui.home;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.uniovi.eii.sdm.ListaPeliculasAdapter;
import es.uniovi.eii.sdm.MainRecycler;
import es.uniovi.eii.sdm.R;
import es.uniovi.eii.sdm.ShowMovie;
import es.uniovi.eii.sdm.datos.server.ServerDataMapper;
import es.uniovi.eii.sdm.datos.server.movielist.MovieData;
import es.uniovi.eii.sdm.datos.server.movielist.MovieListResult;
import es.uniovi.eii.sdm.modelo.Pelicula;
import es.uniovi.eii.sdm.remote.ApiUtils;
import es.uniovi.eii.sdm.remote.ThemoviedbApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static es.uniovi.eii.sdm.remote.ApiUtils.API_KEY;
import static es.uniovi.eii.sdm.remote.ApiUtils.LANGUAGE;

public class HomeFragment extends Fragment {

    // identificador de intent
    public static final String PELICULA_SELECCIONADA = "pelicula_seleccionada";

    //    private HomeViewModel homeViewModel;
    private RecyclerView listaPeliView;
    private View root;

    // API
    private ThemoviedbApi clienteThemoviedbApi;

    // modelo
    private List<Pelicula> listaPeli;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        homeViewModel =
//                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        listaPeliView = (RecyclerView) root.findViewById(R.id.reciclerView);
        listaPeliView.setHasFixedSize(true);

//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        ///////////////////////// recuperación de datos del servicio //////////////////////////
        // cliente para hacer peticiones
        clienteThemoviedbApi= ApiUtils.createThemoviedbApi();

        // Recuperar datos para mostrarlos
        realizarPeticionPeliculasPopulares(clienteThemoviedbApi);

        return root;
    }

    /**
     * Realiza una petición a la API: lista de películas populares
     * de forma asíncrona y procesa el resultado
     * @param clienteThemoviedbApi
     */
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
                        List<MovieData> listaDatosPeliculas= data.getMovieData();
                        Log.d("Peticion PelPopular","ListaDatosPeliculas: "+listaDatosPeliculas);

                        // convierte desde los objetos de data a los objetos modelo MovieData --> Pelicula
                        listaPeli= ServerDataMapper.convertMovieListToDomain(listaDatosPeliculas);

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
                        listaPeliView.setLayoutManager(layoutManager);

                        // Instanciamos el adapter con los datos de la petición y lo asignamos a RecyclerView
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

    // Click del item del adapter
    public void clikonIntem(Pelicula peli) {
        Log.i("Click adapter", "Item Clicked " + peli.getCategoria().getNombre());
        //Toast.makeText(MainActivity.this, "Item Clicked "+user.getId(), Toast.LENGTH_LONG).show();

        //Paso el modo de apertura
        Intent intent = new Intent(root.getContext(), ShowMovie.class);
        intent.putExtra(PELICULA_SELECCIONADA, peli);
        //Transacion de barrido
        startActivity(intent);
//        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getApplicationContext()).toBundle());
    }


}