package es.uniovi.eii.sdm.ui.favmovies;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.uniovi.eii.sdm.ListaPeliculasAdapter;
import es.uniovi.eii.sdm.R;
import es.uniovi.eii.sdm.ShowMovie;
import es.uniovi.eii.sdm.datos.db.PeliculasDataSource;
import es.uniovi.eii.sdm.modelo.Pelicula;

public class FavFragment extends Fragment {

    // identificador de intent
    public static final String PELICULA_SELECCIONADA = "pelicula_seleccionada";

//    private GalleryViewModel galleryViewModel;
    private View root;
    private RecyclerView listaPeliView;

    private List<Pelicula> listaPeliFavoritas;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        galleryViewModel =
//                ViewModelProviders.of(this).get(GalleryViewModel.class);
        root = inflater.inflate(R.layout.fragment_fav, container, false);

        listaPeliView = (RecyclerView) root.findViewById(R.id.reciclerView);
        listaPeliView.setHasFixedSize(true);

//        final TextView textView = root.findViewById(R.id.text_gallery);
//        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        ///////////////////////// recuperación de datos de la BD ///////////////////////////////
        recuperarPeliculasFavoritasDb();
        cargarView();

        return root;
    }

    /**
     * Recupera todas ls peliculas de la tabla peliculas (favoritas) de la BD
     * y las carga en la lista: listaPeliFavoritas
     */
    private void recuperarPeliculasFavoritasDb() {
        // Recuperación desde la base de datos
        //Cargamos en listaPeli las películas con o sin filtro.
        PeliculasDataSource peliculasDataSource = new PeliculasDataSource(root.getContext());

        //Abrir
        peliculasDataSource.open();
//        if(filtrocategoria == null || filtrocategoria=="")
        listaPeliFavoritas = peliculasDataSource.getAllValorations();
//        else //A través de este método introducimos el filtro pero mediante lenguaje SQL en la extracción de la base de datos.
//            listaPeliFavoritas = peliculasDataSource.getFilteredValorations(filtrocategoria);
        Log.d("BD recupera favoritas","listaPeliFavoritas: "+ listaPeliFavoritas);
        //Cerrar
        peliculasDataSource.close();
    }

    /**
     * Usaremos este método para cargar el RecyclerView, la lista de películas y el Adapter.
     * Este método se invoca desde onResume (especialmente
     */
    protected void cargarView() {

        //Pillamos la SharedPreference para hacer el filtro establecido
//        sharedPreferencesMainRecycler =
//                PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
//        filtrocategoria =  sharedPreferencesMainRecycler.getString("keyCategoria", "");

//        recuperarPeliculasFavoritasDb();

        // repetido
//        listaPeliView = (RecyclerView) findViewById(R.id.reciclerView);
//        listaPeliView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        listaPeliView.setLayoutManager(layoutManager);
        ListaPeliculasAdapter lpAdapter = new ListaPeliculasAdapter(listaPeliFavoritas,
                new ListaPeliculasAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Pelicula peli) {
                        clikonIntem(peli);
                    }
                });

        listaPeliView.setAdapter(lpAdapter);
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