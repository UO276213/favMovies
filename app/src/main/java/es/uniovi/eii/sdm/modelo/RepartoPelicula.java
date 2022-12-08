package es.uniovi.eii.sdm.modelo;

/*
Clase para trabajar de manera encapsulada con los registros de la tabla Reparto_Película
 */
public class RepartoPelicula {
    private int id_reparto;
    private int id_pelicula;
    private String nombre_personaje;

    public RepartoPelicula(int id_reparto, int id_pelicula, String nombre_personaje){
        this.id_reparto = id_reparto;
        this.id_pelicula = id_pelicula;
        this.nombre_personaje = nombre_personaje;
    }

    public RepartoPelicula(){    }

    public int getId_reparto() {
        return id_reparto;
    }

    public void setId_reparto(int id_reparto) {
        this.id_reparto = id_reparto;
    }

    public int getId_pelicula() {
        return id_pelicula;
    }

    public void setId_pelicula(int id_pelicula) {
        this.id_pelicula = id_pelicula;
    }

    public String getNombre_personaje() {
        return nombre_personaje;
    }

    public void setNombre_personaje(String nombre_personaje) {
        this.nombre_personaje = nombre_personaje;
    }
}
