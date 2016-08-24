package br.com.puc.alves.test;

import br.com.puc.alves.utils.Util;
import java.io.CharArrayReader;
import java.io.Externalizable;
import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.log4j.Logger;
import weka.attributeSelection.BestFirst;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.output.prediction.PlainText;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.Range;
import weka.core.converters.ConverterUtils;

/**
 * Created by alves on 5/1/14.
 */
public class Test implements Cloneable {
    final static Logger logger = Logger.getLogger(Test.class);
    public static void main(String[] args) {
        try {
            DecimalFormat df = new DecimalFormat("#.000");
            
            System.out.println(df.format(3d));
            
            /*String s = "abcdefgh";
            char[] chars = new char[s.length()];
            s.getChars(0, s.length(), chars, 0);
            //CharArrayReader c1 = new CharArrayReader(c);
            
            /*
            File[] files = new File(Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE).listFiles();
            for (File file : files) {
                if (!file.isDirectory() && file.getName().contains("arff")) {
                    String fileName = file.getName();
                    List<String> lines = Util.getCsvToList(Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + fileName);
                    if (lines.get(lines.size()-1).toLowerCase().indexOf("true") >= 0) {
                        logger.info("Data Set: "+fileName);
                    }
                    //instances = new ConverterUtils.DataSource(Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + fileName).getDataSet();
                }
            }

             */       
            /*
            StringBuilder oneLine = new StringBuilder();
                
            Field[] fields = Output.class.getFields();
            for (Field field : fields) {
                oneLine.append(field.getName());
                oneLine.append(Util.CSV_SEPARATOR);
            }                
            logger.info(oneLine.toString());
            
            
            File[] files = new File(Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE).listFiles();
            Arrays.sort(files, (Object f1, Object f2) -> ((File) f1).getName().toLowerCase().compareTo(((File) f2).getName().toLowerCase()));
            Instances instances;
            List<Output> listOutputs = new ArrayList();
            Output output;
            for (File file : files) {
                if (!file.isDirectory() && file.getName().contains("arff")) {
                    String fileName = file.getName();
                    instances = new ConverterUtils.DataSource(Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + fileName).getDataSet();
                    logger.info(instances.size());
                    logger.info(instances.trainCV(30, 0).size());
                    logger.info(instances.testCV(70, 0).size());
                    /*
                    RemovePercentage resample = new RemovePercentage();
                    resample.setPercentage(2/3);
                    //resample.setInputFormat(instances);
                    Instances train = Filter.useFilter(instances, resample);
                    
                    logger.info("TRAIN " + train.size());
                    
                    resample.setInvertSelection(true);
                    resample.setPercentage(1/3);
                    Instances test = Filter.useFilter(instances, resample);
                    
                    logger.info("TEST " + test.size());
                    */
                    /*
                    instances.randomize(new Random(1));
                    int trainSize = (int) Math.round(instances.numInstances() * 2/3);
                    int testSize = instances.numInstances() - trainSize;
                    Instances train = new Instances(instances, 0, trainSize);
                    logger.info("TRAIN " + train.size());
                    Instances test = new Instances(instances, trainSize, testSize);
                    logger.info("TEST " + test.size());
                    
                    
                    break;
                }
            }
                            */
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    
    
    
    private double getEvaluation(Instances instances, Classifier classifier, int folds, int iterations) throws Exception {
        double values = 0D;
        int seed;
        Evaluation evaluation;
        Random random;
        
        PlainText plainText = new PlainText();
        StringBuffer buffer = new StringBuffer();
        plainText.setBuffer(buffer);
        Boolean bool = Boolean.TRUE;
        Range range = new Range("1");
        Object[] object = new Object[3];
        object[0] = plainText;
        object[1] = range;
        object[2] = bool;
                
        for (int i = 0; i < iterations; i ++) {
            evaluation = new Evaluation(instances);
            seed = (int) System.currentTimeMillis();
            random = new Random(seed);
            evaluation.crossValidateModel(classifier, instances, folds, random, object);
            logger.debug(i+ " AUC = " + evaluation.areaUnderROC(Util.DEFECTIVE));
            values = values + evaluation.areaUnderROC(Util.DEFECTIVE);
        }
        double avgResult = iterations * folds;
        avgResult = 1 / avgResult;
        avgResult = avgResult * values;
        logger.debug("AVG Result = "+avgResult);
        double medResult = values / iterations;
        logger.debug("AVG Result = "+medResult);
        return values;
    }
    
    private void getEval() throws Exception {
        Instances instances = ConverterUtils.DataSource.read(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + "metaFeatures-"+Util.algorithmAmount +".csv");
        
        logger.debug("Read instances");
         
        PlainText plainText = new PlainText();
        StringBuffer buffer = new StringBuffer();
        plainText.setBuffer(buffer);
        Boolean bool = Boolean.TRUE;
        Range range = new Range("1");
        Object[] object = new Object[3];
        object[0] = plainText;
        object[1] = range;
        object[2] = bool;
        
        
        while (instances.numAttributes() > 24) {
            if ((instances.numAttributes()-1) <= 23) {
                instances.deleteAttributeAt(instances.numAttributes()-2);
            } else {
                instances.deleteAttributeAt(instances.numAttributes()-1);
            }
        }
        
        String clsIndex = "last";
        if (clsIndex.length() == 0) {
            clsIndex = "last";
        }
        if (clsIndex.equals("first")) {
            instances.setClassIndex(0);
        } else if (clsIndex.equals("last")) {
            instances.setClassIndex(instances.numAttributes() - 1);
        } else {
            instances.setClassIndex(Integer.parseInt(clsIndex) - 1);
        }
        logger.debug("Set classindex : " + instances.classIndex());
        
        IBk ibk = new IBk(1);
        
        Evaluation evaluation = new Evaluation(instances);
        evaluation.crossValidateModel(ibk, instances, 71, new Random(), object);
        
        String a[] = ((PlainText)object[0]).getBuffer().toString().split("\n");
        System.out.print(a[0]);
        System.out.print(a[1]); 
    }
}
