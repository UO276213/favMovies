package es.uniovi.eii.favmovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import es.uniovi.eii.favmovies.modelos.Categoria;
import es.uniovi.eii.favmovies.modelos.Pelicula;
import es.uniovi.eii.favmovies.util.Conexion;

public class MainActivity extends AppCompatActivity {

    String selectedCategory = "";
    public static final String POS_CATEGORIA_SELEECIONADA = "pos_categoria_seleccionada";
    public static final String CATEGORIA_SELECCIONADA = "categoria_seleccionada";
    public static final String CATEGORIA_MODIFICADA = "categoria_modificada";
    private Spinner spinner;

    public static int GESTION_CATEGORIA = 1;
    private boolean creandoCategoria = false;
    private ArrayList<Categoria> listaCategoria;
    private Pelicula pelicula;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_title);

        listaCategoria = new ArrayList<>();
        listaCategoria.add(new Categoria("Acción", "Película de acción"));
        listaCategoria.add(new Categoria("Comedia", "Película de comedia"));

        FloatingActionButton saveBtn = findViewById(R.id.saveBtn);
        spinner = findViewById(R.id.spinnerCategory);
        introListaSpinner(spinner, listaCategoria);

        //Definimos listnener
        saveBtn.setOnClickListener(view -> {
            // Validación de cmapos

            if (validarCampos())
                Snackbar.make(findViewById(R.id.mainLayout), R.string.save_msg, Snackbar.LENGTH_LONG).show();
        });

        ImageButton modifyCategoryBtn = findViewById(R.id.modifyBtnCategory);
        modifyCategoryBtn.setOnClickListener(view -> {
            spinner = findViewById(R.id.spinnerCategory);
//            Snackbar msgCreaCategory;

            modificarCategoria();


            // Accción de cancelar
//            msgCreaCategory.setAction(android.R.string.cancel, view1 -> Snackbar.make(findViewById(R.id.mainLayout), R.string.cancel_action, Snackbar.LENGTH_LONG).show());

//            msgCreaCategory.show();
        });
    }

    private boolean validarCampos() {
        EditText titleInput = findViewById(R.id.titleTxtInput);
        EditText synopsisInput = findViewById(R.id.synopsisTxtInput);
        EditText durationInput = findViewById(R.id.durationTxtInput);
        EditText dateInput = findViewById(R.id.dateTxtInput);

        if (titleInput.getText().toString().isEmpty()) {
            Snackbar.make(findViewById(R.id.mainLayout), "Falta el título", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (synopsisInput.getText().toString().isEmpty()) {
            Snackbar.make(findViewById(R.id.mainLayout), "Falta la sinopsis", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (selectedCategory.isEmpty()) {
            Snackbar.make(findViewById(R.id.mainLayout), "Categoría sin especificar", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (durationInput.getText().toString().isEmpty()) {
            Snackbar.make(findViewById(R.id.mainLayout), "Falta la duración", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (dateInput.getText().toString().isEmpty()) {
            Snackbar.make(findViewById(R.id.mainLayout), "Falta la fecha", Snackbar.LENGTH_LONG).show();
            return false;
        }


        return true;
    }

    private void modificarCategoria() {
        Intent categoriaIntent = new Intent(MainActivity.this, CategoriaActivity.class);

        categoriaIntent.putExtra(POS_CATEGORIA_SELEECIONADA, spinner.getSelectedItemPosition());
        creandoCategoria = true;
        if (spinner.getSelectedItemPosition() > 0){
            creandoCategoria = false;
            categoriaIntent.putExtra(CATEGORIA_SELECCIONADA, listaCategoria.get(spinner.getSelectedItemPosition() - 1));
        }

        startActivityForResult(categoriaIntent, GESTION_CATEGORIA);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GESTION_CATEGORIA){
            if (resultCode == RESULT_OK){
                Categoria categoriaAux = data.getParcelableExtra(CATEGORIA_MODIFICADA);

                Log.d("favMovies.MainActivity", categoriaAux.toString());

                if (creandoCategoria){
                    listaCategoria.add(categoriaAux);
                    introListaSpinner(spinner, listaCategoria);
                } else {
                    for (Categoria cat: listaCategoria){
                        if (cat.getNombre().equals(categoriaAux.getNombre())) {
                            cat.setDescripcion(categoriaAux.getDescripcion());
                            Log.d("FavMovies.MainActivity", "Modificada la descripción de: " + cat.getNombre());
                            break;
                        }
                    }
                }
            }
        }
    }

    private void introListaSpinner(Spinner spinner, ArrayList<Categoria> listaCategoria) {
        ArrayList<String> nombres = new ArrayList<>();

        nombres.add("Sin definir");

        for (Categoria categoria : listaCategoria){
            nombres.add(categoria.getNombre());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Compartir){
            Log.d("Guardar Peli", "Guardar Peli");
//            guardarPeli();

            Conexion conexion = new Conexion(getApplicationContext());

            if (conexion.CompruebaConexion()){
                compartirPeli();
            }else
                Toast.makeText(getApplicationContext(), R.string.compruba_conexión, Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void compartirPeli() {
        Intent itSend = new Intent(Intent.ACTION_SEND);
        itSend.setType("text/plain");

        itSend.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_compartir) + ": " + pelicula.getTitulo());
        itSend.putExtra(Intent.EXTRA_TEXT, getString(R.string.titulo) + ": " + pelicula.getTitulo() + "\n" + getString(R.string.contenido) + ": " + pelicula.getArgumento());

        Intent shareIntent = Intent.createChooser(itSend, null);

        startActivity(shareIntent);
    }
}

