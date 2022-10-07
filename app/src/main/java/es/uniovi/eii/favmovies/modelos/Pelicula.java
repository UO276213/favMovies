package es.uniovi.eii.favmovies.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Pelicula implements Parcelable {

    private String title;
    private String argument;
    private Categoria category;
    private String duration;
    private String date;

    public Pelicula(String titulo, String argument, Categoria category, String duration, String date) {
        this.title = titulo;
        this.argument = argument;
        this.category = category;
        this.duration = duration;
        this.date = date;
    }

    protected Pelicula(Parcel in) {
        title = in.readString();
        argument = in.readString();
        category = in.readParcelable(Categoria.class.getClassLoader());
        duration = in.readString();
        date = in.readString();
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

    public void setArgument(String argument) {
        this.argument = argument;
    }

    public void setCategory(Categoria category) {
        this.category = category;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitulo(String titulo) {
        this.title = titulo;
    }

    public String getTitulo() {
        return title;
    }

    public String getArgument() {
        return argument;
    }

    public Categoria getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "Pelicula{" +
                "titulo='" + title + '\'' +
                ", argumento='" + argument + '\'' +
                ", categoria=" + category +
                ", duracion='" + duration + '\'' +
                ", fecha='" + date + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(argument);
        parcel.writeParcelable(category, i);
        parcel.writeString(duration);
        parcel.writeString(date);
    }
}
