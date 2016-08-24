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
public class RankMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RankMain rankMain = new RankMain();
        rankMain.writeToCSV(rankMain.getValues());
    }
    
    private Map<String, double[]> getValues() {
        Map<String, double[]> mapRankings = new LinkedHashMap();
        
        List<String> linesMajoritoryArtForest = Util.getCsvToList(Util.getFilePath(Util.RANKING_EXP, "RankingMajoritory-"+Util.ART_FOREST+"-By"));
        List<String> linesMajoritoryKNN = Util.getCsvToList(Util.getFilePath(Util.RANKING_EXP, "RankingMajoritory-"+Util.KNN+"-By"));
        //List<String> linesAleatoryArtForest = Util.getCsvToList(Util.getFilePath(Util.RANKING_EXP, "RankingAleatory-"+Util.ART_FOREST+"-By"));
        List<String> linesAleatoryKNN = Util.getCsvToList(Util.getFilePath(Util.RANKING_EXP, "RankingAleatory-"+Util.KNN+"-By"));
        String[] split;
        double[] values;
        String[] s = new String[2];
        s[0] = "${\\mu}$";
        s[1] = "$\\sigma$";
        int c = 0;
        for (int i = 1; i < linesMajoritoryArtForest.size(); i++) {
            values = new double[5];
            split = linesMajoritoryKNN.get(i).split(Util.CSV_SEPARATOR);
            values[0] = Double.valueOf(split[4]);
            values[2] = Double.valueOf(split[3]);
            split = linesMajoritoryArtForest.get(i).split(Util.CSV_SEPARATOR);
            values[1] = Double.valueOf(split[4]);
            split = linesAleatoryKNN.get(i).split(Util.CSV_SEPARATOR);
            values[3] = Double.valueOf(split[31]);
            values[4] = getMajor(values);
            if (split[0].equals("")) {
                mapRankings.put(s[c], values);
                c++;
            } else {
                mapRankings.put(split[0], values);
            }
        }
        
        return mapRankings;
    }
 
    private double getMajor(double[] values) {
        double value = values[0];
        for (int i = 1; i < 3; i++) {
            if (values[i] > value) {
                value = values[i];
            }
        } 
        return value;
    }
    
    private void writeToCSV(Map<String, double[]> mapRankings) {
        BufferedWriter bw;
        
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.LATEX + "rank-"+Util.MEASURE_TYPE+".tex"), "UTF-8"));
            //bw.write("\\begin{table*}[!htbp]");
            //bw.newLine();
            //bw.write("\\footnotesize");
            //bw.newLine();
            bw.write("\\centering");
            bw.newLine();
            bw.write("\\caption{Ranking}");
            bw.newLine();
            bw.write("\\label{rank}");
            bw.newLine();
            bw.write("\\begin{tabularx}{\\textwidth}{@{\\extracolsep{\\fill}}  l r r r r }");
            bw.newLine();
            bw.write("\\toprule");
            bw.newLine();
            bw.write("\\cmidrule{2-5}");
            bw.newLine();
            bw.write("& PFS-FMA-Rk & PFS-FMA-RA & MAJ-R & RA  \\\\");
            bw.newLine();
            bw.write("\\midrule");  
            bw.newLine();
            bw.write("\\endhead");
            bw.newLine();
            String line = "";
            
            DecimalFormat df = new DecimalFormat("0.000");
            for (Map.Entry<String, double[]> entry : mapRankings.entrySet()) {
                line = entry.getKey();
                double[] values = entry.getValue();
                for (int i = 0; i < 4; i++) {
                    if (values[i] == values[4]) {
                        line += " & \\bftab "+df.format(values[i]);
                    } else {
                        line += " & "+df.format(values[i]);
                    }
                }
                bw.write(line + " \\\\");
                bw.newLine();
            }
            bw.write("\\bottomrule");
            bw.newLine();         
            bw.write("\\end{tabularx}");
            //bw.newLine();
            //bw.write("\\end{table}");
            bw.flush();
            bw.close();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(MetaBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}