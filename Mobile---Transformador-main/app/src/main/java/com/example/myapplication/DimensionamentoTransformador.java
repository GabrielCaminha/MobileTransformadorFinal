package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DimensionamentoTransformador extends AppCompatActivity {

    private Spinner spinnerFrequencia;
    private EditText textTensaoPrimaria;
    private EditText textTensaoSecundaria;
    private EditText potenciaCarga;

    private TextView avisos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dimensionamento_transformador);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textTensaoPrimaria = findViewById(R.id.textTensaoPrimaria);
        textTensaoSecundaria = findViewById(R.id.textTensaoSecundaria);
        potenciaCarga = findViewById(R.id.potenciaCarga);

        avisos = findViewById(R.id.avisos2);

        // Spinner Tipo de Circuito
        String[] tipoCircuito = new String[] {"60 Hz", "50 Hz", "Frequência do Transformador"};
        HintAdapter hintTipoCircuito =new HintAdapter(this,android.R.layout.simple_list_item_1, tipoCircuito);
        spinnerFrequencia = findViewById(R.id.spinnerFrequencia);
        spinnerFrequencia.setAdapter(hintTipoCircuito);
        spinnerFrequencia.setSelection(hintTipoCircuito.getCount());
    }

    public void calcular(View view){
        if (verificarTodosCampos()){
            Intent telaDimensionamento = new Intent(this, ResultadoDimensionamento.class);
            telaDimensionamento.putExtra("frequencia", spinnerFrequencia.getSelectedItem().toString());
            telaDimensionamento.putExtra("tensaoPrimaria", textTensaoPrimaria.getText().toString());
            telaDimensionamento.putExtra("tensaoSecundaria", textTensaoSecundaria.getText().toString());
            telaDimensionamento.putExtra("potenciaCarga", potenciaCarga.getText().toString());

            startActivity(telaDimensionamento);
        }

    }
    private boolean verificarEditText(EditText editText) {
        return editText != null && !editText.getText().toString().trim().isEmpty();
    }

    private boolean verificarSpinner(Spinner spinner) {
        return (spinner != null && spinner.getSelectedItemPosition() != AdapterView.INVALID_POSITION) && spinner.getSelectedItemPosition() != spinner.getCount();
    }

    private boolean verificarTodosCampos() {
        if (!verificarEditText(textTensaoPrimaria) || !verificarEditText(textTensaoSecundaria) || !verificarEditText(potenciaCarga)) {
            avisos.setText("Algum campo está vazio.");
            return false;
        }
        if (!verificarSpinner(spinnerFrequencia)) {
            avisos.setText("Algum campo está vazio.");
            return false;
        }
        return true;
    }
}