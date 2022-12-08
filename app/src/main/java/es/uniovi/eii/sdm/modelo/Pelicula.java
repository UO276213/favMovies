package es.uniovi.eii.sdm.modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class Pelicula implements Parcelable {
        int id;
        String titulo;
        String argumento;
        Categoria categoria;
        String duracion;
        String fecha;

    // Ampliamos propiedades de Pelicula
        String urlCaratula;
        String urlFondo;
        String urlTrailer;

    public Pelicula(int id, String titulo, String argumento, Categoria categoria, String duracion, String fecha,
                    String caratula, String fondo, String trailer) {
        //Incluimos el id en el constructor
        this.id = id;
        this.titulo = titulo;
        this.argumento = argumento;
        this.categoria = categoria;
        this.duracion = duracion;
        this.fecha = fecha;

        this.urlCaratula= caratula;
        this.setUrlFondo(fondo);
        this.urlTrailer= trailer;


    }

    public Pelicula(){}

    protected Pelicula(Parcel in) {
        //Incluimos el Id en los métodos heredados de Parcelable
        id = in.readInt();
        titulo = in.readString();
        argumento = in.readString();
        categoria = in.readParcelable(Categoria.class.getClassLoader());
        duracion = in.readString();
        fecha = in.readString();

        urlCaratula= in.readString();
        setUrlFondo(in.readString());
        urlTrailer= in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //Incluimos el Id en los métodos heredados de Parcelable
        dest.writeInt(id);
        dest.writeString(titulo);
        dest.writeString(argumento);
        dest.writeParcelable(categoria, flags);
        dest.writeString(duracion);
        dest.writeString(fecha);

        dest.writeString(urlCaratula);
        dest.writeString(getUrlFondo());
        dest.writeString(urlTrailer);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Pelicula> CREATOR = new Creator<Pelicula>() {
        @Override
        public Pelicula createFromParcel(Parcel in) {
            return new Pelicula(in);
        }

        @Override
        public Pelicula[] newArray(int size) {
            return new Pelicula[size];
        }
    };

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getArgumento() {
        return argumento;
    }

    public void setArgumento(String argumento) {
        this.argumento = argumento;
    }

    public Categoria getCategoria() {
        return categoria;
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

    public String getUrlCaratula() {
        return urlCaratula;
    }

    public String getUrlFondo() {
        return urlFondo;
    }

    public String getUrlTrailer() {
        return urlTrailer;
    }

    public void setUrlCaratula(String urlCaratula) {
        this.urlCaratula = urlCaratula;
    }

    public void setUrlFondo(String urlFondo) {
        this.urlFondo = urlFondo;
    }

    public void setUrlTrailer(String urlTrailer) {
        this.urlTrailer = urlTrailer;
    }


    @Override
    public String toString() {
//        return "Pelicula{" +
//                "titulo='" + titulo + '\'' +
//                ", argumento='" + argumento + '\'' +
//                ", duracion='" + duracion + '\'' +
//                ", fecha='" + fecha + '\'' +
//                '}';
        return "Pelicula{" +
                "titulo='" + titulo + '\'' +
                ", duracion='" + duracion + '\'' +
                ", fecha='" + fecha + '\'' +
                '}';
    }

    /*
       Encapsulamos el id
        */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
