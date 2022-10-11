package es.uniovi.eii.favmovies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    public static final String FILM_CREATED = "film_created";

    public static int GESTION_CATEGORIA = 1;

    // Modelo
    private ArrayList<Categoria> categoryList;

    // Componentes
    private Spinner spinner;
    private EditText titleInput;
    private EditText synopsisInput;
    private EditText durationInput;
    private EditText dateInput;

    private boolean creatingCategory = false;
    private boolean creatingFilm = false;

    // Película actual
    private Pelicula film;
    private boolean editionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_title);

        titleInput = findViewById(R.id.titleTxtInput);
        synopsisInput = findViewById(R.id.synopsisTxtInput);
        durationInput = findViewById(R.id.durationTxtInput);
        dateInput = findViewById(R.id.dateTxtInput);

        categoryList = new ArrayList<>();
        categoryList.add(new Categoria("Acción", "Película de acción"));
        categoryList.add(new Categoria("Comedia", "Película de comedia"));

        FloatingActionButton saveBtn = findViewById(R.id.saveBtn);
        spinner = findViewById(R.id.spinnerCategory);
        introListaSpinner(spinner, categoryList);

        Intent intent = getIntent();
        film = intent.getParcelableExtra(MainRecyclerActivity.SELECTED_FILM);
        boolean editionMode = intent.getBooleanExtra(MainRecyclerActivity.EDITION_MODE, false);
        if (film != null) {
            titleInput.setText(film.getTitulo());
            synopsisInput.setText(film.getArgument());
            durationInput.setText(film.getDuration());
            dateInput.setText(film.getDate());

            titleInput.setEnabled(editionMode);
            synopsisInput.setEnabled(editionMode);
            durationInput.setEnabled(editionMode);
            dateInput.setEnabled(editionMode);
            spinner.setEnabled(editionMode);
            saveBtn.setEnabled(editionMode);

            for (Categoria category : categoryList) {
                if (category.getNombre().equals(film.getCategory().getNombre())) {
                    spinner.setSelection(categoryList.indexOf(category) + 1);
                    break;
                }
            }
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) selectedCategory = null;
                else selectedCategory = categoryList.get(position - 1).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

        //Definimos listnener
        saveBtn.setOnClickListener(view -> {
            // Validación de cmapos

            if (validarCampos()) {
                guardarPelicula();
                Snackbar.make(findViewById(R.id.mainLayout), R.string.save_msg, Snackbar.LENGTH_LONG).show();
                Intent intentResult = new Intent();
                intentResult.putExtra(FILM_CREATED, film);

                setResult(RESULT_OK, intentResult);
                finish();
            }
        });

        ImageButton modifyCategoryBtn = findViewById(R.id.modifyBtnCategory);
        modifyCategoryBtn.setOnClickListener(view -> {
            modificarCategoria();
        });
    }

    private void guardarPelicula() {
        String title = titleInput.getText().toString();
        String synopsis = synopsisInput.getText().toString();
        String duration = durationInput.getText().toString();
        String date = dateInput.getText().toString();
        Categoria category = categoryList.get(spinner.getSelectedItemPosition());

        film = new Pelicula(title, synopsis, category, duration, date);
    }

    private boolean validarCampos() {

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
        creatingCategory = true;
        if (spinner.getSelectedItemPosition() > 0) {
            creatingCategory = false;
            categoriaIntent.putExtra(CATEGORIA_SELECCIONADA, categoryList.get(spinner.getSelectedItemPosition() - 1));
        }

        startActivityForResult(categoriaIntent, GESTION_CATEGORIA);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GESTION_CATEGORIA) {
            if (resultCode == RESULT_OK) {
                Categoria categoriaAux = data.getParcelableExtra(CATEGORIA_MODIFICADA);

                Log.d("favMovies.MainActivity", categoriaAux.toString());

                if (creatingCategory) {
                    categoryList.add(categoriaAux);
                    introListaSpinner(spinner, categoryList);
                } else {
                    for (Categoria cat : categoryList) {
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

        for (Categoria categoria : listaCategoria) {
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
        if (item.getItemId() == R.id.Compartir) {
            if (film == null) {
                Toast.makeText(getApplicationContext(), R.string.recommend_save_film, Toast.LENGTH_LONG).show();
            } else {
                Conexion conexion = new Conexion(getApplicationContext());

                if (conexion.CompruebaConexion()) compartirPeli();
                else
                    Toast.makeText(getApplicationContext(), R.string.check_conexion, Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void compartirPeli() {
        Intent itSend = new Intent(Intent.ACTION_SEND);
        itSend.setType("text/plain");

        itSend.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject_compartir) + ": " + film.getTitulo());
        itSend.putExtra(Intent.EXTRA_TEXT, getString(R.string.film_title) + ": " + film.getTitulo() + "\n" + getString(R.string.film_content) + ": " + film.getArgument());

        Intent shareIntent = Intent.createChooser(itSend, null);

        startActivity(shareIntent);
    }
}

