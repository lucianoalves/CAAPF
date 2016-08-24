package br.com.puc.alves.base;

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
public class MetaBase {
    final static Logger logger = Logger.getLogger(MetaBase.class);
    //public static String pathData = "src/main/resources/data/";
    //public static String pathCSV = "src/main/resources/csv/";

    private List<MetaFeatures> map;

    public static void main(String[] args) {
        MetaBase main = new MetaBase();
        main.init();
    }

    public void init() {
        String fileName = "";
        try {
            setInstances();
            map = new ArrayList<>();
            //final File files = ;
            File[] files = new File(Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE).listFiles();
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
                    instances = new DataSource(Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + fileName).getDataSet();
                    if (instances.classIndex() == -1) {
                        instances.setClassIndex(instances.numAttributes() -1);
                    }
                    getWekaMeasure(instances, metaFeatures);
                    getDColMeasures(metaFeatures);
                    getStatLogFeatures(instances, metaFeatures);
                    getClassifier(instances, metaFeatures);
                    map.add(metaFeatures);
                }
            }
            writeToCSV(map);
            
        } catch(Exception e) {
            logger.error("Error in file: "+fileName, e);
        }
    }

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
        
        XGBostMain xgbm = null;
        Classifier classifierBase = null;
        switch(algorithmEnum) {
            case AB : classifierBase = new AdaBoostM1();
            case C45 : classifierBase = new J48();
            case K_NN : classifierBase = new IBk(3);
            case MLP : classifierBase = new MultilayerPerceptron();
            case NB : classifierBase = new NaiveBayes();
            case RND_FOR : classifierBase = new RandomForest();
            case SVM : classifierBase = new SMO();
            case XGB : xgbm = new XGBostMain();
            
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
            outputStream = new FileOutputStream(Util.BASE_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE +"/"+ dataSetName + "-" + algorithm.getName() + ".classifiers", true);
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
                File file = new File(Util.BASE_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE +"/"+ metaFeatures.getDataSetName() + "-" + algorithmEnum + ".classifiers");
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
        
    private void getStatLogFeatures(Instances instances, MetaFeatures metaFeatures) throws Exception {
        StatLogFeatures statLogFeatures = new StatLogFeatures(instances, metaFeatures);
    }

    private void getWekaMeasure(Instances instances, MetaFeatures metaFeatures) throws Exception {
        metaFeatures.setExample(instances.numInstances());
        metaFeatures.setAttribute(instances.numAttributes()-1);
        metaFeatures.setClazz(instances.numClasses());
    }

    private void getDColMeasures(MetaFeatures metaFeatures) {
        String line = getLastLine(metaFeatures.getDataSetName());
        String[] measures = new String[14];
        int n = 0;
        for (String s : line.split("  ")) {
            if (!s.equals("") && !s.contains(metaFeatures.getDataSetName())) {
                measures[n] = s;
                n++;
            }
        }
                
        metaFeatures.setF1(new Double(measures[0]));
        metaFeatures.setF1v(new Double(measures[1]));
        metaFeatures.setF2(new Double(measures[2]));
        metaFeatures.setF3(new Double(measures[3]));
        metaFeatures.setF4(new Double(measures[4]));
        metaFeatures.setL1(new Double(measures[5]));
        metaFeatures.setL2(new Double(measures[6]));
        metaFeatures.setL3(new Double(measures[7]));
        metaFeatures.setN1(new Double(measures[8]));
        metaFeatures.setN2((measures[9].trim().equals("inf")) ? -100 : new Double(measures[9]));
        metaFeatures.setN3(new Double(measures[10]));
        metaFeatures.setN4(new Double(measures[11]));
        metaFeatures.setT1(new Double(measures[12]));
        metaFeatures.setT2(new Double(measures[13]));
    }

    private String getLastLine(String fileName) {
        BufferedReader bufferedReader = null;
        String lastLine = null;
        try {
            File file = new File(Util.DCOL + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + fileName + ".txt");
            if (!file.exists()) {
                generateDColMeasures(fileName);
            }
            bufferedReader = new BufferedReader(new FileReader(Util.DCOL + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + fileName + ".txt"));
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                lastLine = currentLine;
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Exception is", e);
        } finally {
            try {
                if (bufferedReader != null) bufferedReader.close();
            } catch (Exception ex) {
                logger.error("Exception is", ex);
            }
        }
        return lastLine;
    }

    @SuppressWarnings("SleepWhileInLoop")
    private void generateDColMeasures(String fileName) throws IOException, InterruptedException {
        logger.debug("Generating DColMeasures of data set: "+fileName);
        Runtime runtime = Runtime.getRuntime();
        runtime.exec("../../DCoL-v1.1/Source/dcol -i " + Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + fileName + " -o " + Util.DCOL + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + fileName + " -A");
        boolean exist = false;
        while (!exist) {
            if (new File(Util.DCOL + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + fileName + ".txt").exists()) {
                exist = true;
            } else {
                Thread.sleep(10000);
            }
        }
        runtime.exec("rm -rf "+Util.DCOL + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + fileName+".log");
    }   
    
    private void writeToCSV(List<MetaFeatures> metaFeaturesList) {
        
        BufferedWriter bwAUC;
        BufferedWriter bwBalance;
        try {
            bwAUC = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE +"/"+ "metaFeatures-"+Util.MEASURE_AUC +"-"+ Util.algorithmAmount + ".csv"), "UTF-8"));
            bwBalance = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE +"/"+ "metaFeatures-"+Util.MEASURE_BALANCE +"-"+ Util.algorithmAmount + ".csv"), "UTF-8"));
            StringBuffer oneLine = new StringBuffer();
            
            Field[] fields = MetaFeatures.class.getFields();
            for (Field field : fields) {
                if (!field.getName().equals("classifiers")) {
                    oneLine.append(field.getName());
                    oneLine.append(Util.CSV_SEPARATOR);
                }
            }
            
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
                
                bwAUC.write(oneLine.toString()+oneLineAUC.toString());
                bwAUC.newLine();         
                
                bwBalance.write(oneLine.toString()+oneLineBalance.toString());
                bwBalance.newLine();
            }
            bwAUC.flush();
            bwBalance.flush();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(MetaBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}