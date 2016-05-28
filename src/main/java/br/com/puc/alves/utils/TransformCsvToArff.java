/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.utils;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

/**
 *
 * @author luciano
 */
public class TransformCsvToArff {

    final static Logger logger = Logger.getLogger(TransformCsvToArff.class);
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        String source = "/home/luciano/Projects/dataSets/repo/";
        
        File[] files = new File(source).listFiles();
        CSVLoader loader = new CSVLoader();
        File file = new File("/home/luciano/Projects/dataSets/repo/berek.csv");
        //for (File file : files) {
            if (!file.isDirectory() && !file.getName().contains("arff")) {
                try {
                    loader.setSource(file);
                    Instances instances = loader.getDataSet();
                    instances.setClass(instances.attribute(instances.numAttributes() -1));
                    
                    // save ARFF
                    ArffSaver saver = new ArffSaver();
                    saver.setInstances(instances);
                    File newFile = new File(source+file.getName().replaceFirst("[.][^.]+$", "")+".arff");
                    saver.setFile(newFile);
                    saver.setDestination(newFile);
                    saver.writeBatch();
                } catch (Exception e) {
                    logger.error(e);
                    logger.warn("File ->"+file.getName());
                }
            }
        //}       
    }
}
