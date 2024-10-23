package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.jjoe64.graphview.series.DataPoint;

public class CurvaDeMagnetizacaoActivity extends AppCompatActivity {

    private Button uploadButton, calculateButton;
    private EditText inputVM, inputNumeroDeEspiras;  // Campos para VM e número de espiras
    private Spinner frequencySpinner;  // Spinner para selecionar a frequência (50 ou 60 Hz)
    private Uri excelFileUri;  // Para armazenar o URI do arquivo Excel

    private ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        excelFileUri = data.getData();
                        Toast.makeText(this, "Arquivo carregado com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curva_de_magnetizacao);

        inputVM = findViewById(R.id.input_vm);  // Campo para tensão primária
        inputNumeroDeEspiras = findViewById(R.id.input_numero_espiras);  // Campo para número de espiras
        frequencySpinner = findViewById(R.id.frequency_spinner);  // Spinner para selecionar a frequência

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.frequency_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(adapter);

        uploadButton = findViewById(R.id.upload_button);
        calculateButton = findViewById(R.id.calculate_button);

        uploadButton.setOnClickListener(view -> openFilePicker());

        calculateButton.setOnClickListener(view -> {
            if (excelFileUri != null) {
                readExcelFile(excelFileUri);
            } else {
                Toast.makeText(this, "Por favor, carregue um arquivo antes de calcular.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(intent);
    }

    private void readExcelFile(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {

            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0); // Obtém a primeira aba do arquivo

            List<Double> mmfData = new ArrayList<>();
            List<Double> fluxData = new ArrayList<>();

            // Lê os dados das colunas MMF e Fluxo
            for (Row row : sheet) {
                if (row.getPhysicalNumberOfCells() < 2) continue; // Ignora linhas com menos de 2 células

                // Lê MMF
                double mmfValue = row.getCell(0) != null && row.getCell(0).getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC
                        ? row.getCell(0).getNumericCellValue() : 0;
                mmfData.add(mmfValue);

                // Lê Fluxo
                double fluxValue = row.getCell(1) != null && row.getCell(1).getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC
                        ? row.getCell(1).getNumericCellValue() : 0;
                fluxData.add(fluxValue);
            }

            // Inicializar valores de entrada do usuário
            double Vp = Double.parseDouble(inputVM.getText().toString());  // Tensão primária do usuário
            double VM = Vp * Math.sqrt(2); // Calcula a tensão máxima (Vm)
            double numeroDeEspiras = Double.parseDouble(inputNumeroDeEspiras.getText().toString());  // Número de espiras
            int freq = Integer.parseInt(frequencySpinner.getSelectedItem().toString()); // Frequência selecionada
            double omega = 2 * Math.PI * freq; // Calcular velocidade angular

            // Calcular fluxo versus tempo
            List<Double> time = new ArrayList<>();
            List<Double> flux = new ArrayList<>();

            double step = (freq == 50) ? 1.0 / 2500 : 1.0 / 3000; // Resolução do step
            double endTime = 0.34;  // Tempo final para um ciclo completo para ambos os casos

            for (double t = 0; t <= endTime; t += step) {
                time.add(t);
                flux.add(-VM / (omega * numeroDeEspiras) * Math.cos(omega * t)); // Calculo do fluxo magnético
            }

            // Interpolar a MMF correspondente ao fluxo calculado
            List<Double> mmfInterpolated = new ArrayList<>();
            for (double f : flux) {
                double interpolatedMMF = interpolateFluxToMMF(fluxData, mmfData, f);
                mmfInterpolated.add(interpolatedMMF);
            }

            // Calcular corrente de magnetização (im)
            List<Double> correnteMagnetizacao = new ArrayList<>();
            for (double mmf : mmfInterpolated) {
                correnteMagnetizacao.add(mmf / numeroDeEspiras); // Cálculo de Im
            }

            // Calcular corrente eficaz (irms)
            double irms = calculateIrms(correnteMagnetizacao);

            // Passar dados para a nova Activity
            Intent intent = new Intent(this, ResultadosCurvaMagnetizacao.class);
            intent.putExtra("irms", irms);

            // Criar série de dados de corrente de magnetização
            DataPoint[] dataPoints = new DataPoint[time.size()];
            for (int i = 0; i < time.size(); i++) {
                dataPoints[i] = new DataPoint(time.get(i), correnteMagnetizacao.get(i));
            }
            intent.putExtra("dataPoints", dataPoints);

            startActivity(intent);  // Inicia a nova Activity para mostrar os resultados

        } catch (Exception e) {
            Log.e("ExcelReader", "Erro ao ler o arquivo: " + e.getMessage(), e);
            Toast.makeText(this, "Erro ao ler o arquivo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Função para interpolar o valor de MMF dado um fluxo
    private double interpolateFluxToMMF(List<Double> fluxData, List<Double> mmfData, double flux) {
        for (int i = 0; i < fluxData.size() - 1; i++) {
            if (flux >= fluxData.get(i) && flux <= fluxData.get(i + 1)) {
                // Interpolação linear
                double t = (flux - fluxData.get(i)) / (fluxData.get(i + 1) - fluxData.get(i));
                return mmfData.get(i) + t * (mmfData.get(i + 1) - mmfData.get(i));
            }
        }
        return 0; // Caso não encontre um intervalo válido, retorna 0
    }

    // Função para calcular o valor eficaz da corrente
    private double calculateIrms(List<Double> correnteMagnetizacao) {
        double sumSquares = 0;
        for (double i : correnteMagnetizacao) {
            sumSquares += i * i;
        }
        return Math.sqrt(sumSquares / correnteMagnetizacao.size());
    }
}
