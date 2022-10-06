package es.uniovi.eii.favmovies.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Categoria implements Parcelable {

    private String nombre;
    private String descripcion;

    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    protected Categoria(Parcel in) {
        nombre = in.readString();
        descripcion = in.readString();
    }

    public static final Creator<Categoria> CREATOR = new Creator<Categoria>() {
        @Override
        public Categoria createFromParcel(Parcel in) {
            return new Categoria(in);
        }

        @Override
        public Categoria[] newArray(int size) {
            return new Categoria[size];
        }
    };

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nombre);
        parcel.writeString(descripcion);
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
