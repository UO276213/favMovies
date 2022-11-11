package es.uniovi.eii.favmovies.remote;

import retrofit2.Retrofit;

public class ApiUtils {
    public static final String LANGUAGE = "es-ES";
    public static final String API_KEY = "fac342fca72cc0b711658b8e7f7c5669";

    public static ThemoviedbApi createThemoviedbApi() {
        Retrofit retrofit= RetrofitClient.getClient(ThemoviedbApi.BASE_URL);

        return retrofit.create(ThemoviedbApi.class);
    }


}
