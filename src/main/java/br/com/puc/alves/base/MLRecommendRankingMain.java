/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.base;

import br.com.puc.alves.utils.Util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

/**
 *
 * @author luciano
 */
public class MLRecommendRankingMain {
    final static Logger logger = Logger.getLogger(MLRecommendRankingMain.class);
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MLRecommendRankingMain main = new MLRecommendRankingMain();
        
        List<String> rankings = Util.getCsvToList(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + "metaFeatures-"+Util.MEASURE_TYPE+"-"+Util.algorithmAmount +".csv");
        Map<String, List<MLAlgorithmBean>> listDataSets = new HashMap<>();
        rankings.stream().map((line) -> line.split(",")).forEach((String[] lineSplit) -> {
            if (!lineSplit[0].equals("dataSetName")) {
                int ordinal = 0;
                List<MLAlgorithmBean> listBean = new ArrayList<>();
                MLAlgorithmBean bean;
                for (int i = 23; i < Util.algorithmAmount + 23; i++) {
                    double measure = Double.valueOf(lineSplit[i]);
                    MLAlgorithmEnum algorithm = MLAlgorithmEnum.values()[ordinal]; 
                    bean = new MLAlgorithmBean(algorithm, measure, 0);
                    listBean.add(bean);
                    ordinal++;
                }
                Collections.sort(listBean);
                
                main.setOrderMLA(listBean);
                
                listDataSets.put(lineSplit[0], listBean);
            }
        });
        main.writeToCSV(rankings, listDataSets);
        main.writeToCSVRankByOrder(rankings, listDataSets);
    }
    
    private void setOrderMLA(List<MLAlgorithmBean> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRank(i+1);
        }
    }
    
    private double getRankByAlgorithm(List<MLAlgorithmBean> list, MLAlgorithmEnum algorithmEnum) {
        double rank = 0;
        for (MLAlgorithmBean bean : list) {
            if (bean.getAlgorithmEnum().ordinal() == algorithmEnum.ordinal()) {
                rank = bean.getRank();
                break;
            }
        }
        return rank;
    }
    
    private void writeToCSVRankByOrder(List<String> rankings, Map<String, List<MLAlgorithmBean>> map)
    {
        try
        {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE +"/"+ "metaFeaturesWithRankingByOrder-"+Util.MEASURE_TYPE+"-"+Util.algorithmAmount +".csv"), "UTF-8"))) {
                for (String s : rankings) {
                    bw.write(s);
                    //bw.write(Util.CSV_SEPARATOR);
                    if (!s.contains("dataSetName")) {                        
                        int rank;
                        List<MLAlgorithmBean> list = map.get(s.split(",")[0]);
                        //bw.write(Util.CSV_SEPARATOR);
                        bw.write("'");
                        for (int i = 0; i < list.size(); i++) {
                            rank = new Double(getRankByAlgorithm(list, MLAlgorithmEnum.values()[i])).intValue();
                            if (i > 0) {
                                bw.write(Util.CSV_SEPARATOR);
                            }
                            bw.write(String.valueOf(rank));
                        }
                        bw.write("'");
                    } else {
                        bw.write(Util.CSV_SEPARATOR);
                        bw.write("RANK");
                    }
                    bw.newLine();
                }
                
                bw.flush();
            }
            
            CSVLoader csvLoader = new CSVLoader();
            csvLoader.setSource(new FileInputStream(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE +"/"+ "metaFeaturesWithRankingByOrder-"+Util.MEASURE_TYPE+"-"+Util.algorithmAmount +".csv"));
                        
            ArffSaver arffSaver = new ArffSaver();
            
            Instances instances = csvLoader.getDataSet();
            for (int i = 0; i < Util.algorithmAmount; i++) {
                instances.deleteAttributeAt(instances.numAttributes() -2);
            }
            
            arffSaver.setInstances(instances);
            arffSaver.setFile(new File(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE +"/"+ "metaFeaturesWithRankingByOrder-"+Util.MEASURE_TYPE+"-"+Util.algorithmAmount +".arff"));
            arffSaver.writeBatch();
        }
        catch (UnsupportedEncodingException e) {}
        catch (FileNotFoundException e){}
        catch (IOException e){}
    }
    
    private void writeToCSV(List<String> rankings, Map<String, List<MLAlgorithmBean>> map)
    {
        try
        {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE +"/"+ "metaFeaturesWithRanking-"+Util.MEASURE_TYPE+"-"+Util.algorithmAmount +".csv"), "UTF-8"))) {
                for (String s : rankings) {
                    bw.write(s);
                    //bw.write(Util.CSV_SEPARATOR);
                    if (!s.contains("dataSetName")) {                        
                        int rank;
                        List<MLAlgorithmBean> list = map.get(s.split(",")[0]);
                        //bw.write("'");
                        for (int i = 0; i < list.size(); i++) {
                            rank = new Double(getRankByAlgorithm(list, MLAlgorithmEnum.values()[i])).intValue();
                            bw.write(String.valueOf(rank));
                            bw.write(Util.CSV_SEPARATOR);
                        }
                        //bw.write("'");
                    } else {
                        for (MLAlgorithmEnum e : MLAlgorithmEnum.values()) {
                            bw.write(e.name()+"_RANK");
                            bw.write(Util.CSV_SEPARATOR);
                        }
                    }
                    bw.newLine();
                }
                /*
                for (int i = 0; i < 22 + Util.algorithmAmount; i++) {
                    bw.write(Util.CSV_SEPARATOR);
                }
                
                for (int c : count) {
                    bw.write(Util.CSV_SEPARATOR);
                    bw.write(String.valueOf(c));
                }
                bw.newLine();
                */
                bw.flush();
            }
        }
        catch (UnsupportedEncodingException e) {}
        catch (FileNotFoundException e){}
        catch (IOException e){}
    }
    
}
