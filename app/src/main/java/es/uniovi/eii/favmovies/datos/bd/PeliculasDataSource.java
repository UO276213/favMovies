package es.uniovi.eii.favmovies.datos.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.database.SQLException;
import java.util.ArrayList;
import java.util.List;

import es.uniovi.eii.favmovies.modelos.Categoria;
import es.uniovi.eii.favmovies.modelos.Pelicula;

public class PeliculasDataSource {

    /**
     * Referencia para manejar la base de datos. Este objeto lo obtenemos a partir de MyDBHelper
     * y nos proporciona metodos para hacer operaciones
     * CRUD (create, read, update and delete)
     */
    private SQLiteDatabase database;
    /**
     * Referencia al helper que se encarga de crear y actualizar la base de datos.
     */
    private MyDBHelper dbHelper;
    /**
     * Columnas de la tabla
     */
    private final String[] allColumns = {MyDBHelper.COLUMNA_ID_PELICULAS, MyDBHelper.COLUMNA_TITULO_PELICULAS,
            MyDBHelper.COLUMNA_ARGUMENTO_PELICULAS, MyDBHelper.COLUMNA_CATEGORIA_PELICULAS, MyDBHelper.COLUMNA_DURACION_PELICULAS,
            MyDBHelper.COLUMNA_FECHA_PELICULAS, MyDBHelper.COLUMNA_CARATULA_PELICULAS, MyDBHelper.COLUMNA_FONDO_PELICULAS,
            MyDBHelper.COLUMNA_TRAILER_PELICULAS};

    /**
     * Constructor.
     *
     * @param context
     */
    public PeliculasDataSource(Context context) {
        //el último parámetro es la versión
        dbHelper = new MyDBHelper(context, null, null, 1);
    }

    /**
     * Abre una conexion para escritura con la base de datos.
     * Esto lo hace a traves del helper con la llamada a getWritableDatabase. Si la base de
     * datos no esta creada, el helper se encargara de llamar a onCreate
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();

    }

    /**
     * Cierra la conexion con la base de datos
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Recibe la película y crea el registro en la base de datos.
     *
     * @param peliculaToInsert
     * @return
     */
    public long createpelicula(Pelicula peliculaToInsert) {
        // Establecemos los valores que se insertaran
        ContentValues values = new ContentValues();

        values.put(MyDBHelper.COLUMNA_ID_PELICULAS, peliculaToInsert.getId());
        values.put(MyDBHelper.COLUMNA_TITULO_PELICULAS, peliculaToInsert.getTitle());
        values.put(MyDBHelper.COLUMNA_ARGUMENTO_PELICULAS, peliculaToInsert.getArgument());
        values.put(MyDBHelper.COLUMNA_CATEGORIA_PELICULAS, peliculaToInsert.getCategory().getNombre());
        values.put(MyDBHelper.COLUMNA_DURACION_PELICULAS, peliculaToInsert.getDuration());
        values.put(MyDBHelper.COLUMNA_FECHA_PELICULAS, peliculaToInsert.getDate());
        values.put(MyDBHelper.COLUMNA_CARATULA_PELICULAS, peliculaToInsert.getCoverUrl());
        values.put(MyDBHelper.COLUMNA_FONDO_PELICULAS, peliculaToInsert.getBackgroundUrl());
        values.put(MyDBHelper.COLUMNA_TRAILER_PELICULAS, peliculaToInsert.getVideoUrl());


        // Insertamos la valoracion
        long insertId =
                database.insert(MyDBHelper.TABLA_PELICULAS, null, values);

        return insertId;
    }

    /**
     * Obtiene todas las valoraciones andadidas por los usuarios.
     *
     * @return Lista de objetos de tipo Pelicula
     */
    public List<Pelicula> getAllValorations() {
        // Lista que almacenara el resultado
        List<Pelicula> peliculaList = new ArrayList<Pelicula>();
        //hacemos una query porque queremos devolver un cursor

        Cursor cursor = database.query(MyDBHelper.TABLA_PELICULAS, allColumns,
                null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            final Pelicula pelicula = new Pelicula();
            cursor.getInt(0);
            pelicula.setId(cursor.getInt(0));
            pelicula.setTitulo(cursor.getString(1));
            pelicula.setArgument(cursor.getString(2));
            pelicula.setCategory(new Categoria(cursor.getString(3), ""));
            pelicula.setDuration(cursor.getString(4));
            pelicula.setDate(cursor.getString(5));
            pelicula.setCoverUrl("https://image.tmdb.org/t/p/original/" + cursor.getString(6));
            pelicula.setBackgroundUrl("https://image.tmdb.org/t/p/original/" + cursor.getString(7));
            pelicula.setVideoUrl("https://youtu.be/" + cursor.getString(8));


            peliculaList.add(pelicula);
            cursor.moveToNext();
        }

        cursor.close();
        // Una vez obtenidos todos los datos y cerrado el cursor, devolvemos la
        // lista.

        return peliculaList;
    }


    /**
     * Obtiene todas las valoraciones andadidas por los usuarios con el filtro introducido en el SQL.
     *
     * @return Lista de objetos de tipo Pelicula
     */
    public List<Pelicula> getFilteredValorations(String filtro) {
        // Lista que almacenara el resultado
        List<Pelicula> peliculaList = new ArrayList<>();
        //hacemos una query porque queremos devolver un cursor

        Cursor cursor = database.rawQuery("Select * " +
                " FROM " + MyDBHelper.TABLA_PELICULAS +
                " WHERE " + MyDBHelper.TABLA_PELICULAS + "." + MyDBHelper.COLUMNA_CATEGORIA_PELICULAS + " = \"" + filtro + "\"", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            final Pelicula pelicula = new Pelicula();
            pelicula.setId(cursor.getInt(0));
            pelicula.setTitulo(cursor.getString(1));
            pelicula.setArgument(cursor.getString(2));
            pelicula.setCategory(new Categoria(cursor.getString(3), ""));
            pelicula.setDuration(cursor.getString(4));
            pelicula.setDate(cursor.getString(5));
            pelicula.setCoverUrl("https://image.tmdb.org/t/p/original/" + cursor.getString(6));
            pelicula.setBackgroundUrl("https://image.tmdb.org/t/p/original/" + cursor.getString(7));
            pelicula.setVideoUrl("https://youtu.be/" + cursor.getString(8));


            peliculaList.add(pelicula);
            cursor.moveToNext();
        }

        cursor.close();
        // Una vez obtenidos todos los datos y cerrado el cursor, devolvemos la
        // lista.

        return peliculaList;
    }
}
