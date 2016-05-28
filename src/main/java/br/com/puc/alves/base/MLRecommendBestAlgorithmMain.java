/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.base;

import br.com.puc.alves.utils.Util;
import java.io.BufferedWriter;
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

/**
 *
 * @author luciano
 */
public class MLRecommendBestAlgorithmMain {
    final static Logger logger = Logger.getLogger(MLRecommendBestAlgorithmMain.class);
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MLRecommendBestAlgorithmMain main = new MLRecommendBestAlgorithmMain();
        
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
    
    private void writeToCSV(List<String> rankings, Map<String, List<MLAlgorithmBean>> map)
    {
        try
        {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE +"/"+ "metaFeaturesWithBestAlgorithm-"+Util.MEASURE_TYPE+"-"+Util.algorithmAmount +".csv"), "UTF-8"))) {
                for (String s : rankings) {
                    bw.write(s);
                    bw.write(Util.CSV_SEPARATOR);
                    if (!s.contains("dataSetName")) {                        
                        int rank;
                        List<MLAlgorithmBean> list = map.get(s.split(",")[0]);
                        for (int i = 0; i < list.size(); i++) {
                            rank = new Double(getRankByAlgorithm(list, MLAlgorithmEnum.values()[i])).intValue();
                            if (rank == 1d) {
                                bw.write(MLAlgorithmEnum.values()[i].name());
                            }
                        }
                    } else {
                        bw.write("Class");
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
