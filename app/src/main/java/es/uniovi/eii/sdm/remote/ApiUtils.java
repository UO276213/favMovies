package es.uniovi.eii.sdm.remote;

import retrofit2.Retrofit;

public class ApiUtils {
    public static final String LANGUAGE = "es-ES";
    public static final String API_KEY = "6bc4475805ebbc4296bcfa515aa8df08";

    public static ThemoviedbApi createThemoviedbApi() {
        Retrofit retrofit= RetrofitClient.getClient(ThemoviedbApi.BASE_URL);

        return retrofit.create(ThemoviedbApi.class);
    }


}
