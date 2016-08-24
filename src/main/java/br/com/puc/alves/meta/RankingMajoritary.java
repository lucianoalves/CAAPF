/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.meta;

import br.com.puc.alves.utils.Statistics;
import br.com.puc.alves.utils.Util;
import static br.com.puc.alves.utils.Util.CSV_SEPARATOR;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import weka.core.Utils;

/**
 *
 * @author ssad
 */
public class RankingMajoritary {
    final static Logger logger = Logger.getLogger(RankingMajoritary.class);
    
    private static String technicalName = "";
    private static int k;
    
    public static void main(String[] args) {
        try {
            RankingMajoritary rankingMajoritary = new RankingMajoritary();
            technicalName = Util.KNN;
            k = 3;  
            rankingMajoritary.writeToCSVSpearman(rankingMajoritary.process());
            technicalName = Util.ART_FOREST;
            k = 0;  
            rankingMajoritary.writeToCSVSpearman(rankingMajoritary.process());
        } catch (Exception e) {
            logger.error("Exception is ", e);
        }
    }
    
    public List<String[]> process() {
        List<String> rankings = Util.getCsvToList(Util.getFilePath(Util.RANKING_RESULT, technicalName, k));
        List<RankingMajoritaryBean> atualRankings = new ArrayList<>();
        RankingMajoritaryBean bean;
        for (String s : rankings) {
            String[] line = s.split(Util.CSV_SEPARATOR);
            if (line[0].equals("DataSetName") || line[0].trim().equals("")) continue;
            String rankAtual = "";
            for (int i = 1; i < Util.algorithmAmount * 2; i+=2) {
                rankAtual += line[i] + ":";
            }
            bean = new RankingMajoritaryBean(rankAtual);
            setRankingMajoritory(atualRankings, bean);
        }
        //System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        Collections.sort(atualRankings);
        String rankMajoritory = atualRankings.get(0).getRanking();
        List<String[]> listResults = new ArrayList<>();
        String[] results;
        for (String s : rankings) {
            String[] line = s.split(Util.CSV_SEPARATOR);
            if (line[0].equals("DataSetName") || line[0].trim().equals("")) continue;
            results = new String[5];
            String rankAtual = "";
            for (int i = 1; i < Util.algorithmAmount * 2; i+=2) {
                rankAtual += line[i] + ":";
            }
            results[0] = line[0];
            results[1] = rankAtual;
            results[2] = rankMajoritory;
            results[3] = Utils.doubleToString(getSpearman(rankAtual, rankMajoritory), 3);
            results[4] = line[Util.algorithmAmount*2+1];
                    
            listResults.add(results);
        }
        return listResults;        
    }
    
    public double getSpearman(String sRankAtual, String sRankPredicted) {
        String[] rankAtual = sRankAtual.split(":");
        String[] rankPredicted = sRankPredicted.split(":");
        
        double spearman = 0;
        double d;
        for (int i = 0; i < Util.algorithmAmount; i++) {
            d = Double.valueOf(rankAtual[i]) - Double.valueOf(rankPredicted[i]);
            spearman = spearman + d * d;
        }
        spearman = 1 - (6 * spearman / (Util.algorithmAmount * Util.algorithmAmount * Util.algorithmAmount - Util.algorithmAmount));
        return spearman;
    }    
        
    private void setRankingMajoritory(List<RankingMajoritaryBean> list, RankingMajoritaryBean bean) {
        if (list.isEmpty()) {
            list.add(bean);
        } else {
            boolean flag = false;
            for (RankingMajoritaryBean ranking : list) {
                if (ranking.getRanking().equals(bean.getRanking())) {
                    ranking.setCount(ranking.getCount()+1);
                    flag = true;
                    break;
                }                
            }
            if (!flag) {
                list.add(bean);
            }
        }
    }
    
    private void writeToCSVSpearman(List<String[]> results) {
        try
        {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.getFilePath(Util.RANKING_EXP, "RankingMajoritory-"+technicalName+"-By")), "UTF-8"))) {
                bw.write("dataSetName"+CSV_SEPARATOR+"RankAtual"+CSV_SEPARATOR+"RankMajoritory"+CSV_SEPARATOR+"SPR-MAJ"+CSV_SEPARATOR+"SPEARMAN");
                bw.newLine();
                StringBuffer oneLine;
                double[] ma = new double[results.size()];
                double[] rm = new double[results.size()];
                int i = 0;
                for (String[] r : results) {
                    oneLine = new StringBuffer();
                    for (String s : r) {
                        oneLine.append(s);
                        oneLine.append(CSV_SEPARATOR);
                    }
                    ma[i] = Double.valueOf(r[r.length-1]);
                    rm[i] = Double.valueOf(r[r.length-2]);
                    i++;
                    bw.write(oneLine.toString());
                    bw.newLine();
                }
                Statistics statisticsRM = new Statistics(rm);
                Statistics statisticsMA = new Statistics(ma);
                bw.write(""+CSV_SEPARATOR+""+CSV_SEPARATOR+"MEDIA"+CSV_SEPARATOR
                        +Utils.doubleToString(statisticsRM.getMean(), 3)+CSV_SEPARATOR+Utils.doubleToString(statisticsMA.getMean(), 3)+CSV_SEPARATOR+"");
                bw.newLine();
                bw.write(""+CSV_SEPARATOR+""+CSV_SEPARATOR+"StDev"+CSV_SEPARATOR
                        +Utils.doubleToString(statisticsRM.getStdDev(), 3)+CSV_SEPARATOR+Utils.doubleToString(statisticsMA.getStdDev(), 3)+CSV_SEPARATOR+"");
                bw.flush();
                bw.flush();
            }
        }
        catch (UnsupportedEncodingException e) {}
        catch (FileNotFoundException e){}
        catch (IOException e){}
    }
    
}
