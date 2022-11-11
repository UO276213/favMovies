package es.uniovi.eii.favmovies.datos.server;

import java.util.ArrayList;
import java.util.List;

import es.uniovi.eii.favmovies.modelos.Categoria;
import es.uniovi.eii.favmovies.modelos.Pelicula;

public class ServerDataMapper {
    private static final String BASE_URL_IMG= "https://image.tmdb.org/t/p/";
    private static final String IMG_W342= "w342";
    private static final String IMG_ORIGINAL= "original";

    /**
     * Convierte datos de la API de cada película a datos del dominio
     * Pelicula     <--     MovieData
     *
     * int id;              <-- Integer id
     * String titulo;       <-- title
     * String argumento;    <-- overview
     * Categoria categoria; No lo rellenamos ya que sólo aparece id
     * String duracion;     No tenemos este dato
     * String fecha;        <-- releaseDate (yyyy-mm-dd)
     *
     * String urlCaratula;  <-- posterPath, hay que completar la url
     * String urlFondo;     <-- backdropPath
     * String urlTrailer;   No tenemos este dato
     *
     * @param movieDetails
     * @return
     */
    public static List<Pelicula> convertMovieListToDomain(List<MovieDetails> movieDetails) {
        ArrayList<Pelicula> lpeliculas= new ArrayList<>();

        for (MovieDetails peliApi: movieDetails) {
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
}
