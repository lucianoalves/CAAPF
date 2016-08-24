/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.meta;

import br.com.puc.alves.utils.Util;
import java.io.File;
import java.util.Arrays;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;
import weka.core.converters.LibSVMSaver;

/**
 *
 * @author luciano
 */
public class CsvToLibSVMMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File[] files = new File[2];
        files[0] = new File(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE + "/metaFeaturesWithBestAlgorithm-"+Util.MEASURE_AUC+"-"+Util.algorithmAmount+".csv");
        files[1] = new File(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE + "/metaFeaturesWithBestAlgorithm-"+Util.MEASURE_BALANCE+"-"+Util.algorithmAmount+".csv");
        String fileName;
        CSVLoader cSVLoader;
        LibSVMSaver libSVMSaver;
        try {
            for (final File file : files) {
                
                fileName = file.getName();
                cSVLoader = new CSVLoader();
                cSVLoader.setSource(file);
                libSVMSaver = new LibSVMSaver();
                Instances instances = cSVLoader.getDataSet();
                removeAttributes(instances);
                instances.setClassIndex(instances.numAttributes()-1);
                instances.stratify(Util.numFolds);
                libSVMSaver.setInstances(instances);
                libSVMSaver.setFile(new File(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE + "/libSVM/" + fileName.replace(".csv", "") + ".txt"));
                libSVMSaver.writeBatch();
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void removeAttributes(Instances instances) {
        for (int i = 0; i < Util.algorithmAmount; i++) {
            instances.deleteAttributeAt(instances.numAttributes() -2);
        }
        instances.deleteAttributeAt(0);
    }
    
}
