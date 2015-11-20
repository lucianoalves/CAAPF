/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.base;

import br.com.puc.alves.utils.ExperimentUtils;
import br.com.puc.alves.utils.Util;
import static br.com.puc.alves.utils.Util.CSV_SEPARATOR;
import java.io.File;
import java.util.List;
import org.apache.log4j.Logger;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;


/**
 *
 * @author ssad
 */
public class NormalizingInstances {
    
    final static Logger logger = Logger.getLogger(NormalizingInstances.class);
    
    public static void main(String[] args) {
        NormalizingInstances log = new NormalizingInstances();
        log.init();
    }
    
    public void init() {
        String fileName = "";
        
        try {
            Instances instances;
            
            ExperimentUtils experimentUtils = new ExperimentUtils();
            List<String> experiments = experimentUtils.getExperiment(0);
            
            for (String s : experiments) {
                String[] split = s.split(CSV_SEPARATOR);
                Util.DB_TYPE = split[0];
                Util.SEARCH_TYPE = split[1];
                logger.info("Experiment -> databases "+ Util.DB_TYPE.toUpperCase() + " attr sel "+Util.SEARCH_TYPE);
            
                final File file = new File(Util.DB_NORMALIZED_NO + Util.STRING_DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE);
                for (final File fileEntry : file.listFiles()) {                    
                    fileName = fileEntry.getName();
                    logger.debug("PATH -> " + Util.DB_NORMALIZED_NO + Util.STRING_DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + fileName);
                    instances = new DataSource(Util.DB_NORMALIZED_NO + Util.STRING_DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + fileName).getDataSet();
                    normalizing(instances, fileName);
                }
            }
        } catch(Exception e) {
            logger.error("Error in file: "+fileName, e);
        }
    }
    
    public void normalizing(Instances instances, String dataSetName) {
        try {
            Normalize normalize = new Normalize();
            normalize.setScale(2);
            normalize.setTranslation(-1);
            logger.debug("Scale       -> "+normalize.getScale());
            logger.debug("Translation -> "+normalize.getTranslation());
            normalize.setInputFormat(instances);
            instances = Filter.useFilter(instances, normalize);
            DataSink.write(Util.DB_NORMALIZED_YES + Util.STRING_DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + dataSetName, instances);
        } catch(Exception e) {
            logger.error("Error in file: "+dataSetName, e);
        }
    }
    
    
}
