package com.example.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class ResultadosCurvaMagnetizacao extends AppCompatActivity {

    private GraphView graph;
    private TextView resultadoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultados_curva_magnetizacao); // Atualize para o novo layout

        graph = findViewById(R.id.graph);
        resultadoTextView = findViewById(R.id.resultado_text_view);

        // Receber dados da intent
        double irms = getIntent().getDoubleExtra("irms", 0);
        String resultado = "A corrente eficaz é: " + irms + " A";
        resultadoTextView.setText(resultado);

        // Criar e adicionar a série de dados ao gráfico
        DataPoint[] dataPoints = (DataPoint[]) getIntent().getSerializableExtra("dataPoints");
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        graph.addSeries(series);
    }
}
