package es.uniovi.eii.favmovies.datos.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * MyDHelper
 */
public class MyDBHelper extends SQLiteOpenHelper {

    /**
     * Nombre y version de la base de datos
     */
    private static final String DATABASE_NAME = "pelis.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * Nombre de la tabla películas y sus columnas
     * id;titulo;argumento;categoria;duracion;fecha;caratula;fondo;trailer
     */
    public static final String TABLA_PELICULAS = "tabla_peliculas";

    public static final String COLUMNA_ID_PELICULAS = "id_pelicula";
    public static final String COLUMNA_TITULO_PELICULAS = "titulo_pelicula";
    public static final String COLUMNA_ARGUMENTO_PELICULAS = "argumento_pelicula";
    public static final String COLUMNA_CATEGORIA_PELICULAS = "categoria_pelicula";
    public static final String COLUMNA_DURACION_PELICULAS = "duracion_pelicula";
    public static final String COLUMNA_FECHA_PELICULAS = "fecha_pelicula";
    public static final String COLUMNA_CARATULA_PELICULAS = "URL_caratula_pelicula";
    public static final String COLUMNA_FONDO_PELICULAS = "URL_fondo_pelicula";
    public static final String COLUMNA_TRAILER_PELICULAS = "URL_trailer_pelicula";


    /**
     * Nombre de la tabla películas_reparto y sus columnas
     * id_reparto;id_pelicula; (estas son Foreign keys); Personaje
     */
    public static final String TABLA_PELICULAS_REPARTO = "tabla_peliculas_reparto";

    public static final String COLUMNA_PERSONAJE = "nombre_personaje";

    /**
     * Nombre de la tabla reparto y sus columnas
     * id;nombre;imagen;URL_imdb
     */
    public static final String TABLA_REPARTO = "tabla_reparto";

    public static final String COLUMNA_ID_REPARTO = "id_reparto";
    public static final String COLUMNA_NOMBRE_ACTOR = "nombre_actor";
    public static final String COLUMNA_IMAGEN_ACTOR = "URL_imagen_actor";
    public static final String COLUMNA_URL_imdb = "URL_imdb_actor";


    /**
     * Script para crear la base datos en SQL
     */
    private static final String CREATE_TABLA_PELICULAS = "create table if not exists " + TABLA_PELICULAS
            + "( " +
            COLUMNA_ID_PELICULAS + " " + "integer primary key, " +
            COLUMNA_TITULO_PELICULAS + " text not null, " +
            COLUMNA_ARGUMENTO_PELICULAS + " text, " +
            COLUMNA_CATEGORIA_PELICULAS + " text not null, " +
            COLUMNA_DURACION_PELICULAS + " text, " +
            COLUMNA_FECHA_PELICULAS + " text, " +
            COLUMNA_CARATULA_PELICULAS + " text, " +
            COLUMNA_FONDO_PELICULAS + " text, " +
            COLUMNA_TRAILER_PELICULAS + " text" +
            ");";

    private static final String CREATE_TABLA_PELICULAS_REPARTO = "create table if not exists " + TABLA_PELICULAS_REPARTO
            + "( " +
            COLUMNA_ID_REPARTO + " " + "integer, " +
            COLUMNA_ID_PELICULAS + " integer, " +
            COLUMNA_PERSONAJE + " text not null, " +
            " primary key (" + COLUMNA_ID_REPARTO + ", " + COLUMNA_ID_PELICULAS + ")"
            + ");";


    private static final String CREATE_TABLA_REPARTO = "create table if not exists " + TABLA_REPARTO
            + "( " +
            COLUMNA_ID_REPARTO + " " + "integer primary key, " +
            COLUMNA_NOMBRE_ACTOR + " text not null, " +
            COLUMNA_IMAGEN_ACTOR + " text, " +
            COLUMNA_URL_imdb + " text"
            + ");";

    /**
     * Script para borrar la base de datos (SQL)
     */
    private static final String DATABASE_DROP_PELICULAS = "DROP TABLE IF EXISTS " + TABLA_PELICULAS;
    private static final String DATABASE_DROP_PELICULAS_REPARTO = "DROP TABLE IF EXISTS " + TABLA_PELICULAS_REPARTO;
    private static final String DATABASE_DROP_REPARTO = "DROP TABLE IF EXISTS " + TABLA_REPARTO;


    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                      int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //invocamos execSQL pq no devuelve ningún tipo de dataset
        db.execSQL(CREATE_TABLA_PELICULAS);
        db.execSQL(CREATE_TABLA_PELICULAS_REPARTO);
        db.execSQL(CREATE_TABLA_REPARTO);

        Log.i("ONCREATE", "EJECUTO CREACION");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DATABASE_DROP_PELICULAS);
        db.execSQL(DATABASE_DROP_PELICULAS_REPARTO);
        db.execSQL(DATABASE_DROP_REPARTO);
        this.onCreate(db);

    }
}
