package es.uniovi.eii.sdm.datos.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import es.uniovi.eii.sdm.modelo.RepartoPelicula;

public class RepartoPeliculaDataSource {
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
    private final String[] allColumns = { MyDBHelper.COLUMNA_ID_REPARTO, MyDBHelper.COLUMNA_ID_PELICULAS,
            MyDBHelper.COLUMNA_PERSONAJE };
    /**
     * Constructor.
     *
     * @param context
     */
    public RepartoPeliculaDataSource(Context context) {
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


    public long createrepartoPelicula(RepartoPelicula repartoPeliculaToInsert) {
        // Establecemos los valores que se insertaran
        ContentValues values = new ContentValues();

        values.put(MyDBHelper.COLUMNA_ID_REPARTO, repartoPeliculaToInsert.getId_reparto());
        values.put(MyDBHelper.COLUMNA_ID_PELICULAS, repartoPeliculaToInsert.getId_pelicula());
        values.put(MyDBHelper.COLUMNA_PERSONAJE, repartoPeliculaToInsert.getNombre_personaje());


        // Insertamos la valoracion
        long insertId =
                database.insert(MyDBHelper.TABLA_PELICULAS_REPARTO, null, values);

        return insertId;
    }

    /**
     * Obtiene todas las valoraciones andadidas por los usuarios.
     *
     * @return Lista de objetos de tipo RepartoPelicula
     *
     */
    public List<RepartoPelicula> getAllValorations() {
        // Lista que almacenara el resultado
        List<RepartoPelicula> repartoPeliculaList = new ArrayList<RepartoPelicula>();
        //hacemos una query porque queremos devolver un cursor

            Cursor cursor = database.query(MyDBHelper.TABLA_PELICULAS_REPARTO, allColumns,
                    null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                final RepartoPelicula repartoPelicula = new RepartoPelicula();
                repartoPelicula.setId_reparto(cursor.getInt(0));
                repartoPelicula.setId_pelicula(cursor.getInt(1));
                repartoPelicula.setNombre_personaje(cursor.getString(2));


                repartoPeliculaList.add(repartoPelicula);
                cursor.moveToNext();
            }


        cursor.close();

        // Una vez obtenidos todos los datos y cerrado el cursor, devolvemos la
        // lista.
        return repartoPeliculaList;
    }

}
