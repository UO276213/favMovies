package es.uniovi.eii.favmovies.modelos;

public class Pelicula {

    private String titulo;
    private String argumento;
    private Categoria categoria;
    private String duracion;
    private String fecha;

    public Pelicula(String titulo, String argumento, Categoria categoria, String duracion, String fecha) {
        this.titulo = titulo;
        this.argumento = argumento;
        this.categoria = categoria;
        this.duracion = duracion;
        this.fecha = fecha;
    }

    public void setArgumento(String argumento) {
        this.argumento = argumento;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getArgumento() {
        return argumento;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    @Override
    public String toString() {
        return "Pelicula{" +
                "titulo='" + titulo + '\'' +
                ", argumento='" + argumento + '\'' +
                ", categoria=" + categoria +
                ", duracion='" + duracion + '\'' +
                ", fecha='" + fecha + '\'' +
                '}';
    }
}
