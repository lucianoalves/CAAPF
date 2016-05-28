/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.meta;

import br.com.puc.alves.utils.Statistics;
import br.com.puc.alves.utils.Util;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import weka.core.Utils;
import static br.com.puc.alves.utils.Util.CSV_SEPARATOR;

/**
 *
 * @author ssad
 */
public class RankingAleatory {

    public static void main(String[] args) {
        RankingAleatory validateSpearman = new RankingAleatory();
        validateSpearman.writeToCSVSpearman(validateSpearman.process());
    }

    public List<String[]> process() {
        List<String[]> results = new ArrayList<>();
        String[] result;
        double[] rankAtual = new double[Util.algorithmAmount];
        List<String> lines = Util.getCsvToList(Util.getFilePath(Util.RANKING_RESULT, "rankingAlgorithmBy", 1));
        List<int[]> rankings;
        for (String l : lines) {
            String[] r = l.split(Util.CSV_SEPARATOR);
            if (r[0].equals("DataSetName") || r[0].trim().equals("")) continue;
            rankings = this.rankings(30);
            result = new String[34];
            result[0] = r[0];
            
            for (int i = 0; i < Util.algorithmAmount; i++) {
                rankAtual[i] = Double.valueOf(r[i+i+1]);
            }
                        
            for (int i = 0; i < 30; i++) {
                int[] rankPredicted = rankings.get(i);
                result[i+1] = Utils.doubleToString(getSpearman(rankAtual, rankPredicted), 3);
            }
            result[31] = Utils.doubleToString(this.getMediaRanking(result, 30), 3);
            result[32] = r[Util.algorithmAmount*3+3];
            if (Double.valueOf(result[32]) > Double.valueOf(result[31])) {
                result[33] = "1";
            } else {
                result[33] = "2";
            }
            
            results.add(result);
        }
        return results;        
    }
    
    public double getMediaRanking(String[] result, int sizeAleatoryRanking) {
        double best = 0D;
        for (int i = 1; i < 31; i++) {
            best += Double.valueOf(result[i]);
        }
        return (best / sizeAleatoryRanking);
    }
    
    private List<int[]> rankings(int qtd) {
        List<int[]> rankings = new ArrayList<>();
        for (int i = 0; i < qtd; i++) {
            rankings.add(i, Util.getRankings());
        }
        return rankings;
    }
    
    public double getSpearman(double[] rankAtual, int[]rankPredicted) {
        double spearman = 0;
        double d;
        for (int i = 0; i < Util.algorithmAmount; i++) {
            d = rankAtual[i] - rankPredicted[i];
            spearman = spearman + d * d;
        }
        spearman = 1 - (6 * spearman / (Util.algorithmAmount * Util.algorithmAmount * Util.algorithmAmount - Util.algorithmAmount));
        return spearman;
    }
    
    private void writeToCSVSpearman(List<String[]> results) {
        try
        {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.getFilePath(Util.RANKING_EXP, "RankingAleatoryBy")), "UTF-8"))) {
                bw.write("dataSetName"+CSV_SEPARATOR+"SPR1"+CSV_SEPARATOR+"SPR2"+CSV_SEPARATOR+"SPR3"+CSV_SEPARATOR+"SPR4"+CSV_SEPARATOR+"SPR5"+CSV_SEPARATOR
                        +"SPR6"+CSV_SEPARATOR+"SPR7"+CSV_SEPARATOR+"SPR8"+CSV_SEPARATOR+"SPR9"+CSV_SEPARATOR+"SPR10"+CSV_SEPARATOR
                        +"SPR11"+CSV_SEPARATOR+"SPR12"+CSV_SEPARATOR+"SPR13"+CSV_SEPARATOR+"SPR14"+CSV_SEPARATOR+"SPR15"+CSV_SEPARATOR
                        +"SPR16"+CSV_SEPARATOR+"SPR17"+CSV_SEPARATOR+"SPR18"+CSV_SEPARATOR+"SPR19"+CSV_SEPARATOR+"SPR20"+CSV_SEPARATOR
                        +"SPR21"+CSV_SEPARATOR+"SPR22"+CSV_SEPARATOR+"SPR23"+CSV_SEPARATOR+"SPR24"+CSV_SEPARATOR+"SPR25"+CSV_SEPARATOR
                        +"SPR26"+CSV_SEPARATOR+"SPR27"+CSV_SEPARATOR+"SPR28"+CSV_SEPARATOR+"SPR29"+CSV_SEPARATOR+"SPR30"+CSV_SEPARATOR
                        +"MEDIA"+CSV_SEPARATOR+"spearman"+CSV_SEPARATOR+"BEST");
                bw.newLine();
                StringBuffer oneLine;
                double[] ma = new double[results.size()];
                double[] ra = new double[results.size()];
                int i = 0;
                for (String[] r : results) {
                    oneLine = new StringBuffer();
                    for (String s : r) {
                        oneLine.append(s);
                        oneLine.append(CSV_SEPARATOR);
                    }
                    ma[i] = Double.valueOf(r[r.length-2]);
                    ra[i] = Double.valueOf(r[r.length-3]);
                    i++;
                    bw.write(oneLine.toString());
                    bw.newLine();
                }
                Statistics statisticsRA = new Statistics(ra);
                Statistics statisticsMA = new Statistics(ma);
                bw.write(""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR
                        +""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR
                        +""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR
                        +""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+"MEDIA"+CSV_SEPARATOR
                        +Utils.doubleToString(statisticsRA.getMean(), 3)+CSV_SEPARATOR+Utils.doubleToString(statisticsMA.getMean(), 3)+CSV_SEPARATOR+"");
                bw.newLine();
                bw.write(""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR
                        +""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR
                        +""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR
                        +""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+"StDev"+CSV_SEPARATOR
                        +Utils.doubleToString(statisticsRA.getStdDev(), 3)+CSV_SEPARATOR+Utils.doubleToString(statisticsMA.getStdDev(), 3)+CSV_SEPARATOR+"");
                bw.flush();
            }
        }
        catch (UnsupportedEncodingException e) {}
        catch (FileNotFoundException e){}
        catch (IOException e){}
    }
}