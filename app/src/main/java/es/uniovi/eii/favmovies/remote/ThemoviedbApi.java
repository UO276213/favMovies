package es.uniovi.eii.favmovies.remote;

import es.uniovi.eii.favmovies.datos.server.MovieListResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ThemoviedbApi {
    public static final String BASE_URL= "https://api.themoviedb.org/3/";

    // https://api.themoviedb.org/3/movie/popular?api_key=6bc4475805ebbc4296bcfa515aa8df08&language=es-ES&page=1
    @GET("movie/{lista}")
    Call<MovieListResult> getListMovies(
            @Path("lista") String lista,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

}
