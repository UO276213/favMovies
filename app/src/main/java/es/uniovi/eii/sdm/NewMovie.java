package es.uniovi.eii.sdm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import es.uniovi.eii.sdm.modelo.Categoria;
import es.uniovi.eii.sdm.modelo.Pelicula;
import es.uniovi.eii.sdm.util.Conexion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class NewMovie extends AppCompatActivity {


    private Snackbar msgCreaCategoria;

    // identificadores de intents
    public static final String CATEGORIA_SELECCIONADA = "categoria_seleccionada";
    public static final String CATEGORIA_MODIFICADA = "categoria_modificada";
    public static final String POS_CATEGORIA_SELECCIONADA = "pos_categoria_seleccionada";

    // identificador de activity
    private static final int GESTION_CATEGORIA = 1;

    // Modelo
    private List<Categoria> listaCategorias;



    // componentes
    private Spinner spinner;
    private EditText editTitulo;
    private EditText editContenido;
    private EditText editfecha;
    private EditText editDuracion;
    private boolean creandoCategoria;
    private Button btnGuardar;
    ImageButton btnModifCategoria;

    //pelicula actual
     Pelicula pelicula;





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Comprobamos a qué petición se está respondiendo
        if (requestCode == GESTION_CATEGORIA) {
            // Nos aseguramos que el resultado fue OK
            if (resultCode == RESULT_OK) {
                Categoria cateAux= data.getParcelableExtra(CATEGORIA_MODIFICADA);
                //String cad= data.getStringExtra(CATEGORIA_MODIFICADA);
                Log.d("FavMovies.MainActivity",cateAux.toString());

                if (creandoCategoria) {
                    // añadimos categoría a la lista
                    listaCategorias.add(cateAux);
                    introListaSpinner(spinner, listaCategorias);
                    Log.i("Creando CategoriaNueva", "true");


                } else {
                    // busca la categoría del mismo nombre en la lista y cambia la descripción
                    for (Categoria cat: listaCategorias) {
                        if (cat.getNombre().equals(cateAux.getNombre())) {
                            cat.setDescripcion(cateAux.getDescripcion());
                            Log.d("FavMovies.MainActivity","Modificada la descripción de: "+cat.getNombre());
                            break;
                        }

                    }
                }

            }
            else if (resultCode==RESULT_CANCELED) {
                Log.d("FavMovie.MainActivity","CategoriaActivity cancelada");
            }

        }


    }


    /**********CREATE********************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_movie);
        setTitle(R.string.tituloActivityEntrada);

                    // Inicializa el modelo de datos
        listaCategorias = new ArrayList<Categoria>();
        // Inicializa el spinner
        spinner = (Spinner) findViewById(R.id.spinnerCategoria);




        listaCategorias.add(new Categoria("Sin definir", ""));
        listaCategorias.add(new Categoria("Acción", "Peliculas de acción"));
        listaCategorias.add(new Categoria("Comedia", "Peliculas de comedia"));
        listaCategorias.add(new Categoria("Bélica", "Peliculas de guerra"));
        listaCategorias.add(new Categoria("Aventura", "Peliculas de aventura"));
        listaCategorias.add(new Categoria("Musicales", "Peliculas musicales"));
        listaCategorias.add(new Categoria("Drama", "Peliculas de drama"));


        introListaSpinner(spinner, listaCategorias);




        // Recupera campos edición
        editTitulo = (EditText)findViewById(R.id.editTitulo);
        editContenido = (EditText)findViewById(R.id.editContenido);

        editfecha=(EditText)findViewById(R.id.eFecha);
        editDuracion=(EditText)findViewById(R.id.editDuracion);
        btnGuardar= (Button) findViewById(R.id.buttonGuardar);
        btnModifCategoria= (ImageButton)findViewById(R.id.btnModifCategoria);

        //Ocultar el botón de guardar
        btnModifCategoria.setVisibility(View.GONE);




        //Recepción datos
        Intent intentPeli= getIntent();

        Pelicula pelicula=intentPeli.getParcelableExtra(MainRecycler.PELICULA_SELECCIONADA);
        if (pelicula!=null) //apertura en modo consulta
            abrirModoConsulta(pelicula);







        // Definir el observer para el evento click del botón
        // Definimos listener
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Validación de campos
                //TODO: coordinator Layout
                if (validarCampos()){
                    guardarPeli();
                    Snackbar.make(findViewById(R.id.layoutPrincipal), R.string.msg_guardado,
                        Snackbar.LENGTH_LONG)
                        .show();

                }
            }
        });



        //Definir el observer en el evento onclick del image button Categoría

        // Definimos listener
        btnModifCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Spinner spiner=(Spinner)findViewById(R.id.spinnerCategoria);
                if (spiner.getSelectedItemPosition()==0) {
                    msgCreaCategoria = Snackbar.make(findViewById(R.id.layoutPrincipal), R.string.msg_crear_nueva_categoria,
                            Snackbar.LENGTH_LONG);
                } else {
                    msgCreaCategoria = Snackbar.make(findViewById(R.id.layoutPrincipal), R.string.msg_modif_categoria,
                            Snackbar.LENGTH_LONG);

                }


                //Accion de OK
                msgCreaCategoria.setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(findViewById(R.id.layoutPrincipal), R.string.msg_accion_ok,
                                Snackbar.LENGTH_LONG)
                                .show();

                        modificarCategoria();
                    }

                });
                msgCreaCategoria.show();

              /*  //Accion de cancelar
                msgCreaCategoria.setAction(android.R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(findViewById(R.id.layoutPrincipal), R.string.msg_accion_cancelada,
                                Snackbar.LENGTH_LONG)
                                .show();
                    }

                });
                msgCreaCategoria.show();*/






            }
        });



    }
/***************METODOS*******************************************/
/* Este método en la BD cambia totalmente, ya no guardamaos pelis, las obtenemos de la BD*****/
    public void guardarPeli(){
        /*String Caratula_por_defecto="https://image.tmdb.org/t/p/original/jnFCk7qGGWop2DgfnJXeKLZFuBq.jpg\n";
        String fondo_por_defecto="https://image.tmdb.org/t/p/original/xJWPZIYOEFIjZpBL7SVBGnzRYXp.jpg\n";
        String trailer_por_defecto="https://www.youtube.com/watch?v=lpEJVgysiWs\n";
        pelicula= new Pelicula (editTitulo.getText().toString(), editContenido.getText().toString(),
                listaCategorias.get(spinner.getSelectedItemPosition()), editfecha.getText().toString(),
                editDuracion.getText().toString(),Caratula_por_defecto,fondo_por_defecto,trailer_por_defecto);

        Log.i ("pelicula Guradad Categoria",listaCategorias.get(spinner.getSelectedItemPosition()).getNombre() );

        Intent intentResultado= new Intent();
        intentResultado.putExtra(MainRecycler.PELICULA_CREADA,pelicula);
        setResult(RESULT_OK,intentResultado);
        finish();
*/


    }

    public void compartirPeli(){

        if (!validarCampos()) return;
        /* es necesario hacer un intent con la constate ACTION_SEND */
        /*Llama a cualquier app que haga un envío*/
        Intent itSend = new Intent(Intent.ACTION_SEND);
        /* vamos a enviar texto plano */
        itSend.setType("text/plain");
        // itSend.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{para});
        itSend.putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.subject_compartir) + ": " + pelicula.getTitulo());
        itSend.putExtra(Intent.EXTRA_TEXT, getString(R.string.titulo)
                +": "+pelicula.getTitulo()+"\n"+
                getString(R.string.contenido)
                +": "+pelicula.getArgumento());

        /* iniciamos la actividad */
                /* puede haber más de una aplicacion a la que hacer un ACTION_SEND,
                   nos sale un ventana que nos permite elegir una.
                   Si no lo pongo y no hay activity disponible, pueda dar un error */
        Intent shareIntent=Intent.createChooser(itSend, null);

        startActivity(shareIntent);

    }

    private void modificarCategoria(){
        Intent categoriaIntent= new Intent(NewMovie.this, CategoriaActivity.class);
        //Lanza acitivity categoría sin pasarle ninguna categoría (parámetro)
        // startActivity(categoriaIntent);




        categoriaIntent.putExtra(POS_CATEGORIA_SELECCIONADA,spinner.getSelectedItemPosition());
        creandoCategoria= true;
        if (spinner.getSelectedItemPosition()>0) {
            creandoCategoria= false;
            categoriaIntent.putExtra(CATEGORIA_SELECCIONADA,
                    listaCategorias.get(spinner.getSelectedItemPosition()));
        }
       // Log.d("Posicion Spinner",spinner.getSelectedItemPosition()+"" );
        // lanzamos activity para gestionar categoría esperando por un resultado
       startActivityForResult(categoriaIntent,GESTION_CATEGORIA);
    }


    private void introListaSpinner(Spinner spinner, List<Categoria> listaCategorias) {
        // Creamos un nuevo array sólo con los nombres de las categorías
        ArrayList<String> nombres= new ArrayList<String>();
        //TODO: Avisar de este cambio
       // nombres.add("Sin definir");
        for (Categoria elemento : listaCategorias) {
            nombres.add(elemento.getNombre());
        }
        // Crea un ArrayAdapter usando un array de strings y el layout por defecto del spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,nombres);
        // Especifica el layout para usar cuando aparece la lista de elecciones
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        // Aplicar el adapter al spinner
        spinner.setAdapter(adapter);
    }

    //Valida los campos vacios. Si los están muestra un mensaje de error con un tooltip
    public boolean validarCampos() {

        if (editTitulo.getText().toString().isEmpty()) {
            editTitulo.setError(getString(R.string.hint_titulo_nota));
            editTitulo.requestFocus();
            return false;
        }
        if (editContenido.getText().toString().isEmpty()) {
            editContenido.setError(getString(R.string.hint_contenido_nota));
            editContenido.requestFocus();
            return false;
        }
        if (editDuracion.getText().toString().isEmpty()) {
            editDuracion.setError(getString(R.string.hint_duracion));
            editDuracion.requestFocus();
            return false;
        }
        if (editfecha.getText().toString().isEmpty()) {
            editfecha.setError(getString(R.string.hint_fecha));
            editfecha.requestFocus();
            return false;
        }




        return true;



    }

    //Apertura de activity en modo consulta
    public void abrirModoConsulta(Pelicula pelicula){
        if (!pelicula.getTitulo().isEmpty()) { //apertura en modo consulta


            //Actualizar componentes con valores de la pelicula específica
            editTitulo.setText(pelicula.getTitulo());
            editContenido.setText(pelicula.getArgumento());
            editDuracion.setText(pelicula.getDuracion());
            editfecha.setText(pelicula.getFecha());

            //Busqueda en la lista de categoria para colocar la posición del spinner
            int i=0;
            int posicion=0;
            String nombreaccion=pelicula.getCategoria().getNombre();


            for (Categoria elemento : listaCategorias) {
                if (elemento.getNombre().equals(nombreaccion))
                    posicion = i;
                i++;
            }
            spinner.setSelection(posicion);

            //Inhabilitar edición de los conponentes
            editTitulo.setEnabled(false);
            editContenido.setEnabled(false);
            editfecha.setEnabled(false);
            editDuracion.setEnabled(false);
            btnGuardar.setEnabled(false);
            btnModifCategoria.setEnabled(false);
            spinner.setEnabled(false);


        }

    }


}