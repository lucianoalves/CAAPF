/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.sac;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToBinary;
import weka.filters.unsupervised.attribute.NumericToNominal;

/**
 *
 * @author luciano
 */
public class ConvertCsvToArff {

    final static Logger logger = Logger.getLogger(ConvertCsvToArff.class);
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        File[] files = new File(SacUtils.SAC + SacUtils.CSV).listFiles();
        CSVLoader loader = new CSVLoader();
        loader.setNoHeaderRowPresent(true);
        for (File file : files) {
            if (!file.isDirectory()) {
                try {
                    loader.setSource(file);
                    Instances originalInstances = loader.getDataSet();
                                        
                    NumericToNominal convert= new NumericToNominal();
                    String[] options= new String[2];
                    options[0] = "-R";
                    options[1] = ""+(originalInstances.numAttributes());

                    convert.setOptions(options);
                    convert.setInputFormat(originalInstances);

                    Instances instances = Filter.useFilter(originalInstances, convert);
                    instances.setClassIndex(instances.numAttributes()-1);
                    
                    // save ARFF
                    ArffSaver saver = new ArffSaver();
                    saver.setInstances(instances);
                    
                    String fileName = file.getName().replaceFirst("[.][^.]+$", "");
                    //fileName = fileName.replaceAll("((2))", "");
                    //fileName = fileName.replaceAll("((3))", "");
                    
                    File newFile = new File(SacUtils.SAC + SacUtils.ARFF+fileName);
                    saver.setFile(newFile);
                    saver.setDestination(newFile);
                    saver.writeBatch();
                } catch (Exception e) {
                    logger.error(e);
                    logger.warn("File ->"+file.getName());
                }
            }
        }       
    }
}
