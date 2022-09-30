package es.uniovi.eii.favmovies.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Conexion {
    private final Context mContexto;

    public Conexion(Context applicationContext) {
        mContexto = applicationContext;
    }

    public boolean CompruebaConexion() {

        ConnectivityManager connectivityManager = (ConnectivityManager) mContexto.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        boolean connectado = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return connectado;
    }
}
