/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.puc.alves.meta;

import br.com.puc.alves.utils.Util;
import br.com.puc.alves.base.ClassifierRanking;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import static br.com.puc.alves.utils.Util.CSV_SEPARATOR;
import org.apache.log4j.Logger;

/**
 *
 * @author luciano
 */
public class RankingBalance {
    
    final static Logger logger = Logger.getLogger(RankingBalance.class);
    
    private Map<Integer, List<Double>> spearmans;
    
    public static void main(String[] args) {
        RankingBalance ranking = new RankingBalance();
        ranking.process();
    }
    
    public RankingBalance() {}
    
    public void process() {
        try {
            Map<String, double[]> lstRankings;
            double[] rankings;
            spearmans = new LinkedHashMap<>(30);
            Instances instancesRF = null;
            Instances instancesMetaBase = new DataSource(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE + "/metaFeatures-"+ Util.algorithmAmount + ".csv").getDataSet();
            for (int k = 1; k < 2; k++) {
                lstRankings = new LinkedHashMap<>(instancesMetaBase.size());
                Instances instancesNB = getInstances(ClassifierRanking.NB, k);
                if (Util.algorithms.contains(ClassifierRanking.RF)) {
                    instancesRF = getInstances(ClassifierRanking.RF, k);   
                }
                Instances instancesJ48 = getInstances(ClassifierRanking.J48, k);
                Instances instancesIBK = getInstances(ClassifierRanking.IBK, k);
                Instances instancesSMO = getInstances(ClassifierRanking.SMO, k);
                Instances instancesMLP = getInstances(ClassifierRanking.MLP, k);
                Instances instancesABM = getInstances(ClassifierRanking.ABM, k);

                Instance instance;
                String dataSetName;
                for (int n = 0; n < instancesNB.numInstances(); n++) {
                    instance = instancesNB.get(n);
                    dataSetName = instancesMetaBase.get(n).stringValue(0);
                    rankings = new double[24];
                    rankings[0] = instance.value(Util.getPredictorPosition(0));
                    rankings[1] = instance.value(Util.getPredictorPosition(1));
                    if (Util.algorithms.contains(ClassifierRanking.RF)) {
                        getValues(instancesRF, 2, rankings, n);
                    }
                    getValues(instancesJ48, 4, rankings, n);
                    getValues(instancesIBK, 6, rankings, n);
                    getValues(instancesSMO, 8, rankings, n);
                    getValues(instancesMLP, 10, rankings, n);
                    getValues(instancesABM, 12, rankings, n);
                    getBalance(instancesMetaBase, rankings, 0, n);
                    getBalance(instancesMetaBase, rankings, 1, n);
                    rankings[16] = getBestRank(dataSetName, rankings, 1);
                    rankings[17] = getBalance(instancesMetaBase, ClassifierRanking.NB, n);
                    rankings[18] = getBalance(instancesMetaBase, ClassifierRanking.RF, n);
                    rankings[19] = getBalance(instancesMetaBase, ClassifierRanking.J48, n);
                    rankings[20] = getBalance(instancesMetaBase, ClassifierRanking.IBK, n);
                    rankings[21] = getBalance(instancesMetaBase, ClassifierRanking.SMO, n);
                    rankings[22] = getBalance(instancesMetaBase, ClassifierRanking.MLP, n);
                    rankings[23] = getBalance(instancesMetaBase, ClassifierRanking.ABM, n);
                    lstRankings.put(dataSetName, rankings);
                }
                writeToCSV(lstRankings, k, instancesMetaBase.size());
                //writeToCSVSpearman(instancesMetaBase.size());
            }
            
        } catch (Exception e) {
            logger.error("Exception is", e);
        }
    }
    
    public Instances getInstances(String algorithmName, int k) throws Exception {
        Instances instances;
        instances = new DataSource(Util.RANKING_PREDICT + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + Util.META_BASE_TYPE + "/predictions-"+ algorithmName + "-" + Util.algorithmAmount + "-RANK-Balance-K-"+k+".arff").getDataSet();
        return instances;
    }
    
    public void getValues(Instances instances, int pos, double[] rankings, int line) throws Exception {
        Instance instance = instances.get(line);
        rankings[pos] = instance.value(Util.getPredictorPosition(0));
        rankings[pos+1] = instance.value(Util.getPredictorPosition(1));
    }
    
    public double getBestRank(String dataSetName, double[] rankings, int rankType) throws Exception {
        int pos = rankType;
        double rank = rankings[pos];
        for (int i = pos + 2; i < 14; i+=2) {
            if (rank > rankings[i]) {
                rank = rankings[i];
                pos = i;
            }
        }
        return rankings[pos];
    }
    
    public double getBalance(Instances instances, String classifierRanking, int line) throws Exception {
        int pos = 0;
        
        switch (classifierRanking) {
            case ClassifierRanking.NB:
                pos = 1;
                break;
            case ClassifierRanking.RF:
                pos = 3;
                break;
            case ClassifierRanking.J48:
                pos = 5;
                break;
            case ClassifierRanking.IBK:
                pos = 7;
                break;
            case ClassifierRanking.SMO:
                pos = 9;
                break;
            case ClassifierRanking.MLP:
                pos = 11;
                break;
            case ClassifierRanking.ABM:
                pos = 13;
                break;
        }
        return instances.get(line).value(pos + 37);
    }
    
    public void getBalance(Instances instances, double[] rankings, int rankType, int line) throws Exception {
        int pos = rankType;
        double rank = rankings[pos];
        for (int i = pos + 2; i < 14; i+=2) {
            if (rank > rankings[i]) {
                rank = rankings[i];
                pos = i;
            }
        }
        if (rankType == 0) pos = pos + 1;
        rankings[rankType+(Util.algorithmAmount*2)] = instances.get(line).value(pos + 37);
    }
      
    private double setSpearmanRank(double[] rankings) {
        double a1 = rankings[0] - rankings[1];
        double a2 = rankings[2] - rankings[3];
        double a3 = rankings[4] - rankings[5];
        double a4 = rankings[6] - rankings[7];
        double a5 = rankings[8] - rankings[9];
        double a6 = rankings[10] - rankings[11];
        double a7 = rankings[12] - rankings[13];
        double spearman = a1 * a1 + a2 * a2 + a3 * a3 + a4 * a4 + a5 * a5 + a6 * a6 + a7 * a7;
        spearman = 1 - (Util.algorithmAmount * spearman / (Util.algorithmAmount * Util.algorithmAmount * Util.algorithmAmount - Util.algorithmAmount));
        return spearman;
    }
    
    private void writeToCSV(Map<String, double[]> rankings, int k, int metaBaseSize) { 
        try
        {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.RANKING_RESULT + Util.DB_TYPE+ Util.SEARCH_TYPE+ "/" + Util.META_BASE_TYPE +"/rankingAlgorithmByBalance-"+ Util.algorithmAmount + "-K-"+k+".csv"), "UTF-8"))) {
                bw.write("DataSetName"+CSV_SEPARATOR+"NB-Atual"+CSV_SEPARATOR+"NB-Predicted"+CSV_SEPARATOR+"RF-Atual"+CSV_SEPARATOR+"RF-Predicted"+CSV_SEPARATOR
                        +"J48-Atual"+CSV_SEPARATOR+"J48-Predicted"+CSV_SEPARATOR+"IBK-Atual"+CSV_SEPARATOR+"IBK-Predicted"+CSV_SEPARATOR
                        +"SVM-Atual"+CSV_SEPARATOR+"SVM-Predicted"+CSV_SEPARATOR+"MLP-Atual"+CSV_SEPARATOR+"MLP-Predicted"+CSV_SEPARATOR+"ABM-Atual"+CSV_SEPARATOR+"ABM-Predicted"+CSV_SEPARATOR+"Balance-ATUAL"+CSV_SEPARATOR
                        +"Balance-PREDICTED"+CSV_SEPARATOR+"BEST-RANK"+CSV_SEPARATOR+"NB-Balance"+CSV_SEPARATOR+"RF-Balance"+CSV_SEPARATOR+"J48-Balance"+CSV_SEPARATOR+"IBK-Balance"+CSV_SEPARATOR+"SMO-Balance"+CSV_SEPARATOR+"MLP-Balance"+CSV_SEPARATOR+"ABM-Balance"+CSV_SEPARATOR+"SPEARMAN");
                bw.newLine();
                
                StringBuffer oneLine;
                List<Double> listSpearmans = new ArrayList<>(metaBaseSize+1);
                double sumSpearman = 0d;
                for (Map.Entry<String, double[]> entry : rankings.entrySet()) {
                    oneLine = new StringBuffer();
                    oneLine.append(entry.getKey());
                    oneLine.append(CSV_SEPARATOR);
                    double[] rank = entry.getValue();
                    int c = 1;
                    for (double r : rank) {
                        oneLine.append(Utils.doubleToString(r, 3));
                        oneLine.append(CSV_SEPARATOR);
                        c++;
                    }
                    double spearman = setSpearmanRank(rank);
                    listSpearmans.add(spearman);
                    sumSpearman += spearman;
                    oneLine.append(Utils.doubleToString(spearman, 3));
                    //oneLine.append(CSV_SEPARATOR);
                    bw.write(oneLine.toString());
                    bw.newLine();
                }
                listSpearmans.add(sumSpearman/metaBaseSize);
                spearmans.put(k, listSpearmans);
                bw.write(""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR
                        +""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR
                        +""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+""+CSV_SEPARATOR+Utils.doubleToString((sumSpearman/metaBaseSize), 3));
                bw.flush();
            }
        }
        catch (UnsupportedEncodingException e) {}
        catch (FileNotFoundException e){}
        catch (IOException e){}
    }
    
    private void writeToCSVSpearman(int metaBaseSize) {
        try
        {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.RANKING_RESULT + Util.DB_TYPE+Util.SEARCH_TYPE+ "/" + Util.META_BASE_TYPE +"/spearmanByBalance-"+ Util.algorithmAmount + ".csv"), "UTF-8"))) {
                bw.write("K=1"+CSV_SEPARATOR+"K=2"+CSV_SEPARATOR+"K=3"+CSV_SEPARATOR+"K=4"+CSV_SEPARATOR+"K=5"+CSV_SEPARATOR
                        +"K=6"+CSV_SEPARATOR+"K=7"+CSV_SEPARATOR+"K=8"+CSV_SEPARATOR+"K=9"+CSV_SEPARATOR+"K=10"+CSV_SEPARATOR
                        +"K=11"+CSV_SEPARATOR+"K=12"+CSV_SEPARATOR+"K=13"+CSV_SEPARATOR+"K=14"+CSV_SEPARATOR+"K=15"+CSV_SEPARATOR
                        +"K=16"+CSV_SEPARATOR+"K=17"+CSV_SEPARATOR+"K=18"+CSV_SEPARATOR+"K=19"+CSV_SEPARATOR+"K=20"+CSV_SEPARATOR
                        +"K=21"+CSV_SEPARATOR+"K=22"+CSV_SEPARATOR+"K=23"+CSV_SEPARATOR+"K=24"+CSV_SEPARATOR+"K=25"+CSV_SEPARATOR
                        +"K=26"+CSV_SEPARATOR+"K=27"+CSV_SEPARATOR+"K=28"+CSV_SEPARATOR+"K=29"+CSV_SEPARATOR+"K=30");
                bw.newLine();
                StringBuffer oneLine;
                for (int i=0; i <= metaBaseSize; i++) {
                    oneLine = new StringBuffer();
                    for (Map.Entry<Integer, List<Double>> entry : spearmans.entrySet()) {
                        oneLine.append(Utils.doubleToString(entry.getValue().get(i), 3));
                        oneLine.append(CSV_SEPARATOR);
                    }
                    bw.write(oneLine.toString());
                    bw.newLine();
                }
                bw.flush();
            }
        }
        catch (UnsupportedEncodingException e) {}
        catch (FileNotFoundException e){}
        catch (IOException e){}
    }
}