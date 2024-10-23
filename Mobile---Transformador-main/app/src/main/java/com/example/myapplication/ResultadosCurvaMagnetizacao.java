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
        setContentView(R.layout.activity_resultados_curva_magnetizacao);

        graph = findViewById(R.id.graph);
        resultadoTextView = findViewById(R.id.resultado_text_view);

        // Receber dados da intent
        double irms = getIntent().getDoubleExtra("irms", 0);
        String resultado = "A corrente eficaz é: " + irms + " A";
        resultadoTextView.setText(resultado);

        // Criar e adicionar a série de dados ao gráfico
        DataPoint[] dataPoints = (DataPoint[]) getIntent().getSerializableExtra("dataPoints");

        if (dataPoints != null && dataPoints.length > 0) {
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
            graph.addSeries(series);

            // Configurações do gráfico para melhorar a exibição
            configureGraph(series);
        }
    }

    // Método para configurar o gráfico
    private void configureGraph(LineGraphSeries<DataPoint> series) {
        // Ativar a capacidade de zoom no gráfico
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        // Definir limites do eixo Y manualmente
        graph.getViewport().setYAxisBoundsManual(true);
        double minY = series.getLowestValueY(); // Encontrar o menor valor Y
        double maxY = series.getHighestValueY(); // Encontrar o maior valor Y

        // Ajustar os limites do eixo Y para múltiplos de 0.5
        minY = Math.floor(minY * 2) / 2.0; // Arredonda para baixo para o múltiplo de 0.5 mais próximo
        maxY = Math.ceil(maxY * 2) / 2.0;  // Arredonda para cima para o múltiplo de 0.5 mais próximo

        graph.getViewport().setMinY(minY);
        graph.getViewport().setMaxY(maxY);

        // Configurar o espaçamento do eixo Y para 0.5
        graph.getGridLabelRenderer().setNumVerticalLabels((int) ((maxY - minY) / 0.5) + 1);

        // Adicionar títulos aos eixos
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Tempo (s)");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Corrente de Magnetização (A)");

        // Ajustar o aspecto do gráfico
        adjustGraphAspectRatio();
    }

    // Método para ajustar a proporção do gráfico
    private void adjustGraphAspectRatio() {
        // Ajusta a altura do gráfico
        graph.post(() -> {
            int width = graph.getWidth();
            int newHeight = (int) (width * 1.5); // Altura 1.5 vezes a largura (proporção 2:3)
            graph.getLayoutParams().height = newHeight;
            graph.requestLayout();
        });
    }
}
