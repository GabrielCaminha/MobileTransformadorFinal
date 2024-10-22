package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResultadoDimensionamento extends AppCompatActivity {

    private TextView textNp;
    private TextView textNs;
    private TextView textBitola1;
    private TextView textBitola2;
    private TextView textTipoLamina;
    private TextView textQntDeLamina;

    private TextView textAltura;
    private TextView textLargura;
    private TextView textComprimento;
    private TextView textAreaFrontal;
    private TextView textVolume;

    private TextView textPesoNucleoFerro;
    private TextView textPesoNucleoCobre;
    private TextView textPesoTotal;

    private TextView textFrequencia;
    private TextView textTensaoPrimaria;
    private TextView textTensaoSecundaria;
    private TextView textCorrentePrimaria;
    private TextView textCorrenteSecundaria;
    private TextView textPotenciaPrimaria;
    private TextView textPotenciaSecundaria;
    private TextView textSecaoFio1;
    private TextView textSecaoFio2;
    private TextView textSecaoMagnetica;
    private TextView textSecaoGeometrica;
    private TextView textRelacaoEspiras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resultado_dimensionamento);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textNp = findViewById(R.id.Np);
        textNs = findViewById(R.id.Ns);
        textBitola1 = findViewById(R.id.bitola1);
        textBitola2 = findViewById(R.id.bitola2);
        textTipoLamina = findViewById(R.id.tipoLamina);
        textQntDeLamina = findViewById(R.id.qntLamina);

        textAltura = findViewById(R.id.altura);
        textLargura = findViewById(R.id.largura);
        textComprimento = findViewById(R.id.comprimento);
        textAreaFrontal = findViewById(R.id.areaFrontal);
        textVolume = findViewById(R.id.volume);

        textPesoNucleoFerro = findViewById(R.id.pesoNucleoFerro);
        textPesoNucleoCobre = findViewById(R.id.pesoNucleoCobre);
        textPesoTotal = findViewById(R.id.pesoTotal);

        textFrequencia = findViewById(R.id.frequencia);
        textTensaoPrimaria = findViewById(R.id.tensaoPrimaria);
        textTensaoSecundaria = findViewById(R.id.tensaoSecundaria);
        textCorrentePrimaria = findViewById(R.id.correntePrimaria);
        textCorrenteSecundaria = findViewById(R.id.correnteSecundaria);
        textPotenciaPrimaria = findViewById(R.id.potenciaPrimaria);
        textPotenciaSecundaria = findViewById(R.id.potenciaSecundaria);
        textSecaoFio1 = findViewById(R.id.secaoFio1);
        textSecaoFio2 = findViewById(R.id.secaoFio2);
        textSecaoMagnetica = findViewById(R.id.secaoMagnetica);
        textSecaoGeometrica = findViewById(R.id.secaoGeometrica);
        textRelacaoEspiras = findViewById(R.id.relacaoEspiras);

        Bundle dados = getIntent().getExtras();
        assert dados != null;
        String frequencia = dados.getString("frequencia");
        Double tensaoPrimaria = Double.parseDouble(dados.getString("tensaoPrimaria"));
        Double tensaoSecundaria = Double.parseDouble(dados.getString("tensaoSecundaria"));
        Double potenciaCarga = Double.parseDouble(dados.getString("potenciaCarga"));

        calcularParametros(frequencia, tensaoPrimaria, tensaoSecundaria, potenciaCarga);
    }

    @SuppressLint("DefaultLocale")
    public void calcularParametros(String frequenciaString, double tensaoPrimaria, double tensaoSecundaria, double potenciaSecundaria){

        String tipoLamina;
        double frequencia;
        if (frequenciaString.equals("50 Hz")){
            frequencia = 50.0;
        } else {
            frequencia = 60.0;
        }

        double potenciaPrimaria = potenciaSecundaria * 1.1;
        double correntePrimaria = potenciaPrimaria / tensaoPrimaria ;
        double correnteSecundaria = potenciaSecundaria / tensaoSecundaria;

        double secaoFio1 = secaoCondutor(correntePrimaria, potenciaSecundaria);
        double secaoFio2 = secaoCondutor(correnteSecundaria, potenciaSecundaria);
        String secaoFio1Bitola = tipoBitola(secaoFio1);
        String secaoFio2Bitola = tipoBitola(secaoFio2);

        if(potenciaPrimaria <= 800){
            tipoLamina = "Lâminas Padronizadas";
        } else {
            tipoLamina = "Lâminas Compridas";
        }

        double secaoMagnetica = calcularSecaoMagnetica(tipoLamina, potenciaSecundaria, frequencia);
        double secaoGeometrica = secaoMagnetica * 1.1;

        double a;
        if (secaoGeometrica > 20) {
            a = 5.0;
        } else {
            a = (double) Math.round(Math.sqrt(secaoGeometrica));
        }

        double b = secaoGeometrica/a;

        double secaoMagneticaNucleo = a*b;
        double secaoGeometricaNucleo = secaoMagneticaNucleo/1.1;

        double relacaoEspiras;
        if (frequencia == 50){
            relacaoEspiras = 40/secaoGeometricaNucleo;
        } else {
            relacaoEspiras = 33.5/secaoGeometricaNucleo;
        }

        double n1 = tensaoPrimaria * relacaoEspiras;
        double n2 = tensaoSecundaria * relacaoEspiras * 1.1;

        // execucaoViavel(tipoLamina, n1, secaoFio1, n2, secaoFio2, a);

        double pesoNucleoFerro = calcularPesoNucleoFerro(tipoLamina, a, b);
        double pesoNucleoCobre = calcularPesoNucleoCobre(a, b, n1, secaoFio1, n2, secaoFio2);
        double pesoTotal = pesoNucleoFerro+pesoNucleoCobre;


        double altura = 0.0;
        double comprimento = 0.0;
        double largura = 0.0;
        if(tipoLamina.equals("Lâminas Padronizadas")){
            altura = 2.5 * a;
            largura = 3 * a;
            comprimento = b;
        }
        if(tipoLamina.equals("Lâminas Compridas")){
            altura = 4 * a;
            largura = 3 * a;
            comprimento = b;
        }
        double areaFrontal = altura * largura;
        double volume = areaFrontal * comprimento * 0.9;

        double acesita = 0.035;
        double qntLaminas = Math.round((b*0.9)/acesita);

        textNp.setText(String.format("Np: %s espiras", Math.round(n1)));
        textNs.setText(String.format("Ns: %s espiras", Math.round(n2)));
        textBitola1.setText(String.format("Bitola Primário: %s", secaoFio1Bitola));
        textBitola2.setText(String.format("Bitola Secundário: %s", secaoFio2Bitola));
        textTipoLamina.setText(String.format("Tipo de Lâmina: %s", tipoLamina));
        textQntDeLamina.setText(String.format("Quantidade de Lâminas: %s", qntLaminas));

        textAltura.setText(String.format("Altura: %.2f cm", altura));
        textLargura.setText(String.format("Largura: %.2f cm", largura));
        textComprimento.setText(String.format("Comprimento: %.2f cm", comprimento));
        textAreaFrontal.setText(String.format("Área Frontal: %.2f cm²", areaFrontal));
        textVolume.setText(String.format("Volume: %.2f cm³", volume));

        textPesoNucleoFerro.setText(String.format("Peso do Núcleo (Ferro): %.2f kg", pesoNucleoFerro));
        textPesoNucleoCobre.setText(String.format("Peso do Núcleo (Cobre): %.2f kg", pesoNucleoCobre));
        textPesoTotal.setText(String.format("Peso Total: %.2f kg", pesoTotal));

        textFrequencia.setText(String.format("Frequência: %.2f Hz", frequencia));
        textTensaoPrimaria.setText(String.format("Tensão Primária: %.2f V", tensaoPrimaria));
        textTensaoSecundaria.setText(String.format("Tensão Secundária: %.2f V", tensaoSecundaria));
        textCorrentePrimaria.setText(String.format("Corrente Primária: %.2f A", correntePrimaria));
        textCorrenteSecundaria.setText(String.format("Corrente Secundária: %.2f A", correnteSecundaria));
        textPotenciaPrimaria.setText(String.format("Potência Primária: %.2f W", potenciaPrimaria));
        textPotenciaSecundaria.setText(String.format("Potência Secundária: %.2f W", potenciaSecundaria));
        textSecaoFio1.setText(String.format("Seção Fio 1: %.2f ", secaoFio1));
        textSecaoFio2.setText(String.format("Seção Fio 2: %.2f ", secaoFio2));
        textSecaoMagnetica.setText(String.format("Seção Magnética: %.2f ", secaoMagnetica));
        textSecaoGeometrica.setText(String.format("Seção Geométrica: %.2f ", secaoGeometrica));
        textRelacaoEspiras.setText(String.format("Relação de Espiras: %.2f ", relacaoEspiras));

    }

    private double calcularPesoNucleoCobre(double a, double b, double n1, double s1, double n2, double s2){
        double lm = 2*a + 2*b + 0.5*a*3.14;
        return ((calcularSecaoCobre(n1, s1, n2, s2) / 100)*lm*9)/1000;
    }

    private double calcularSecaoCobre(double n1, double s1, double n2, double s2){
        return n1*s1 + n2*s2;
    }

    private double calcularPesoNucleoFerro(String tipoLamina, double a, double b){
        if(tipoLamina.equals("Lâminas Padronizadas")) {
            double[] tamanhoAPadronizada = {1.5, 2, 2.5, 3, 3.5, 4, 5};
            double[] pesoLaminaPadronizada = {0.095, 0.170, 0.273, 0.380, 0.516, 0.674, 1.053};
            for (int i = 0; i < tamanhoAPadronizada.length; i++){
                if (tamanhoAPadronizada[i] == a){
                    return pesoLaminaPadronizada[i]*b;
                }
            }
        }
        if(tipoLamina.equals("Lâminas Compridas")){
            double[] tamanhoAComprida = {4, 5};
            double[] pesoLaminaComprida = {1.000, 1.580};
            for (int i = 0; i < tamanhoAComprida.length; i++){
                if (tamanhoAComprida[i] == a){
                    return pesoLaminaComprida[i]*b;
                }
            }
        }
        return 0.0;
    }

//    private boolean execucaoViavel(String tipoLamina, double n1, double s1, double n2, double s2, double a){
//        Double secaoJanela = 0.0;
//        if(tipoLamina.equals("Lâminas Padronizadas")){
//            double[] tamanhoAPadronizada = {1.5, 2, 2.5, 3, 3.5, 4, 5};
//            double[] secaoJanelaPadronizada = {168, 300, 468, 675, 900 ,1200 ,1880};
//            for (int i = 0; i < tamanhoAPadronizada.length; i++){
//                if (tamanhoAPadronizada[i] == a){
//                    secaoJanela = secaoJanelaPadronizada[i];
//                    break;
//                }
//            }
//        }
//        if(tipoLamina.equals("Lâminas Compridas")){
//            double[] tamanhoAComprida = {4, 5};
//            double[] secaoJanelaComprida = {2400, 3750};
//            for (int i = 0; i < tamanhoAComprida.length; i++){
//                if (tamanhoAComprida[i] == a){
//                    secaoJanela = secaoJanelaComprida[i];
//                    break;
//                }
//            }
//        }
//        if (secaoJanela/calcularSecaoCobre(n1, s1, n2, s2) > 3){
//            return true;
//        }
//
//        return false;
//    }

    private double calcularSecaoMagnetica(String tipoLamina, double potenciaSecundaria, double frequencia) {
        if(tipoLamina.equals("Lâminas Padronizadas")){
            return 7.5 * Math.sqrt(potenciaSecundaria/frequencia);
        }
        if(tipoLamina.equals("Lâminas Compridas")){
            return 6 * Math.sqrt(potenciaSecundaria/frequencia);
        }

        return 0.0;
    }

    private double secaoCondutor(double corrente, double potencia){
        if (potencia <= 500){
            return corrente/3;
        }
        if (potencia >= 500 && potencia < 1000){
            return corrente/2.5;
        }
        if (potencia >= 1000 && potencia <= 3000){
            return corrente/2;
        }
        return 0.0;
    }

    private String tipoBitola(double secaoCondutor){

        double[] limiteSuperior = {53.476, 42.409, 33.362, 26.271, 21.152, 16.774, 13.303, 10.549, 8.366, 6.635, 5.262, 4.173, 3.309, 2.624, 2.081, 1.650, 1.309, 1.038, 0.823, 0.653, 0.518};
        double[] limiteInferior = {42.409, 33.362, 26.271, 21.152, 16.774, 13.303, 10.549, 8.366, 6.635, 5.262, 4.173, 3.309, 2.624, 2.081, 1.650, 1.309, 1.038, 0.823, 0.653, 0.518, 0.411};
        String[] descricao = {"fio 0", "fio 1", "fio 2", "fio 3", "fio 4", "fio 5", "fio 6", "fio 7", "fio 8", "fio 9", "fio 10", "fio 11", "fio 12", "fio 13", "fio 14", "fio 15", "fio 16", "fio 17", "fio 18", "fio 19", "fio 20"};

        for (int i = 0; i < limiteSuperior.length; i++){
            if (secaoCondutor > limiteInferior[i] && secaoCondutor <= limiteSuperior[i]){
                return descricao[i];
            }
        }

        return "";
    }
}