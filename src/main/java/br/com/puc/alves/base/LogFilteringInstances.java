/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.base;

import br.com.puc.alves.utils.Util;
import java.io.File;
import org.apache.log4j.Logger;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;


/**
 *
 * @author ssad
 */
public class LogFilteringInstances {
    
    final static Logger logger = Logger.getLogger(LogFilteringInstances.class);
    
    public static void main(String[] args) {
        LogFilteringInstances log = new LogFilteringInstances();
        log.init();
    }
    
    public void init() {
        String fileName = "";
        
        try {
            Instances instances;
            final File file = new File(Util.DB_DF_PRED + Util.DB_TYPE_OFF);
            for (final File fileEntry : file.listFiles()) {
                    //if (!fileEntry.isDirectory() && fileEntry.getName().contains("arff") && !fileEntry.getName().contains(".txt") && !fileEntry.getName().contains(".classifiers")) {
                        fileName = fileEntry.getName();

                        instances = new DataSource(Util.DB_DF_PRED + Util.DB_TYPE_OFF + fileName).getDataSet();
                        applyLogFiltering(instances, fileName);
                    //}
            }
        } catch(Exception e) {
            logger.error("Error in file: "+fileName, e);
        }

    }
    
    public void applyLogFiltering(Instances instances, String dataSetName) {
        for (int i = 0; i < instances.numInstances(); i++) {
            Instance instance = instances.get(i);
            for (int j = 0; j < instance.numAttributes() -1; j++) {
                double value = instance.value(j);
                if (value > 0d) {
                    value = Math.log10(value);
                    value = value / Math.log10(2);
                    instance.setValue(j, value);
                }
            }
        }
        try {
            DataSink.write(Util.DB_DF_PRED + Util.DB_TYPE_LOG + "/" + dataSetName, instances);
        } catch(Exception e) {
            logger.error("Error in file: "+dataSetName, e);
        }
    }
    
    
}
