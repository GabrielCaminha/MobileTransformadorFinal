package com.example.myapplication;

import android.annotation.SuppressLint;
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

public class ParametrosTransformador extends AppCompatActivity {
    //Dados do Transformador
    private EditText espirasPrimario;
    private EditText espirasSecundario;
    private Spinner tipoCircuito;
    private Spinner referencia;

    //Conjunto de Dados A
    private Spinner tipoEnsaioA;
    private Spinner ladoEnsaioA;
    private EditText tensaoA;
    private EditText correnteA;
    private EditText potenciaA;

    // Conjunto de Dados B
    private Spinner tipoEnsaioB;
    private Spinner ladoEnsaioB;
    private EditText tensaoB;
    private EditText correnteB;
    private EditText potenciaB;

    private TextView avisos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_parametros_transformador);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        createSpinners();

        espirasPrimario = findViewById(R.id.textEspirasPrimario);
        espirasSecundario = findViewById(R.id.textEspirasSecundario);
        tipoCircuito = findViewById(R.id.spinnerCircuito);
        referencia = findViewById(R.id.spinnerReferido);

        tipoEnsaioA = findViewById(R.id.spinnerTipoEnsaioA);
        ladoEnsaioA = findViewById(R.id.spinnerLadoEnsaioA);
        tensaoA = findViewById(R.id.textTensaoA);
        correnteA = findViewById(R.id.textCorrenteA);
        potenciaA = findViewById(R.id.textPotenciaA);

        tipoEnsaioB = findViewById(R.id.spinnerTipoEnsaioB);
        ladoEnsaioB = findViewById(R.id.spinnerLadoEnsaioB);
        tensaoB = findViewById(R.id.textTensaoB);
        correnteB = findViewById(R.id.textCorrenteB);
        potenciaB = findViewById(R.id.textPotenciaB);

        avisos = findViewById(R.id.avisos);
    }

    public void calcular(View view){
        if ((tipoEnsaioA.getSelectedItem().toString().equals(tipoEnsaioB.getSelectedItem().toString())) && !tipoEnsaioA.getSelectedItem().toString().equals("Tipo de Ensaio")){
            avisos.setText(String.format("Os 2 tipos de Ensaio são %s", tipoEnsaioA.getSelectedItem().toString()));
        } else {
            if (verificarTodosCampos()){
                Intent telaResultado = new Intent(this, ResultadoTransformador.class);
                telaResultado.putExtra("espirasPrimario", espirasPrimario.getText().toString());
                telaResultado.putExtra("espirasSecundario", espirasSecundario.getText().toString());
                telaResultado.putExtra("tipoCircuito", tipoCircuito.getSelectedItem().toString());
                telaResultado.putExtra("referencia", referencia.getSelectedItem().toString());

                telaResultado.putExtra("tipoEnsaioA", tipoEnsaioA.getSelectedItem().toString());
                telaResultado.putExtra("ladoEnsaioA", ladoEnsaioA.getSelectedItem().toString());
                telaResultado.putExtra("tensaoA", tensaoA.getText().toString());
                telaResultado.putExtra("correnteA", correnteA.getText().toString());
                telaResultado.putExtra("potenciaA", potenciaA.getText().toString());

                telaResultado.putExtra("tipoEnsaioB", tipoEnsaioB.getSelectedItem().toString());
                telaResultado.putExtra("ladoEnsaioB", ladoEnsaioB.getSelectedItem().toString());
                telaResultado.putExtra("tensaoB", tensaoB.getText().toString());
                telaResultado.putExtra("correnteB", correnteB.getText().toString());
                telaResultado.putExtra("potenciaB", potenciaB.getText().toString());

                startActivity(telaResultado);
            }

        }
    }

    private void createSpinners(){
        // Spinner Tipo de Circuito
        String[] tipoCircuito = new String[] {"T - Circuito em T", "L - Circuito em L", "Série - Circuito em Série", "Tipo de Circuito Equivalente"};
        HintAdapter hintTipoCircuito =new HintAdapter(this,android.R.layout.simple_list_item_1, tipoCircuito);
        Spinner spinnerCircuito = findViewById(R.id.spinnerCircuito);
        spinnerCircuito.setAdapter(hintTipoCircuito);
        spinnerCircuito.setSelection(hintTipoCircuito.getCount());

        // Spinner de Referência
        String[] referencia = new String[] {"Primário", "Secundário", "Selecione a Referência"};
        HintAdapter hintReferencia = new HintAdapter(this,android.R.layout.simple_list_item_1, referencia);
        Spinner spinnerReferencia = findViewById(R.id.spinnerReferido);
        spinnerReferencia.setAdapter(hintReferencia);
        spinnerReferencia.setSelection(hintReferencia.getCount());

        // Spinner de Tipo Ensaio A
        String[] tipoEnsaio = new String[] {"Curto-Circuito", "Circuito Aberto", "Tipo de Ensaio"};
        HintAdapter hintTipoEnsaio = new HintAdapter(this,android.R.layout.simple_list_item_1, tipoEnsaio);
        Spinner spinnerTipoEnsaioA = findViewById(R.id.spinnerTipoEnsaioA);
        spinnerTipoEnsaioA.setAdapter(hintTipoEnsaio);
        spinnerTipoEnsaioA.setSelection(hintTipoEnsaio.getCount());

        // Spinner de Lado Ensaio A
        String[] ladoEnsaio = new String[] {"Baixa Tensão", "Alta Tensão", "Lado do Ensaio"};
        HintAdapter hintLadoEnsaio = new HintAdapter(this,android.R.layout.simple_list_item_1, ladoEnsaio);
        Spinner spinnerLadoEnsaioA = findViewById(R.id.spinnerLadoEnsaioA);
        spinnerLadoEnsaioA.setAdapter(hintLadoEnsaio);
        spinnerLadoEnsaioA.setSelection(hintLadoEnsaio.getCount());

        // Spinner de Tipo Ensaio B
        Spinner spinnerTipoEnsaioB = findViewById(R.id.spinnerTipoEnsaioB);
        spinnerTipoEnsaioB.setAdapter(hintTipoEnsaio);
        spinnerTipoEnsaioB.setSelection(hintTipoEnsaio.getCount());

        // Spinner de Lado Ensaio B
        Spinner spinnerLadoEnsaioB = findViewById(R.id.spinnerLadoEnsaioB);
        spinnerLadoEnsaioB.setAdapter(hintLadoEnsaio);
        spinnerLadoEnsaioB.setSelection(hintLadoEnsaio.getCount());
    }

    private boolean verificarEditText(EditText editText) {
        return editText != null && !editText.getText().toString().trim().isEmpty();
    }

    private boolean verificarSpinner(Spinner spinner) {
        return (spinner != null && spinner.getSelectedItemPosition() != AdapterView.INVALID_POSITION) && spinner.getSelectedItemPosition() != spinner.getCount();
    }

    @SuppressLint("SetTextI18n")
    private boolean verificarTodosCampos() {
        if (!verificarEditText(espirasPrimario) || !verificarEditText(espirasSecundario)
                || !verificarEditText(tensaoA) || !verificarEditText(correnteA) || !verificarEditText(potenciaA)
                || !verificarEditText(tensaoB) || !verificarEditText(correnteB) || !verificarEditText(potenciaB)) {
            avisos.setText("Algum campo está vazio.");
            return false;
        }

        if (!verificarSpinner(tipoCircuito) || !verificarSpinner(referencia)
                || !verificarSpinner(tipoEnsaioA) || !verificarSpinner(ladoEnsaioA)
                || !verificarSpinner(tipoEnsaioB) || !verificarSpinner(ladoEnsaioB)) {
            avisos.setText("Algum campo está vazio.");
            return false;
        }

        if (ladoEnsaioA.getSelectedItem().toString().equals(ladoEnsaioB.getSelectedItem().toString())){
            avisos.setText("Os 2 Ensaios são de " + ladoEnsaioA.getSelectedItem().toString());
            return false;
        }

        return true;
    }

}