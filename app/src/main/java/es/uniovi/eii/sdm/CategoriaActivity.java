package es.uniovi.eii.sdm;

import androidx.appcompat.app.AppCompatActivity;
import es.uniovi.eii.sdm.modelo.Categoria;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CategoriaActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);

       //Recepción de datos
        Intent intent= getIntent();
        int posCategoria= intent.getIntExtra(NewMovie.POS_CATEGORIA_SELECCIONADA, 0);
        Categoria categEntrada=null;

        if (posCategoria>0)
            categEntrada=intent.getParcelableExtra(NewMovie.CATEGORIA_SELECCIONADA);

        TextView textViewCrea= (TextView)findViewById(R.id.textViewCrea);
        final EditText editNomCategoria= (EditText)findViewById(R.id.editNomCategoria);
        final EditText editDescripcion= (EditText)findViewById(R.id.editDescripcion);
        // Recuperamos referencia al botón
        Button btnOk= (Button)findViewById(R.id.btnOk);
        Button btnCancel= (Button)findViewById(R.id.btnCancel);

        // Ponemos etiqueta título en función de si hay que crear / modificar categoría
        if (posCategoria==0)
            textViewCrea.setText(R.string.creacion_categoria);
        else {
            textViewCrea.setText(R.string.modificacion_categoria);
            editNomCategoria.setText(categEntrada.getNombre());
            editDescripcion.setText(categEntrada.getDescripcion());
            // no dejamos cambiar el nombre de la categoría
            editNomCategoria.setEnabled(false);
        }

        // Definir el observer para el evento click del botón guardar
        // Definimos listener
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Categoria categSalida = new Categoria(editNomCategoria.getText().toString(),
                        editDescripcion.getText().toString());

                Intent intentResultado= new Intent();
                intentResultado.putExtra(NewMovie.CATEGORIA_MODIFICADA,categSalida);
                setResult(RESULT_OK,intentResultado);
                finish();
            }
        });

        // Listener para el evento click del botón cancelar
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

    }


}