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
            Instances instances;
            MetaFeatures metaFeatures;
            for (final File file : files) {
                if (!file.isDirectory() && file.getName().contains("arff")) {
                    fileName = file.getName();
                    logger.debug("Dataset: "+fileName);
                    metaFeatures = new MetaFeatures();
                    metaFeatures.setDataSetName(fileName);
                    instances = new DataSource(Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + fileName).getDataSet();
                    if (instances.classIndex() == -1) {
                        instances.setClassIndex(instances.numAttributes() -1);
                    }
                    getWekaMeasure(instances, metaFeatures);
                    getDColMeasures(metaFeatures);
                    getStatLogFeatures(instances, metaFeatures);
                    getClassifier(instances, metaFeatures);
                    logger.debug("Classifier-AUC: "+metaFeatures.getClassifierAUC());
                    logger.debug("Classifier-Balance: "+metaFeatures.getClassifierBalance());
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
    
    private void generateClassifier(Instances instances, MetaFeatures metaFeatures) {
        NaiveBayes naiveBayes = new NaiveBayes();
        RandomForest randomForest = new RandomForest();
        J48 j48 = new J48();
        IBk iBk = new IBk();
        iBk.setKNN(3);
        SMO smo = new SMO();
        MultilayerPerceptron multilayerPerceptron = new MultilayerPerceptron();
        AdaBoostM1 adaBoostM1 = new AdaBoostM1();
        
        List<ClassifierRanking> listClassifiers = new ArrayList<>();
        
        try {
            listClassifiers.add(getEvaluation(instances, naiveBayes, Util.numFolds, Util.numIterations, ClassifierRanking.NB));
            listClassifiers.add(getEvaluation(instances, randomForest, Util.numFolds, Util.numIterations, ClassifierRanking.RF));
            listClassifiers.add(getEvaluation(instances, j48, Util.numFolds, Util.numIterations, ClassifierRanking.J48));
            listClassifiers.add(getEvaluation(instances, iBk, Util.numFolds, Util.numIterations, ClassifierRanking.IBK));
            listClassifiers.add(getEvaluation(instances, smo, Util.numFolds, Util.numIterations, ClassifierRanking.SMO));
            listClassifiers.add(getEvaluation(instances, multilayerPerceptron, Util.numFolds, Util.numIterations, ClassifierRanking.MLP));
            listClassifiers.add(getEvaluation(instances, adaBoostM1, Util.numFolds, Util.numIterations, ClassifierRanking.ABM));
            //Collections.sort(listClassifiers);
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
    
    private ClassifierRanking getEvaluation(Instances instances, Classifier classifierBase, int folds, int iterations, String classifierName) throws Exception {
        double auc = 0D;
        double pd = 0D;
        double pf = 0D;
        List<Evaluation> lstEvaluation = new ArrayList<>(iterations);
        Evaluation evaluation;
        Random random;
                
        for (int i = 1; i <= iterations; i ++) {
            evaluation = new Evaluation(instances);
            random = new Random(i);
            evaluation.crossValidateModel(classifierBase, instances, folds, random);
            auc = auc + evaluation.areaUnderROC(Util.DEFECTIVE);
            pd = pd + Util.getPD(evaluation);
            pf = pf + Util.getPF(evaluation);
            lstEvaluation.add(evaluation);
        }
        auc = auc / iterations;
        logger.debug(" AUC = " + auc);
        double balance = Util.getBalance(pd / iterations, pf / iterations);
        ClassifierRanking classifierRanking = new ClassifierRanking(classifierName, auc, balance, 0D, 0D, lstEvaluation);
        return classifierRanking;
    }
    
    private void getClassifier(Instances instances, MetaFeatures metaFeatures) {
        ObjectInputStream objectinputstream = null;
        FileInputStream streamIn = null;
        try {
            File file = new File(Util.BASE_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE +"/"+ metaFeatures.getDataSetName() + ".classifiers");
            if (!file.exists()) {
                generateClassifier(instances, metaFeatures);
            }
            streamIn = new FileInputStream(Util.BASE_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE +"/"+ metaFeatures.getDataSetName() + ".classifiers");
            objectinputstream = new ObjectInputStream(streamIn);
            List<ClassifierRanking> listClassifiers = (List<ClassifierRanking>) objectinputstream.readObject();
            /*
            for (ClassifierRanking cr : listClassifiers) {
                if (!algorithms.contains(cr.getName())) {
                    listClassifiers.remove(cr);
                    break;
                }
            }
            */
            Util.IS_AUC = true;
            Collections.sort(listClassifiers);
            setRankingAUC(listClassifiers);
            metaFeatures.setClassifierAUC(listClassifiers.get(0));
            Util.IS_AUC = false;
            Collections.sort(listClassifiers);
            setRankingBalance(listClassifiers);
            metaFeatures.setClassifierBalance(listClassifiers.get(0));
            metaFeatures.setRankings(listClassifiers);
        } catch (IOException | ClassNotFoundException e) {
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
    
    private void setRankingAUC(List<ClassifierRanking> listClassifiers) {
        for (int i = 0; i < listClassifiers.size(); i++) {
            if (i == (listClassifiers.size() - 1)) {
                listClassifiers.get(i).setRankingAUC(i+1);
            } else if (listClassifiers.get(i).getAreaROC() > listClassifiers.get(i+1).getAreaROC()) {
                listClassifiers.get(i).setRankingAUC(i+1);
            } else {
                int n = i;
                int size = 1;
                while (listClassifiers.get(n).getAreaROC() < listClassifiers.get(n+1).getAreaROC()) {
                    size ++;
                }
                double ranking = getRanking(i+1, size);
                for (int j = i; j < (i + size); j++) {
                    listClassifiers.get(j).setRankingAUC(ranking);
                }
                i = i + size;
            }
        }
        
    }
    
    private void setRankingBalance(List<ClassifierRanking> listClassifiers) {
        for (int i = 0; i < listClassifiers.size(); i++) {
            if (i == (listClassifiers.size() - 1)) {
                listClassifiers.get(i).setRankingBalance(i+1);
            } else if (listClassifiers.get(i).getBalance() > listClassifiers.get(i+1).getBalance()) {
                listClassifiers.get(i).setRankingBalance(i+1);
            } else {
                int n = i;
                int size = 1;
                while (listClassifiers.get(n).getBalance() < listClassifiers.get(n+1).getBalance()) {
                    size ++;
                }
                double ranking = getRanking(i+1, size);
                for (int j = i; j < (i + size); j++) {
                    listClassifiers.get(j).setRankingBalance(ranking);
                }
                i = i + size;
            }
        }
        
    }
    private double getRanking(int pos, int size) {
        double ranking = 0;
        for (int i = pos; i < (pos + size); i++) {
            ranking = ranking + i;
        }
        return ranking / size;
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

    private double[] getMeasures(List<ClassifierRanking> rankings, boolean isAUC) {
        double[] measures = new double[Util.algorithmAmount*2];
        ClassifierRanking classifierRanking;
        double ranking;
        double measure;
        for (ClassifierRanking ranking1 : rankings) {
            classifierRanking = ranking1;
            ranking = classifierRanking.getRankingBalance();
            measure = classifierRanking.getBalance();
            if (isAUC) {
                ranking = classifierRanking.getRankingAUC();
                measure = classifierRanking.getAreaROC();
            }
            if (classifierRanking.getName().equals(ClassifierRanking.NB)) {
                measures[0] = ranking;
                measures[1] = measure;
            }
            if (classifierRanking.getName().equals(ClassifierRanking.RF) && Util.algorithms.contains(ClassifierRanking.RF)) {
                measures[2] = ranking;
                measures[3] = measure;
            }
            if (classifierRanking.getName().equals(ClassifierRanking.J48)) {
                measures[4] = ranking;
                measures[5] = measure;
            }
            if (classifierRanking.getName().equals(ClassifierRanking.IBK)) {
                measures[6] = ranking;
                measures[7] = measure;
            }
            if (classifierRanking.getName().equals(ClassifierRanking.SMO)) {
                measures[8] = ranking;
                measures[9] = measure;
            }
            if (classifierRanking.getName().equals(ClassifierRanking.MLP)) {
                measures[10] = ranking;
                measures[11] = measure;
            }
            if (classifierRanking.getName().equals(ClassifierRanking.ABM)) {
                measures[12] = ranking;
                measures[13] = measure;
            }
        }
        return measures;
    }
    
    private void writeToCSV(List<MetaFeatures> metaFeaturesList)
    {
        try
        {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE +"/"+ "metaFeatures-"+Util.algorithmAmount +".csv"), "UTF-8"))) {
                StringBuffer oneLine = new StringBuffer();
                
                Field[] fields = MetaFeatures.class.getFields();
                for (Field field : fields) {
                    if (!field.getName().equals("classifierAUC") && !field.getName().equals("classifierBalance") && !field.getName().equals("rankings")) {
                        oneLine.append(field.getName());
                        oneLine.append(Util.CSV_SEPARATOR);
                    }
                }
                
                oneLine.append("NB-RANK-AUC");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("NB-AUC");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("RF-RANK-AUC");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("RF-AUC");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("J48-RANK-AUC");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("J48-AUC");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("IBK-RANK-AUC");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("IBK-AUC");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("SMO-RANK-AUC");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("SMO-AUC");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("MLP-RANK-AUC");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("MLP-AUC");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("ABM-RANK-AUC");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("ABM-AUC");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("NB-RANK-Balance");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("NB-Balance");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("RF-RANK-Balance");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("RF-Balance");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("J48-RANK-Balance");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("J48-Balance");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("IBK-RANK-Balance");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("IBK-Balance");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("SMO-RANK-Balance");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("SMO-Balance");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("MLP-RANK-Balance");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("MLP-Balance");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("ABM-RANK-Balance");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("ABM-Balance");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("classifier-AUC");
                oneLine.append(Util.CSV_SEPARATOR);
                oneLine.append("classifier-Balance");
                
                bw.write(oneLine.toString());
                bw.newLine();
                
                double[] measuresAUC;
                double[] measuresBalance;
                for (MetaFeatures metaFeatures : metaFeaturesList)
                {
                    measuresAUC = getMeasures(metaFeatures.getRankings(), true);
                    measuresBalance = getMeasures(metaFeatures.getRankings(), false);
                    
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
                    
                    for (double measure : measuresAUC) {
                        oneLine.append(measure);
                        oneLine.append(Util.CSV_SEPARATOR);
                    }
                    
                    for (double measure : measuresBalance) {
                        oneLine.append(measure);
                        oneLine.append(Util.CSV_SEPARATOR);
                    }
                    
                    oneLine.append(metaFeatures.getClassifierAUC());
                    oneLine.append(Util.CSV_SEPARATOR);
                    oneLine.append(metaFeatures.getClassifierBalance());
                    bw.write(oneLine.toString());
                    bw.newLine();
                }
                bw.flush();
            }
        }
        catch (UnsupportedEncodingException e) {}
        catch (FileNotFoundException e){}
        catch (IOException e){}
    }
}