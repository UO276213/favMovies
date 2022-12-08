package es.uniovi.eii.sdm.datos.server;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import es.uniovi.eii.sdm.datos.server.credits.Cast;
import es.uniovi.eii.sdm.datos.server.moviedetail.MovieDetail;
import es.uniovi.eii.sdm.datos.server.movielist.MovieData;
import es.uniovi.eii.sdm.datos.server.movielist.MovieListResult;
import es.uniovi.eii.sdm.modelo.Actor;
import es.uniovi.eii.sdm.modelo.Categoria;
import es.uniovi.eii.sdm.modelo.Pelicula;

public class ServerDataMapper {
    private static final String BASE_URL_IMG= "https://image.tmdb.org/t/p/";
    private static final String IMG_W342= "w342";
    private static final String IMG_ORIGINAL= "original";
    private static final String BASE_URL_YOUTUBE= "https://youtu.be/";
    private static final String SITE_YOUTUBE = "YouTube";

    public static List<Pelicula> convertToDomain(MovieListResult peliculas) {
        return convertMovieListToDomain(peliculas.getMovieData());
    }

    /**
     * Convierte datos de cada pelicula de la API: MovieData
     * en datos del dominio: Pelicula
     * @param movieData lista de películas de la API
     * @return lista de peliculas del dominio
     *
     * id               <-- id
     * titulo           <-- title
     * argumento        <-- overview
     * categoria        <-- genreIds (es una lista de id de generos)
     * duracion
     * fecha            <-- releaseDate
     * urlCaratula      <-- posterPath, completamos la url
     * urlFondo         <-- backdropPath
     * urlTrailer
     */
    public static List<Pelicula> convertMovieListToDomain(List<MovieData> movieData) {
        ArrayList<Pelicula> lpeliculas= new ArrayList<Pelicula>();

        for (MovieData peliApi: movieData) {
            String urlCaratula;
            String urlFondo;

            if (peliApi.getPosterPath()==null) {
                urlCaratula= "";
            } else {
                urlCaratula= BASE_URL_IMG + IMG_W342 + peliApi.getPosterPath();
            }
            if (peliApi.getBackdropPath()==null) {
                urlFondo= "";
            } else {
                urlFondo = BASE_URL_IMG + IMG_ORIGINAL + peliApi.getBackdropPath();
            }

            lpeliculas.add(new Pelicula(peliApi.getId(),
                    peliApi.getTitle(),
                    peliApi.getOverview(),
                    new Categoria("",""),
                    "",
                    peliApi.getReleaseDate(),
                    urlCaratula,
                    urlFondo,
                    ""
                    ));
        }

        return lpeliculas;
    }

    /**
     * Partiendo de un objeto pelicula ya existente
     * Convierte datos de detalle de la película al dominio
     * y completa el objeto pelicula
     * categoria        <-- primer género que encontramos (si no hay -> vacío)
     * duracion         <-- conversión desde minutos(int) -> formato 1h 23m
     * urlTrailer       <-- url trailer en formato youtu.be
     *
     * @param data
     * @param pelicula
     */
    public static void convertMovieDetailToDomain(MovieDetail data, Pelicula pelicula) {
        // con los detalles del servicio vamos a rellenar los datos que nos faltan
        // en pelicula

        if (data.getGenres().size()>0) {
            pelicula.setCategoria(
                    new Categoria(data.getGenres().get(0).getName(), ""));
        }
        int duracion= data.getRuntime();
        String duracionCad= (data.getRuntime()/60>0?(data.getRuntime()/60+"h "):"")+data.getRuntime()%60+"m";
        Log.d("ShowMovie","Duracion= "+duracion+" cad->"+duracionCad);
        pelicula.setDuracion(duracionCad);
        String youTubeUrl= "";
        // comprueba si hay al menos un vídeo asociado
        if (data.getVideos().getResults().size()>0)
            // Si el primer vídeo es de youtube obtiene el código y crea con el la url
            if (data.getVideos().getResults().get(0).getSite().equals(SITE_YOUTUBE))
                youTubeUrl= BASE_URL_YOUTUBE + data.getVideos().getResults().get(0).getKey();
        Log.d("ShowMovie","youTubeUrl= "+youTubeUrl);
        pelicula.setUrlTrailer(youTubeUrl);

    }

    /**
     * Convierte datos de cada miembro del reparto de la API: Cast
     * en datos del dominio: Actor
     * @param castList
     * @return
     *
     * id (no está en el constructor)   <-- castId
     * nombre_actor                     <-- name
     * nombre_personaje                 <-- character
     * imagen                           <-- profilePath
     * imdb (no tenemos nada equivalente)
     */
    public static List<Actor> convertCastListToDomain(List<Cast> castList) {
        ArrayList<Actor> lreparto= new ArrayList<Actor>();

        for (Cast cast: castList) {
            // Actor(String nombre_actor, String nombre_personaje, String imagen, String imdb)
            lreparto.add(new Actor(cast.getName(),
                    cast.getCharacter(),
                    (cast.getProfilePath()!=null)?(BASE_URL_IMG + IMG_W342 + cast.getProfilePath()):"",
                    ""
                    ));
        }

        return lreparto;
    }
}
