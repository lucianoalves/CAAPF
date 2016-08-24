/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.latex;

import br.com.puc.alves.base.MetaBase;
import br.com.puc.alves.utils.Util;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 *
 * @author luciano
 */
public class BestWithAllMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BestWithAllMain rankMain = new BestWithAllMain();
        rankMain.writeToCSV(rankMain.getValues());
    }
    
    private Map<String, double[]> getValues() {
        Map<String, double[]> mapRankings = new LinkedHashMap();
        
        List<String> lines = Util.getCsvToList(Util.getFilePath(Util.BEST_ALGORITHM_EXP, "BestAlgorithmVsRankMediaAllAlgorithms"));
        String[] split;
        double[] values;
        String[] s = new String[2];
        s[0] = "${\\mu}$";
        s[1] = "${\\sigma}$";
        int c = 0;
        for (int i = 1; i < lines.size(); i++) {
            values = new double[10];
            split = lines.get(i).split(Util.CSV_SEPARATOR);
            values[0] = Double.valueOf(split[9]);
            values[1] = Double.valueOf(split[1]);
            values[2] = Double.valueOf(split[2]);
            values[3] = Double.valueOf(split[3]);
            values[4] = Double.valueOf(split[4]);
            values[5] = Double.valueOf(split[5]);
            values[6] = Double.valueOf(split[6]);
            values[7] = Double.valueOf(split[7]);
            values[8] = Double.valueOf(split[8]);
            values[9] = getMinor(values);
            
            if (split[0].equals("MEDIA") || split[0].equals("StDev")) {
                mapRankings.put(s[c], values);
                c++;
            } else {
                mapRankings.put(split[0], values);
            }
        }
        
        return mapRankings;
    }
     
    private double getMinor(double[] values) {
        double value = values[0];
        for (int i = 1; i < 9; i++) {
            if (values[i] < value) {
                value = values[i];
            }
        } 
        return value;
    }
    
    private void writeToCSV(Map<String, double[]> mapRankings) {
        BufferedWriter bw;
        
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.LATEX + "bestAll-"+Util.MEASURE_TYPE+".tex"), "UTF-8"));
            
            bw.write("\\centering");
            bw.newLine();
            bw.write("\\label{best}");
            bw.newLine();
            bw.write("\\begin{tabularx}{\\textwidth}{@{\\extracolsep{\\fill}} l c c c c c c c c c}");
            bw.newLine();
            bw.write("\\toprule");
            bw.newLine();
            bw.write("BASE DADOS & PFS-FMA-EN-8 & NB & RF & C4.5 & \\textit{k}NN & SVM & MLP & ADB & XGB \\\\");
            bw.newLine();
            bw.write("\\midrule");  
            bw.newLine();
            String line = "";
            
            DecimalFormat df = new DecimalFormat("0.000");
            for (Map.Entry<String, double[]> entry : mapRankings.entrySet()) {
                line = entry.getKey();
                double[] values = entry.getValue();
                for (int i = 0; i < 9; i++) {
                    String value = df.format(values[i]);
                    if (df.format(values[9]).equals(value)) {
                        line += " & \\bftab "+value;
                    } else {
                        line += " & "+value;
                    }
                }
                bw.write(line + " \\\\");
                bw.newLine();
            }
            bw.write("\\bottomrule");
            bw.newLine();         
            bw.write("\\caption{Recomendar o melhor algoritmo}");
            bw.newLine();
            bw.write("\\end{tabularx}");
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(MetaBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}