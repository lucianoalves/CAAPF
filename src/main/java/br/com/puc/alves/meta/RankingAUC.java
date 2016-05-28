/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.puc.alves.meta;

import br.com.puc.alves.utils.Util;
import br.com.puc.alves.base.MLAlgorithmEnum;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import static br.com.puc.alves.utils.Util.CSV_SEPARATOR;
import java.util.LinkedHashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author luciano
 */
public class RankingAUC {
    final static Logger logger = Logger.getLogger(RankingAUC.class);
    private Map<Integer, List<Double>> spearmans;
    
    public static void main(String[] args) {
        RankingAUC ranking = new RankingAUC();
        ranking.process();
    }
    
    public RankingAUC() {}
    
    public void process() {
        try {
            Map<String, double[]> lstRankings;
            double[] rankings;
            spearmans = new LinkedHashMap<>(30);
            //Instances instancesMetaBase = new DataSource(Util.META_NIVEL + Util.DB_TYPE +Util.SEARCH_TYPE+ "/metaFeatures-" +Util.algorithmAmount + ".csv").getDataSet();
            Instances instancesMetaBase = DataSource.read(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + "metaFeatures-"+Util.MEASURE_AUC+"-"+Util.algorithmAmount +".csv");
            Instances[] instances = new Instances[Util.algorithmAmount];
            for (int k = 1; k < 2; k++) {
                lstRankings = new LinkedHashMap<>(instancesMetaBase.size());
                
                for (int i = 0; i < Util.algorithmAmount; i++) {
                    instances[i] = new DataSource(Util.RANKING_PREDICT + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + Util.META_BASE_TYPE + "/predictions-"+ MLAlgorithmEnum.values()[i] + "-" + Util.algorithmAmount + "-RANK-AUC-K-"+k+".arff").getDataSet();    
                }
                
                Instance instance;
                String dataSetName;
                for (int n = 0; n < instances[0].numInstances(); n++) {
                    dataSetName = instancesMetaBase.get(n).stringValue(0);
                    rankings = new double[Util.algorithmAmount*3+2];
                    int r = 0;
                    for (Instances ins : instances) {
                        instance = ins.get(n);
                        rankings[r] = instance.value(Util.getPredictorPosition(0));
                        r++;
                        rankings[r] = instance.value(Util.getPredictorPosition(1));
                        r++;
                    }                
                    
                    //getAUC(instancesMetaBase, rankings, 0, n);
                    //getAUC(instancesMetaBase, rankings, 1, n);
    
                    
                    for (int i = 0; i < Util.algorithmAmount; i++) {
                        rankings[Util.algorithmAmount*2+2+i] = instancesMetaBase.get(n).value(i + 23);
                    }
                                        
                    lstRankings.put(dataSetName, rankings);
                }
                writeToCSV(lstRankings, k, instancesMetaBase.size());
                //writeToCSVSpearman(instancesMetaBase.size());
            }
            
        } catch (Exception e) {
            logger.error("Exception is", e);
        }
    }
            
    public double getBestRank(double[] rankings, int rankType) throws Exception {
        int pos = rankType;
        double rank = rankings[pos];
        for (int i = (pos + 2); i < 14; i+=2) {
            if (rank > rankings[i]) {
                rank = rankings[i];
                pos = i;
            }
        }
        return rankings[pos];
    }
    
    public void getAUC(Instances instances, double[] rankings, int rankType, int line) throws Exception {
        int pos = rankType;
        double rank = rankings[pos];
        for (int i = pos + 2; i < 14; i+=2) {
            if (rank > rankings[i]) {
                rank = rankings[i];
                pos = i;
            }
        }
        if (rankType == 0) pos = pos + 1;
        
        rankings[rankType+(Util.algorithmAmount*2)] = instances.get(line).value(pos + 23);
    }
    
    private double setSpearmanRank(double[] rankings) {
        double spearman = 0;
        double d;
        for (int i = 0; i < Util.algorithmAmount * 2; i+=2) {
            d = rankings[i] - rankings[i+1];
            spearman = spearman + d * d;
        }
        spearman = 1 - (6 * spearman / (Util.algorithmAmount * Util.algorithmAmount * Util.algorithmAmount - Util.algorithmAmount));
        return spearman;
    }
    
    private void writeToCSV(Map<String, double[]> rankings, int k, int metaBaseSize) { 
        try
        {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.RANKING_RESULT + Util.DB_TYPE+ Util.SEARCH_TYPE + "/" + Util.META_BASE_TYPE + "/rankingAlgorithmByAUC-" + Util.algorithmAmount +"-K-"+k+".csv"), "UTF-8"))) {
                bw.write("DataSetName");
                for (int i = 0; i < Util.algorithmAmount; i++) {
                    bw.write(CSV_SEPARATOR);
                    bw.write(MLAlgorithmEnum.values()[i]+"-Atual");
                    bw.write(CSV_SEPARATOR);
                    bw.write(MLAlgorithmEnum.values()[i]+"-Predicted");
                }
                bw.write(CSV_SEPARATOR+"AUC-ATUAL"+CSV_SEPARATOR+"AUC-PREDICTED");
                for (int i = 0; i < Util.algorithmAmount; i++) {
                    bw.write(CSV_SEPARATOR);
                    bw.write(MLAlgorithmEnum.values()[i]+"-AUC");
                }
                bw.write(CSV_SEPARATOR+"SPEARMAN");
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
                for (int i = 0; i < Util.algorithmAmount*3+3; i++) {
                    bw.write(""+CSV_SEPARATOR);
                }
                bw.write(Utils.doubleToString((sumSpearman/metaBaseSize), 3));
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
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.RANKING_RESULT + Util.DB_TYPE +Util.SEARCH_TYPE + "/" + Util.META_BASE_TYPE +"/spearmanByAUC-"+ Util.algorithmAmount+".csv"), "UTF-8"))) {
                bw.write("K=1"+CSV_SEPARATOR+"K=2"+CSV_SEPARATOR+"K=3"+CSV_SEPARATOR+"K=4"+CSV_SEPARATOR+"K=5"+CSV_SEPARATOR
                        +"K=6"+CSV_SEPARATOR+"K=7"+CSV_SEPARATOR+"K=8"+CSV_SEPARATOR+"K=9"+CSV_SEPARATOR+"K=10"+CSV_SEPARATOR
                        +"K=11"+CSV_SEPARATOR+"K=12"+CSV_SEPARATOR+"K=13"+CSV_SEPARATOR+"K=14"+CSV_SEPARATOR+"K=15"+CSV_SEPARATOR
                        +"K=16"+CSV_SEPARATOR+"K=17"+CSV_SEPARATOR+"K=18"+CSV_SEPARATOR+"K=19"+CSV_SEPARATOR+"K=20"+CSV_SEPARATOR
                        +"K=21"+CSV_SEPARATOR+"K=22"+CSV_SEPARATOR+"K=23"+CSV_SEPARATOR+"K=24"+CSV_SEPARATOR+"K=25"+CSV_SEPARATOR
                        +"K=26"+CSV_SEPARATOR+"K=27"+CSV_SEPARATOR+"K=28"+CSV_SEPARATOR+"K=29"+CSV_SEPARATOR+"K=30");
                bw.newLine();
                StringBuffer oneLine;
                for (int i=0; i<(metaBaseSize); i++) {
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