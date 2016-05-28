/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.lessmann;

import br.com.puc.alves.base.MLAlgorithmEnum;
import br.com.puc.alves.base.MetaFeatures;
import br.com.puc.alves.utils.Util;
import static br.com.puc.alves.utils.Util.CSV_SEPARATOR;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author luciano
 */
public class GenerateMetaBaseMain {

    final static Logger logger = Logger.getLogger(GenerateMetaBaseMain.class);
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        GenerateMetaBaseMain main = new GenerateMetaBaseMain();
        main.init();
    }
    
    public void init() {
        List<MetaFeatures> listFeatures = getMetaFeatures();
        Map<String, List<Output>> mapOutputs = new LinkedHashMap();
        for (int i = 0; i < Util.algorithmAmount; i++) {
            String fileName = Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + MLAlgorithmEnum.values()[i].name() + ".csv";
            if (Files.exists(Paths.get(fileName))) {
                mapOutputs.put(MLAlgorithmEnum.values()[i].name(), getOutput(fileName));
            }            
        }
        writeToCSV(listFeatures, mapOutputs);
    }
    
    public List<MetaFeatures> getMetaFeatures() {
        List<MetaFeatures> listFeatures = new ArrayList();
        List<String> listCSV = Util.getCsvToList(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE + "/metaFeatures.csv");
        MetaFeatures features;
        for (String s : listCSV) {
            String[] l = s.split(",");
            if (l[0].equals("dataSetName") || l[0].trim().equals("")) continue;
            features = new MetaFeatures();
            features.setDataSetName(l[0]);
            features.setExample(Integer.parseInt(l[1]));
            features.setAttribute(Integer.parseInt(l[2]));
            features.setClazz(Integer.parseInt(l[3]));
            features.setBinaryAttributes(Integer.parseInt(l[4]));
            features.setF1(new Double(l[5]));
            features.setF1v(new Double(l[6]));
            features.setF2(new Double(l[7]));
            features.setF3(new Double(l[8]));
            features.setF4(new Double(l[9]));
            features.setL1(new Double(l[10]));
            features.setL2(new Double(l[11]));
            features.setL3(new Double(l[12]));
            features.setN1(new Double(l[13]));
            features.setN2(l[14].equals("?") ? -100D : new Double(l[14]));
            features.setN3(new Double(l[15]));
            features.setN4(new Double(l[16]));
            features.setT1(new Double(l[17]));
            features.setT2(new Double(l[18]));
            features.setSkew(new Double(l[19]));
            features.setKurtosis(new Double(l[20]));
            features.setMultipleCorrelation(new Double(l[21]));
            features.setsDRatio(l[22].equals("?") ? -100D : new Double(l[22]));
            listFeatures.add(features);            
        }
        
        return listFeatures;
    }
    
    public List<Output> getOutput(String fileName) {
        List<Output> listOutput = new ArrayList();
        List<String> listCSV = Util.getCsvToList(fileName);
        Output output;
        for (String s : listCSV) {
            String[] l = s.split(",");
            if (l[0].equals("dataSetName") || l[0].trim().equals("")) continue;
            double aucDefective = new Double(l[1]);
            double aucNonDefective = new Double(l[2]);
            double balance = new Double(l[3]);
            output = new Output(l[0], aucDefective, aucNonDefective, balance);
            listOutput.add(output);
        }
        Collections.sort(listOutput);
        return listOutput;
    }
    
    
    private void writeToCSV(List<MetaFeatures> listFeatures, Map<String, List<Output>> mapOutputs) {
        try
        {
            BufferedWriter bwAUC = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE +"/"+ "metaFeatures-AUC-"+mapOutputs.size() +".csv"), "UTF-8"));
            BufferedWriter bwBalance = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE +"/"+ "metaFeatures-Balance-"+mapOutputs.size() +".csv"), "UTF-8"));
            StringBuffer oneLine = new StringBuffer();

            Field[] fields = MetaFeatures.class.getFields();
            for (Field field : fields) {
                if (!field.getName().equals("classifierAUC") && !field.getName().equals("classifierBalance") && !field.getName().equals("rankings")) {
                    oneLine.append(field.getName());
                    oneLine.append(Util.CSV_SEPARATOR);
                }
            }
            
            for (Map.Entry<String, List<Output>> entry : mapOutputs.entrySet()) {
                oneLine.append(entry.getKey());
                oneLine.append(CSV_SEPARATOR);
            }
            bwAUC.write(oneLine.substring(0, oneLine.length()-1));
            bwAUC.newLine();
            
            bwBalance.write(oneLine.substring(0, oneLine.length()-1));
            bwBalance.newLine();
            
            int i = 0;
            StringBuffer aucLine;
            StringBuffer balanceLine;
            for (MetaFeatures metaFeatures : listFeatures)
            {
                oneLine = new StringBuffer();
                oneLine.append(metaFeatures.getDataSetName());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getExample());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getAttribute());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getClazz());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getBinaryAttributes());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getF1());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getF1v());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getF2());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getF3());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getF4());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getL1());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getL2());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getL3());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getN1());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getN2() == -100D ? "?" : metaFeatures.getN2());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getN3());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getN4());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getT1());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getT2());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getSkew());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getKurtosis());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getMultipleCorrelation());
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append(metaFeatures.getsDRatio() == -100D ? "?" : metaFeatures.getsDRatio());
                oneLine.append(Util.CSV_SEPARATOR);
                    
                aucLine = new StringBuffer();
                balanceLine = new StringBuffer();
                for (Map.Entry<String, List<Output>> entry : mapOutputs.entrySet()) {
                    Output output = entry.getValue().get(i);
                    aucLine.append(output.getAucDefective());
                    aucLine.append(Util.CSV_SEPARATOR);
                    balanceLine.append(output.getBalance());
                    balanceLine.append(Util.CSV_SEPARATOR);
                }
                i++;
                bwAUC.write(oneLine.toString());
                bwAUC.write(aucLine.substring(0, aucLine.length()-1));
                bwAUC.newLine();
                bwBalance.write(oneLine.toString());
                bwBalance.write(balanceLine.substring(0, balanceLine.length()-1));
                bwBalance.newLine();
            }
            bwAUC.flush();
            bwBalance.flush();
        }
        catch (UnsupportedEncodingException e) {}
        catch (FileNotFoundException e){}
        catch (IOException e){}
    }
}
