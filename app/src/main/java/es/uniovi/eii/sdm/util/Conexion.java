package es.uniovi.eii.sdm.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Conexion {
    private Context mContexto;


    //Contexto de la conexión
    public Conexion(Context mContext) {
        mContexto=mContext;
    }

    public boolean CompruebaConexion(){
        boolean conectado= false;
        //ConnectivityManager es la clase que pregunta por el estado de la conexión de las redes
        //mContext.getSystemService(Context.CONNECTIVITY_SERVICE): Instancia de la clase ConnectivityManager
        //getSystemService: Permite manejar servicios del sistema. El parámetro que se le pasa es la clase del servicio que
        //se desea manejar


        ConnectivityManager connectivityManager = (ConnectivityManager)
                mContexto.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        conectado = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        /* // Requiere versión Lollypop de Android
        Network[] networks = connectivityManager.getAllNetworks();
        NetworkInfo networkInfo;
        for (Network mNetwork : networks) {
            networkInfo = connectivityManager.getNetworkInfo(mNetwork);
            if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                conectado = true;
            }


        }*/
        return conectado;

    }



}
