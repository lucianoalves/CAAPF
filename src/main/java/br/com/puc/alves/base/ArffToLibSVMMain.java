/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.base;

import br.com.puc.alves.utils.Util;
import java.io.File;
import java.util.Arrays;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.LibSVMSaver;

/**
 *
 * @author luciano
 */
public class ArffToLibSVMMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File[] files = new File(Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE).listFiles();
        Arrays.sort(files, (Object f1, Object f2) -> ((File) f1).getName().toLowerCase().compareTo(((File) f2).getName().toLowerCase()));
        String fileName;
        ArffLoader arffLoader;
        LibSVMSaver libSVMSaver;
        try {
            for (final File file : files) {
                if (!file.isDirectory() && file.getName().contains("arff")) {
                    fileName = file.getName();
                    arffLoader = new ArffLoader();
                    arffLoader.setSource(file);
                    libSVMSaver = new LibSVMSaver();
                    Instances instances = arffLoader.getDataSet();
                    instances.setClassIndex(instances.numAttributes()-1);
                    instances.stratify(Util.numFolds);
                    libSVMSaver.setInstances(instances);
                    libSVMSaver.setFile(new File(Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE + "/libSVM/" + fileName.replace(".arff", "") + ".txt"));
                    libSVMSaver.writeBatch();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
