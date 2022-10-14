package es.uniovi.eii.favmovies;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import es.uniovi.eii.favmovies.modelos.Pelicula;
import es.uniovi.eii.favmovies.util.Conexion;

public class ShowMovie extends AppCompatActivity {

    private Pelicula film;
    private CollapsingToolbarLayout toolBarLayout;
    private ImageView backgroundImage;
    private TextView category;
    private TextView estreno;
    private TextView duracion;
    private TextView argumento;
    private ImageView caratula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_movie);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());
        backgroundImage = findViewById(R.id.background_image);

        category = findViewById(R.id.categoria);
        estreno = findViewById(R.id.estreno);
        duracion = findViewById(R.id.duracion);
        argumento = findViewById(R.id.argumento);
        caratula = findViewById(R.id.caratula);

        Intent intentFilm = getIntent();
        film = intentFilm.getParcelableExtra(MainRecyclerActivity.SELECTED_FILM);
        Log.d("TEST", film.getCoverUrl());

        if (film != null)
            showData(film);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            showTrailer(film.getVideoUrl());
        });
    }

    private void showTrailer(String videoUrl) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_show_movie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings)
            return true;

        if (id == R.id.Compartir) {
            Conexion conexion = new Conexion(getApplicationContext());

            if (conexion.CompruebaConexion()) compartirPeli();
            else
                Toast.makeText(getApplicationContext(), R.string.conexion_error, Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void compartirPeli() {
        Intent itSend = new Intent(Intent.ACTION_SEND);
        itSend.setType("text/plain");

        itSend.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_compartir) + ": " + film.getTitle());
        itSend.putExtra(Intent.EXTRA_TEXT, getString(R.string.film_title) + ": " + film.getTitle() + "\n" + getString(R.string.film_content) + ": " + film.getArgument());

        Intent shareIntent = Intent.createChooser(itSend, null);

        startActivity(shareIntent);
    }

    private void showData(Pelicula film) {
        if (!film.getTitle().isEmpty()) {
            String date = film.getDate();
            category.setText(film.getCategory().getNombre());
            estreno.setText(film.getDate());
            duracion.setText(film.getDuration());
            argumento.setText(film.getArgument());
            Picasso.get().load(film.getCoverUrl()).into(caratula);
            Picasso.get().load(film.getBackgroundUrl()).into(backgroundImage);

        }
    }
}