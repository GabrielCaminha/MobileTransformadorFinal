package com.example.myapplication;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

public class ResultadoTransformador extends AppCompatActivity {

    private ImageView imagem;

    private TextView zcc;
    private TextView req;
    private TextView xeq;
    private TextView rc;
    private TextView zphi;
    private TextView xm;
    private TextView rcp;
    private TextView xmp;
    private TextView iphip;
    private TextView ic;
    private TextView im;

    private TextView rp;
    private TextView xp;
    private TextView rs;
    private TextView xs;

    private TextView reqtotal;
    private TextView xeqtotal;
    private TextView regulacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resultado_transformador);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imagem = findViewById(R.id.imageView);

        zcc = findViewById(R.id.Np);
        req = findViewById(R.id.Req);
        xeq = findViewById(R.id.Xeq);
        rc = findViewById(R.id.Rc);
        zphi = findViewById(R.id.Zphi);
        xm = findViewById(R.id.Xm);
        rcp = findViewById(R.id.RcP);
        xmp = findViewById(R.id.XmP);
        iphip = findViewById(R.id.IphiP);
        ic = findViewById(R.id.Ic);
        im = findViewById(R.id.Im);

        rp = findViewById(R.id.Rp);
        xp = findViewById(R.id.Xp);
        rs = findViewById(R.id.Rs);
        xs = findViewById(R.id.Xs);

        reqtotal = findViewById(R.id.ReqTotal);
        xeqtotal = findViewById(R.id.XeqTotal);

        Bundle dados = getIntent().getExtras();
        assert dados != null;
        Double espirasPrimario = Double.parseDouble(dados.getString("espirasPrimario"));
        Double espirasSecundario = Double.parseDouble(dados.getString("espirasSecundario"));
        String tipoCircuito = dados.getString("tipoCircuito");
        String referencia = dados.getString("referencia");

        String tipoEnsaioA = dados.getString("tipoEnsaioA");
        String ladoEnsaioA = dados.getString("ladoEnsaioA");
        Double tensaoA = Double.parseDouble(dados.getString("tensaoA"));
        Double correnteA = Double.parseDouble(dados.getString("correnteA"));
        Double potenciaA = Double.parseDouble(dados.getString("potenciaA"));

        String tipoEnsaioB = dados.getString("tipoEnsaioB");
        String ladoEnsaioB = dados.getString("ladoEnsaioB");
        Double tensaoB = Double.parseDouble(dados.getString("tensaoB"));
        Double correnteB = Double.parseDouble(dados.getString("correnteB"));
        Double potenciaB = Double.parseDouble(dados.getString("potenciaB"));

        calcularParametros(espirasPrimario, espirasSecundario, tipoCircuito, referencia, tipoEnsaioA, ladoEnsaioA, tipoEnsaioB, ladoEnsaioB, tensaoA, correnteA, potenciaA, tensaoB, correnteB, potenciaB);
    }

    @SuppressLint("DefaultLocale")
    private void calcularParametros(Double n1, Double n2, String tipoCircuito, String referencia, String tipoA, String ladoA, String tipoB, String ladoB, Double vA, Double iA, Double pA, Double vB, Double iB, Double pB){
        Double a = n1 / n2;
        Double a2 = a * a;
        Double Zcc = 0.0, Req = 0.0, Xeq = 0.0, Rc = 0.0, Zphi = 0.0, Xm = 0.0, RcPrime = 0.0, XmPrime = 0.0, IphiPrime = 0.0,
                Ic = 0.0, Im = 0.0, Rp = 0.0, Xp = 0.0, Rs = 0.0, Xs = 0.0, ReqTotal = 0.0, XeqTotal = 0.0;

        if (tipoA.equals("Curto-Circuito") && ladoA.equals("Baixa Tensão") && tipoB.equals("Circuito Aberto") && ladoB.equals("Alta Tensão")){
            Zcc = vA / iA;
            Req = pA / (iA * iA);
            Xeq = Math.sqrt((Zcc*Zcc)-(Req*Req));
            Rc = (vB*vB)/pB;
            Zphi = vB/iB;
            Xm = 1/(Math.sqrt(1/(Zphi * Zphi) -1/(Rc * Rc)));

            RcPrime = Rc * a2;
            XmPrime = Xm * a2;

            IphiPrime = iB / a;
            Ic = n1 / RcPrime;
            Im = n1 / XmPrime;

        } else if (tipoA.equals("Circuito Aberto") && ladoA.equals("Baixa Tensão") && tipoB.equals("Curto-Circuito") && ladoB.equals("Alta Tensão")) {
            Rc = vA * vA / pA;
            Zphi = vA / iA;
            Xm = 1/(Math.sqrt(1/(Zphi * Zphi) -1/(Rc * Rc)));

            RcPrime = Rc * a2;
            XmPrime = Xm * a2;

            IphiPrime = iA / a;
            Ic = vA / RcPrime;
            Im = vA / XmPrime;

            Zcc = vB / iB;
            Req = pB / (iB * iB);
            Xeq = Math.sqrt(Zcc * Zcc - Req * Req);
        } else if(tipoA.equals("Curto-Circuito") && ladoA.equals("Alta Tensão") && tipoB.equals("Circuito Aberto") && ladoB.equals("Baixa Tensão")){
            Zcc = vA / iA;
            Req = pA / (iA * iA);
            Xeq = Math.sqrt(Zcc * Zcc - Req * Req);

            Rc = vB * vB / pB;
            Zphi = vB / iB;
            Xm = Math.sqrt(Zphi * Zphi - Rc * Rc);
        } else if(tipoA.equals("Circuito Aberto") && ladoA.equals("Alta Tensão") && tipoB.equals("Curto-Circuito") && ladoB.equals("Baixa Tensão")){
            Rc = vA * vA / pA;
            Zphi = vA / iA;
            Xm = 1/(Math.sqrt(1/(Zphi * Zphi) -1/(Rc * Rc)));

            RcPrime = Rc * Math.pow(n1 / n2, 2);
            XmPrime = Xm * Math.pow(n1 / n2, 2);

            IphiPrime = iA * (n2 / n1);
            Ic = vA / RcPrime;
            Im = vA / XmPrime;

            Zcc = vB / iB;
            Req = pB / (iB * iB);
            Xeq = Math.sqrt(Zcc * Zcc - Req * Req);
        }
        zcc.setText(String.format("Zcc: %.2f Ω", Zcc));
        req.setText(String.format("Req: %.2f Ω", Req));
        xeq.setText(String.format("Xeq: %.2f Ω", Xeq));
        rc.setText(String.format("Rc: %.2f Ω", Rc));
        zphi.setText(String.format("Zφ: %.2f Ω", Zphi));
        xm.setText(String.format("Xm: %.2f Ω", Xm));
        rcp.setText(String.format("Rc' (Referido ao Primário): %.2f kΩ", RcPrime/1000));
        xmp.setText(String.format("Xm' (Referido ao Primário): %.2f kΩ", XmPrime/1000));
        iphip.setText(String.format("Iφ' (Referido ao Primário): %.2f A", IphiPrime));
        ic.setText(String.format("Ic: %.2f mA", Ic*1000));
        im.setText(String.format("Im: %.2f mA", Im*1000));

        switch (tipoCircuito) {
            case "T - Circuito em T":
                if (referencia.equals("Primário")) {
                    Rp = Req / 2;
                    Xp = Xeq / 2;
                    Rs = Rp;
                    Xs = Xp;
                } else if (referencia.equals("Secundário")) {
                    Rp = Req / a2;
                    Xp = Xeq / a2;
                    Rs =  Rp;
                    Xs =  Xp;
                }
                imagem.setImageResource(R.drawable.tipot);
                rp.setText(String.format("Rp: %.2f Ω", Rp));
                xp.setText(String.format("Xp: %.2f Ω", Xp));
                rs.setText(String.format("Rs: %.2f Ω", Rs));
                xs.setText(String.format("Xs: %.2f Ω", Xs));
                ((ViewGroup) reqtotal.getParent()).removeView(reqtotal);
                ((ViewGroup) xeqtotal.getParent()).removeView(xeqtotal);
                break;
            case "L - Circuito em L":
                if (referencia.equals("Primário")) {
                    Rp = Req / 2;
                    Xp = Xeq / 2;
                    ReqTotal = Rp * 2;
                    XeqTotal = Xp + Xs;
                } else if (referencia.equals("Secundário")) {
                    Rp = Req * a2;
                    Xp = Xeq * a2;
                    ReqTotal = Rp + (Req - Rp);
                    XeqTotal = Xp + (Xeq - Xp);
                }
                imagem.setImageResource(R.drawable.tipol);
                reqtotal.setText(String.format("Req Total: %.2f Ω", ReqTotal));
                xeqtotal.setText(String.format("Xeq Total: %.2f Ω", XeqTotal));
                ((ViewGroup) rp.getParent()).removeView(rp);
                ((ViewGroup) xp.getParent()).removeView(xp);
                ((ViewGroup) rs.getParent()).removeView(rs);
                ((ViewGroup) xs.getParent()).removeView(xs);
                break;
            case "Série - Circuito em Série":
                imagem.setImageResource(R.drawable.serie);
                ReqTotal = Req + RcPrime / Math.pow(n1 / n2, 2);
                XeqTotal = Xeq + XmPrime / Math.pow(n1 / n2, 2);
                reqtotal.setText(String.format("Req Total: %.2f Ω", ReqTotal));
                xeqtotal.setText(String.format("Xeq Total: %.2f Ω", XeqTotal));
                ((ViewGroup) rp.getParent()).removeView(rp);
                ((ViewGroup) xp.getParent()).removeView(xp);
                ((ViewGroup) rs.getParent()).removeView(rs);
                ((ViewGroup) xs.getParent()).removeView(xs);
                break;
        }

        calcularRegulacao(Req, Xeq, vA, iA);

        calcularFasorial(a, vB, Req, iB, Xeq);
    }

    private void calcularRegulacao(Double Req, Double Xeq, Double Vfl, Double If) {
        Double Vnl = Vfl + (Req * If + Xeq * If);
        Double regulacao = ((Vnl - Vfl) / Vfl) * 100;

        TextView regulacaoTextView = findViewById(R.id.regulacao);

        regulacaoTextView.setText(String.format("Regulação: %.2f%%", regulacao));
    }

    private void calcularFasorial(Double a, Double vB, Double Req, Double iB, Double Xeq) {
        // Parte real: vB + Req * iB
        Double realPart = vB + (Req * iB);

        // Parte imaginária: jXeq * iB
        Double imaginaryPart = Xeq * iB;

        // Calculando Vp/a
        Double Vp_over_a = realPart / a;

        // Calculando o ângulo em radianos e convertendo para graus
        Double angleRadians = Math.atan2(imaginaryPart, realPart);
        Double angleDegrees = Math.toDegrees(angleRadians);

        // Exibindo o resultado e o ângulo
        String resultado = String.format(
                "Tensão no primário dividida por a (Vp/a): %.2f V\n" +
                        "Componente imaginário (jXeq * Is): %.2f V\n" +
                        "Ângulo de fase entre Vp/a e jXeq * Is: %.2f°",
                Vp_over_a, imaginaryPart, angleDegrees
        );
        TextView fasorialTextView = findViewById(R.id.fasorial_result);
        fasorialTextView.setText(resultado);

        // Plotando o gráfico (diagrama fasorial)
        GraphView graph = findViewById(R.id.fasorial_graph);

        // Limpando séries anteriores
        graph.removeAllSeries();

        // Plotar o fasor como um vetor partindo da origem (0,0) para (realPart, imaginaryPart)
        LineGraphSeries<DataPoint> fasorSeries = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 0),  // Origem
                new DataPoint(realPart, imaginaryPart)  // Vetor resultante (real, imaginário)
        });
        fasorSeries.setColor(Color.BLUE);
        fasorSeries.setThickness(8);

        // Adicionar série ao gráfico
        graph.addSeries(fasorSeries);

        // Adicionar um ponto no final do vetor para marcar o fim do fasor
        PointsGraphSeries<DataPoint> endPointSeries = new PointsGraphSeries<>(new DataPoint[] {
                new DataPoint(realPart, imaginaryPart)
        });
        endPointSeries.setShape(PointsGraphSeries.Shape.POINT);
        endPointSeries.setColor(Color.RED);
        endPointSeries.setSize(10);
        graph.addSeries(endPointSeries);

        // Configurações do gráfico
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(-Math.max(Math.abs(realPart), Math.abs(imaginaryPart)) - 50);
        graph.getViewport().setMaxX(Math.max(realPart, imaginaryPart) + 50);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-Math.max(Math.abs(realPart), Math.abs(imaginaryPart)) - 50);
        graph.getViewport().setMaxY(Math.max(realPart, imaginaryPart) + 50);

        // Habilitando escala
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);

        // Desenhando o ângulo (linhas pontilhadas)
        LineGraphSeries<DataPoint> angleSeries = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 0), // Origem
                new DataPoint(realPart, 0), // Linha no eixo real
                new DataPoint(realPart, imaginaryPart)  // Linha do fasor até a parte imaginária
        });

        // Tornando a linha pontilhada para indicar o ângulo
        angleSeries.setColor(Color.GRAY);
        angleSeries.setDrawDataPoints(true);
        angleSeries.setDataPointsRadius(5);
        angleSeries.setThickness(2);
        graph.addSeries(angleSeries);

        // Adicionar texto com o valor do ângulo no gráfico
        graph.addSeries(new LineGraphSeries<>(new DataPoint[] {new DataPoint(0, 0)}));
        graph.getSecondScale().addSeries(new LineGraphSeries<>(new DataPoint[] {new DataPoint(0, imaginaryPart)}));
        graph.addSeries(fasorSeries);
    }

}