package es.uniovi.eii.sdm.remote;

import es.uniovi.eii.sdm.datos.server.credits.Credits;
import es.uniovi.eii.sdm.datos.server.moviedetail.MovieDetail;
import es.uniovi.eii.sdm.datos.server.movielist.MovieListResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ThemoviedbApi {
    String BASE_URL="https://api.themoviedb.org/3/";

    // Pide una lista predeterminada (popular, top_ratet, upcoming) a la API
    // El resultado para estas peticiones tiene la misma estructura
    // Lista de películas populares
    // https://api.themoviedb.org/3/movie/popular?api_key={API_KEY}&language=es-ES&page=1
    @GET("movie/{lista}")
    Call<MovieListResult> getListMovies(
            @Path("lista") String lista,    // popular, top_ratet, upcoming
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );

    // Petición de búsqueda de películas según un criterio
    // El resultado tiene la misma estructura que el de getListMovies
    // El rey leon
    // https://api.themoviedb.org/3/search/movie?api_key={API_KEY}&language=es-ES&query=El+rey+leon
    @GET("search/movie")
    Call<MovieListResult> searchMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page,
            @Query("query") String query
    );

    // Petición de detalles de una película, incluidos vídeos
    // Detalles de una película el rey leon + videos
    // https://api.themoviedb.org/3/movie/420818?api_key={API_KEY}&language=es-ES&append_to_response=videos
    @GET("movie/{id}")
    Call<MovieDetail> getMovieDetail(
            @Path("id") int id,
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("append_to_response") String appendToResponse  // videos
    );

    // Petición de créditos de la película
    // Lista del reparto y equipo técnico de la película (créditos)
    // https://api.themoviedb.org/3/movie/420818/credits?api_key=6bc4475805ebbc4296bcfa515aa8df08&language=es-ES
    //        --> creditos el rey leon.json
    @GET("movie/{id}/credits")
    Call<Credits> getMovieCredits(
            @Path("id") int id,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

}
