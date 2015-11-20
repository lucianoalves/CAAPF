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
import java.util.List;
import org.apache.log4j.Logger;
import weka.core.Utils;

/**
 *
 * @author ssad
 */
public class RankingKNNvsRankMediaAllAlgorithm {
    final static Logger logger = Logger.getLogger(RankingKNNvsRankMediaAllAlgorithm.class);
    
    public static void main(String[] args) {
        RankingKNNvsRankMediaAllAlgorithm averageRanking = new RankingKNNvsRankMediaAllAlgorithm();
        //String measure = "AUC";
        averageRanking.process();
    }
    
    public void process() {
        try {
            double[] rankMedio;
            double[] rankTotal = new double[Util.algorithmAmount*2+1];

            List<String> dataSets = new ArrayList<>();
            List<double[]> rankMedios = new ArrayList<>();

            List<String> lines = Util.getCsvToList(Util.getFilePath(Util.RANKING_RESULT, "rankingAlgorithmBy", 1));


            for (String l : lines) {
                String[] r = l.split(Util.CSV_SEPARATOR);
                if (r[0].equals("DataSetName") || r[0].trim().equals("")) continue;
                rankMedio = new double[Util.algorithmAmount*2+1];
                setAUC(r, rankMedio, rankTotal);
                setRank(r, rankMedio, rankTotal);
                dataSets.add(r[0]);
                rankMedios.add(rankMedio);
            }

            writeToCSV(dataSets, rankMedios, rankTotal);
        } catch (Exception e) {
            logger.error("Exception is", e);
        }
    }
    
    public void setAUC(String[] csvLineResult, double[] rankMedio, double[] rankTotal) {
        for (int i = 0; i < 7; i++) {
            rankMedio[i] = Double.valueOf(csvLineResult[i+(Util.algorithmAmount*2+4)]);
            rankTotal[i] += rankMedio[i];
        }
    }
    
    public void setRank(String[] csvLineResult, double[] rankMedio, double[] rankTotal) {
        int rankAtual;
        int rankPredicted;
        
        for (int i = 0; i < 7; i++) {
            rankAtual = Integer.parseInt(csvLineResult[i*2+1]);
            rankPredicted = Integer.parseInt(csvLineResult[i*2+2]);
            if (rankPredicted == 1) {
                rankMedio[Util.algorithmAmount] = rankAtual;
            }
            rankMedio[i+1+Util.algorithmAmount] = rankAtual;
        }
        
        //setWidth
        for (int i = (1 + Util.algorithmAmount); i < (1+Util.algorithmAmount*2); i++) {
            if (rankMedio[Util.algorithmAmount] == rankMedio[i]) {
                rankMedio[i] = rankMedio[i] + 0.5d;
                rankMedio[Util.algorithmAmount] = rankMedio[Util.algorithmAmount] + 0.5d;
            }
        }
        
        //increase rank
        double value = rankMedio[Util.algorithmAmount] + 0.5;
        for (int i = (1 + Util.algorithmAmount); i < (1+Util.algorithmAmount*2); i++) {
            if (rankMedio[i] >= value) {
                rankMedio[i] = rankMedio[i] + 1d;
            }
        }
        
        for (int i = Util.algorithmAmount; i < (1+Util.algorithmAmount*2); i++) {
            rankTotal[i] += rankMedio[i];
        }
    }
    
    private void writeToCSV(List<String> dataSets, List<double[]> rankMedios, double[] rankTotal) {
        try
        {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.getFilePath(Util.RANKING_EXP, "ML-KNNvsRankMedia")), "UTF-8"))) {
                bw.write("dataSetName"+CSV_SEPARATOR+"NB"+CSV_SEPARATOR+"RF"+CSV_SEPARATOR+"J48"+CSV_SEPARATOR+"IBK"+CSV_SEPARATOR+"SMO"+CSV_SEPARATOR+"MLP"+CSV_SEPARATOR+"ABM"+CSV_SEPARATOR
                        +"RANK-META"+CSV_SEPARATOR+"RANK-NB"+CSV_SEPARATOR+"RANK-RF"+CSV_SEPARATOR+"RANK-J48"+CSV_SEPARATOR+"RANK-IBK"+CSV_SEPARATOR
                        +"RANK-SMO"+CSV_SEPARATOR+"RANK-MLP"+CSV_SEPARATOR+"RANK-ABM");
                bw.newLine();
                StringBuffer oneLine;
                double[] rankMedio;
                for (int i = 0; i < dataSets.size(); i++) {
                    oneLine = new StringBuffer();
                    
                    oneLine.append(dataSets.get(i));
                    oneLine.append(CSV_SEPARATOR);
                    
                    rankMedio = rankMedios.get(i);
                    
                    for (double d : rankMedio) {
                        oneLine.append(Utils.doubleToString(d, 3));
                        oneLine.append(CSV_SEPARATOR);
                    }
                    bw.write(oneLine.toString());
                    bw.newLine();
                }
                
                oneLine = new StringBuffer();
                oneLine.append("MEDIA");
                oneLine.append(CSV_SEPARATOR);
                for (double d : rankTotal) {
                    oneLine.append(Utils.doubleToString(d/dataSets.size(), 3));
                    oneLine.append(CSV_SEPARATOR);
                }
                bw.write(oneLine.toString());
                bw.newLine();
                oneLine = new StringBuffer();
                oneLine.append("StDev");
                oneLine.append(CSV_SEPARATOR);
                double[] stDev = getStDev(rankMedios, rankMedios.get(0).length);
                for (double d : stDev) {
                    oneLine.append(Utils.doubleToString(d, 3));
                    oneLine.append(CSV_SEPARATOR);
                }
                bw.write(oneLine.toString());
                
                bw.flush();
            }
        }
        catch (UnsupportedEncodingException e) {}
        catch (FileNotFoundException e){}
        catch (IOException e){}
    }
    
    private double[] getStDev(List<double[]> rankMedios, int size) {
        double[] stDev = new double[size];
        Statistics statistics;
        double[] values;
        for (int i = size-1; i > -1; i--){
            values = new double[rankMedios.size()];
            for (int j = 0; j < rankMedios.size(); j++) {
                values[j] = rankMedios.get(j)[i];
            }
            statistics = new Statistics(values);
            stDev[i] = statistics.getStdDev();
        }
        return stDev;
    }
}