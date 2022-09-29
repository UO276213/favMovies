package es.uniovi.eii.favmovies;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    String selectedCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.app_title);

        FloatingActionButton saveBtn = findViewById(R.id.saveBtn);

        //Definimos listnener
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validación de cmapos

                if (validarCampos())
                    Snackbar.make(findViewById(R.id.mainLayout), R.string.save_msg, Snackbar.LENGTH_LONG).show();
            }
        });

        Button modifyCategoryBtn = findViewById(R.id.modifyBtnCategory);
        modifyCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spinner spinner = findViewById(R.id.spinnerCategory);
                Snackbar msgCreaCategory;
                if (spinner.getSelectedItemPosition() == 0) {
                    selectedCategory = "";
                    msgCreaCategory = Snackbar.make(findViewById(R.id.mainLayout), R.string.create_category_msg, Snackbar.LENGTH_LONG);
                } else {
                    selectedCategory = spinner.getSelectedItem().toString();
                    msgCreaCategory = Snackbar.make(findViewById(R.id.mainLayout), R.string.modify_category, Snackbar.LENGTH_LONG);
                }

                // Accción de cancelar
                msgCreaCategory.setAction(android.R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(findViewById(R.id.mainLayout), R.string.cancel_action, Snackbar.LENGTH_LONG).show();
                    }
                });

                msgCreaCategory.show();
            }
        });
    }

    private boolean validarCampos() {
        EditText titleInput = (EditText) findViewById(R.id.titleTxtInput);
        EditText synopsisInput = (EditText) findViewById(R.id.synopsisTxtInput);
        EditText durationInput = (EditText) findViewById(R.id.durationTxtInput);
        EditText dateInput = (EditText) findViewById(R.id.dateTxtInput);

        if (titleInput.getText().toString().isEmpty()) {
            Snackbar.make(findViewById(R.id.mainLayout), "Falta el título", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (synopsisInput.getText().toString().isEmpty()) {
            Snackbar.make(findViewById(R.id.mainLayout), "Falta la sinopsis", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (selectedCategory.isEmpty()) {
            Snackbar.make(findViewById(R.id.mainLayout), "Categoría sin especificar", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (durationInput.getText().toString().isEmpty()) {
            Snackbar.make(findViewById(R.id.mainLayout), "Falta la duración", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (dateInput.getText().toString().isEmpty()) {
            Snackbar.make(findViewById(R.id.mainLayout), "Falta la fecha", Snackbar.LENGTH_LONG).show();
            return false;
        }


        return true;
    }
}