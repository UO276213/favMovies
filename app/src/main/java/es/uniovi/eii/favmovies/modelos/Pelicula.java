package es.uniovi.eii.favmovies.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Pelicula implements Parcelable {

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
    private String title;
    private String argument;
    private Categoria category;
    private String duration;
    private String date;
    private String coverUrl;
    private String backgroundUrl;
    private String videoUrl;
    private int id;

    public Pelicula(int id, String titulo, String argument, Categoria category, String duration, String date, String coverUrl, String backgroundUrl, String videoUrl) {
        this.id = id;
        this.title = titulo;
        this.argument = argument;
        this.category = category;
        this.duration = duration;
        this.date = date;
        this.coverUrl = coverUrl;
        this.backgroundUrl = backgroundUrl;
        this.videoUrl = videoUrl;
    }

    public Pelicula(String titulo, String argument, Categoria category, String duration, String date, String coverUrl, String backgroundUrl, String videoUrl) {
        this.title = titulo;
        this.argument = argument;
        this.category = category;
        this.duration = duration;
        this.date = date;
        this.coverUrl = coverUrl;
        this.backgroundUrl = backgroundUrl;
        this.videoUrl = videoUrl;
    }

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
        coverUrl = in.readString();
        backgroundUrl = in.readString();
        videoUrl = in.readString();

    }

    public Pelicula() { }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
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

    public String getArgument() {
        return argument;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    public Categoria getCategory() {
        return category;
    }

    public void setCategory(Categoria category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Pelicula{" +
                "title='" + title + '\'' +
                ", argument='" + argument + '\'' +
                ", category=" + category +
                ", duration='" + duration + '\'' +
                ", date='" + date + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", backgroundUrl='" + backgroundUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
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
        parcel.writeString(coverUrl);
        parcel.writeString(backgroundUrl);
        parcel.writeString(videoUrl);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
