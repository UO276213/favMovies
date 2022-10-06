package es.uniovi.eii.favmovies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import es.uniovi.eii.favmovies.modelos.Categoria;

public class CategoriaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);

        Intent intent = getIntent();
        int posCategoria = intent.getIntExtra(MainActivity.POS_CATEGORIA_SELEECIONADA, 0);

        Categoria categoriaEntrada = null;

        if (posCategoria > 0)
            categoriaEntrada = intent.getParcelableExtra(MainActivity.CATEGORIA_SELECCIONADA);

        TextView textViewCrea = findViewById(R.id.textViewCreo);
        final EditText editNomCategoria = findViewById(R.id.editNomCategoria);
        final EditText editDescripcion = findViewById(R.id.editDescripcion);

        Button btnOk = findViewById(R.id.btnOk);
        Button btnCancel = findViewById(R.id.btnCancel);

        if (posCategoria == 0)
            textViewCrea.setText(R.string.new_catelogry_label);
        else {
            textViewCrea.setText(R.string.modify_category_existent);
            editNomCategoria.setText(categoriaEntrada.getNombre());
            editDescripcion.setText(categoriaEntrada.getDescripcion());

            editNomCategoria.setEnabled(false);
        }


        btnOk.setOnClickListener(view -> {
            Categoria categoriaSalida = new Categoria(editNomCategoria.getText().toString(), editDescripcion.getText().toString());
            Intent intentResult = new Intent();
            intentResult.putExtra(MainActivity.CATEGORIA_MODIFICADA, categoriaSalida);

            setResult(RESULT_OK, intentResult);
            finish();
        });

        btnCancel.setOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();
        });

    }
}