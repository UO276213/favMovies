package es.uniovi.eii.sdm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {
    //Declaro el objeto sharedPreference como atributo de la clase
    SharedPreferences sharedPreferences;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //deshabiltiar la opción de ir hacia atrás con la flecha (por ahora)
            actionBar.setDisplayHomeAsUpEnabled(true);
        }




    }
    @Override
    protected void onPause() {
        super.onPause();

       /* sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this *//* Activity context *//*);
        name = sharedPreferences.getString("keyCategoria", "");
        Log.i ("Categoria", name);



        //establezco la categoría


        MainRecycler.filtrocategoria=name;
*/


    }






    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }


    }
}