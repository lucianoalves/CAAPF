package br.com.puc.alves.sac;

import br.com.puc.alves.base.*;
import br.com.puc.alves.utils.Util;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.meta.AdaBoostM1;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

/**
 * Created by alves on 4/13/14.
 */
public class SacMetaBase {
    final static Logger logger = Logger.getLogger(SacMetaBase.class);
    //public static String pathData = "src/main/resources/data/";
    //public static String pathCSV = "src/main/resources/csv/";

    private List<MetaFeatures> map;

    public static void main(String[] args) {
        SacMetaBase main = new SacMetaBase();
        main.init();
    }

    public void init() {
        String fileName = "";
        try {
            //setInstances();
            map = new ArrayList<>();
            //final File files = ;
            File[] files = new File(SacUtils.SAC + SacUtils.ARFF).listFiles();
            Arrays.sort(files, (Object f1, Object f2) -> ((File) f1).getName().toLowerCase().compareTo(((File) f2).getName().toLowerCase()));
            //Arrays.sort(files, Collections.reverseOrder());
            Instances instances;
            MetaFeatures metaFeatures;
            for (final File file : files) {
                if (!file.isDirectory() && file.getName().contains("arff")) {
                    fileName = file.getName();
                    logger.debug("Dataset: "+fileName);
                    metaFeatures = new MetaFeatures();
                    metaFeatures.setDataSetName(fileName.replace(".arff", ""));
                    instances = new DataSource(SacUtils.SAC + SacUtils.ARFF + fileName).getDataSet();
                    if (instances.classIndex() == -1) {
                        instances.setClassIndex(instances.numAttributes() -1);
                    }                                        
                    getClassifier(instances, metaFeatures);
                    map.add(metaFeatures);
                }
            }
            writeToCSV(map);
            
        } catch(Exception e) {
            logger.error("Error in file: "+fileName, e);
        }
    }
/*
    private void setInstances() throws Exception {
        if (!Util.SEARCH_TYPE.equals(Util.SEARCH_NONE)) {
            File[] files = new File(Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_NONE).listFiles();
            for (final File file : files) {
                String dataSetName = file.getName();
                if (!new File(Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + dataSetName).exists()) {
                    Instances instances = new DataSource(Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_NONE + "/" + dataSetName).getDataSet();
                    if (Util.SEARCH_TYPE.equals(Util.SEARCH_FS)) {
                        AttributeSelection filter = new AttributeSelection();
                        CfsSubsetEval eval = new CfsSubsetEval();
                        BestFirst search = new BestFirst();
                        filter.setEvaluator(eval);
                        filter.setSearch(search);
                        filter.setInputFormat(instances);
                        Instances newData = Filter.useFilter(instances, filter);
                        ConverterUtils.DataSink.write(Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + dataSetName, newData);
                    }
                    if (Util.SEARCH_TYPE.equals(Util.SEARCH_BE)) {
                        AttributeSelection filter = new AttributeSelection();
                        CfsSubsetEval eval = new CfsSubsetEval();
                        GreedyStepwise search = new GreedyStepwise();
                        search.setSearchBackwards(true);
                        filter.setEvaluator(eval);
                        filter.setSearch(search);
                        filter.setInputFormat(instances);
                        Instances newData = Filter.useFilter(instances, filter);
                        ConverterUtils.DataSink.write(Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + dataSetName, newData);
                    }
                }
            }
        }
    }
*/    
    /*
    private void generateClassifier(Instances instances, MetaFeatures metaFeatures) {
        NaiveBayes naiveBayes = new NaiveBayes();
        RandomForest randomForest = new RandomForest();
        J48 j48 = new J48();
        IBk iBk = new IBk();
        iBk.setKNN(3);
        SMO smo = new SMO();
        MultilayerPerceptron multilayerPerceptron = new MultilayerPerceptron();
        AdaBoostM1 adaBoostM1 = new AdaBoostM1();
        OneR oneR = new OneR();
        JRip jRip = new JRip();
        
        List<ClassifierRanking> listClassifiers = new ArrayList<>();
        
        try {
            listClassifiers.add(getEvaluation(instances, naiveBayes, Util.numFolds, Util.numIterations, ClassifierRanking.NB));
            listClassifiers.add(getEvaluation(instances, randomForest, Util.numFolds, Util.numIterations, ClassifierRanking.RF));
            listClassifiers.add(getEvaluation(instances, j48, Util.numFolds, Util.numIterations, ClassifierRanking.J48));
            listClassifiers.add(getEvaluation(instances, iBk, Util.numFolds, Util.numIterations, ClassifierRanking.IBK));
            listClassifiers.add(getEvaluation(instances, smo, Util.numFolds, Util.numIterations, ClassifierRanking.SMO));
            listClassifiers.add(getEvaluation(instances, multilayerPerceptron, Util.numFolds, Util.numIterations, ClassifierRanking.MLP));
            listClassifiers.add(getEvaluation(instances, adaBoostM1, Util.numFolds, Util.numIterations, ClassifierRanking.ABM));
            //listClassifiers.add(getEvaluation(instances, oneR, Util.numFolds, Util.numIterations, ClassifierRanking.ONE_R));
            //listClassifiers.add(getEvaluation(instances, jRip, Util.numFolds, Util.numIterations, ClassifierRanking.RIPPER));
        } catch (Exception e) {
            logger.error("Exception is", e);
        }
        
        ObjectOutputStream objectOutputStream = null;
        FileOutputStream outputStream;
        try{
            outputStream = new FileOutputStream(Util.BASE_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE +"/"+ metaFeatures.dataSetName + ".classifiers", true);
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(listClassifiers);
        } catch (Exception e) {
                logger.error("Exception is", e);
        } finally {
                if(objectOutputStream  != null){
                    try {
                        objectOutputStream.close();
                    } catch (IOException ex) {
                        logger.error("Exception is", ex);
                    }
                 } 
        }
        
    }
    */
    
    @SuppressWarnings("UnusedAssignment")
    private Algorithm getEvaluation(String dataSetName, Instances instances, MLAlgorithmEnum algorithmEnum, int folds, int iterations) throws Exception {
        double auc = 0D;
        double pd = 0D;
        double pf = 0D;
        double balance = 0D;
        
        List<Evaluation> lstEvaluation = new ArrayList<>(iterations);
        Evaluation evaluation;
        Random random;
        
        SacXGBostMain xgbm = null;
        Classifier classifierBase = null;
        switch(algorithmEnum) {
            case AB : classifierBase = new AdaBoostM1();
            case C45 : classifierBase = new J48();
            case K_NN : classifierBase = new IBk(3);
            case MLP : classifierBase = new MultilayerPerceptron();
            case NB : classifierBase = new NaiveBayes();
            case RND_FOR : classifierBase = new RandomForest();
            case SVM : classifierBase = new SMO();
            case XGB : xgbm = new SacXGBostMain();
            
            default: if (classifierBase == null) {
                classifierBase = new RandomForest();
            }
        }
        
        double[] metrics = null;
        for (int i = 1; i <= iterations; i ++) {
            if (xgbm != null) {
                metrics = xgbm.getXGBoostEvaluating(dataSetName, i, folds);
                auc = auc + metrics[0];
                balance = balance + metrics[1];
            } else {
                evaluation = new Evaluation(instances);
                random = new Random(i);
                evaluation.crossValidateModel(classifierBase, instances, folds, random);
                auc = auc + evaluation.areaUnderROC(Util.DEFECTIVE);
                pd = pd + Util.getPD(evaluation);
                pf = pf + Util.getPF(evaluation);
                lstEvaluation.add(evaluation);
            }
        }
        auc = auc / iterations;
        logger.debug(" AUC = " + auc);
        if (xgbm == null) {
            balance = Util.getBalance(pd / iterations, pf / iterations);
        } else {
            balance = balance / iterations;
        }
        logger.debug(" Balance = " + balance);
        Algorithm algorithm = new Algorithm(algorithmEnum, auc, balance, lstEvaluation);
        
        return algorithm;
    }
    
    private void saveClassifier(String dataSetName, Algorithm algorithm) {
        ObjectOutputStream objectOutputStream = null;
        FileOutputStream outputStream;
        try{
            outputStream = new FileOutputStream(SacUtils.SAC + SacUtils.CLASSIFIERS + dataSetName + "-" + algorithm.getName() + ".classifiers", true);
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(algorithm);
        } catch (Exception e) {
                logger.error("Exception is", e);
        } finally {
                if(objectOutputStream  != null){
                    try {
                        objectOutputStream.close();
                    } catch (IOException ex) {
                        logger.error("Exception is", ex);
                    }
                 } 
        }
    }
    
    private void getClassifier(Instances instances, MetaFeatures metaFeatures) {
        ObjectInputStream objectinputstream = null;
        FileInputStream streamIn = null;
        try {
            List<Algorithm> listClassifiers = new ArrayList();
            Algorithm algorithm;
            
            for (MLAlgorithmEnum algorithmEnum : MLAlgorithmEnum.values()) {
                File file = new File(SacUtils.SAC + SacUtils.CLASSIFIERS +"/"+ metaFeatures.getDataSetName() + "-" + algorithmEnum + ".classifiers");
                if (!file.exists()) {
                    algorithm = getEvaluation(metaFeatures.getDataSetName(), instances, algorithmEnum, Util.numFolds, Util.numIterations);
                    saveClassifier(metaFeatures.getDataSetName(), algorithm);
                } else {
                    streamIn = new FileInputStream(file);
                    objectinputstream = new ObjectInputStream(streamIn);
                    algorithm = (Algorithm) objectinputstream.readObject();
                }
                listClassifiers.add(algorithm);
            }
                                    
            metaFeatures.setClassifiers(listClassifiers);
        } catch (Exception e) {
            logger.error("Exception is", e);
        } finally {
            try {
                if (streamIn != null) streamIn.close();
                if (objectinputstream != null) objectinputstream.close();
            } catch (Exception ex) {
                logger.error("Exception is", ex);
            }
        }
    }
    
    private void writeToCSV(List<MetaFeatures> metaFeaturesList) {
        
        BufferedWriter bwAUC;
        BufferedWriter bwBalance;
        try {
            bwAUC = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(SacUtils.SAC + "metaFeatures-"+Util.MEASURE_AUC +"-"+ Util.algorithmAmount + ".csv"), "UTF-8"));
            bwBalance = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(SacUtils.SAC + "metaFeatures-"+Util.MEASURE_BALANCE +"-"+ Util.algorithmAmount + ".csv"), "UTF-8"));
            StringBuilder oneLine = new StringBuilder();
                      
            for (MLAlgorithmEnum e : MLAlgorithmEnum.values()) {
                oneLine.append(e);
                oneLine.append(Util.CSV_SEPARATOR);
            }
            
            bwAUC.write(oneLine.toString());
            bwAUC.newLine();
            
            bwBalance.write(oneLine.toString());
            bwBalance.newLine();
            
            for (MetaFeatures metaFeatures : metaFeaturesList)
            {
                StringBuilder oneLineAUC = new StringBuilder();
                StringBuilder oneLineBalance = new StringBuilder();
                
                metaFeatures.getClassifiers().stream().map((algorithm) -> {
                    oneLineAUC.append(algorithm.getAreaROC());
                    return algorithm;
                }).map((algorithm) -> {
                    oneLineAUC.append(Util.CSV_SEPARATOR);
                    oneLineBalance.append(algorithm.getBalance());
                    return algorithm;
                }).forEach((_item) -> {
                    oneLineBalance.append(Util.CSV_SEPARATOR);
                });
                
                bwAUC.write(oneLineAUC.toString());
                bwAUC.newLine();         
                
                bwBalance.write(oneLineBalance.toString());
                bwBalance.newLine();
            }
            bwAUC.flush();
            bwBalance.flush();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(SacMetaBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}