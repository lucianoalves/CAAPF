/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.meta;

import br.com.puc.alves.utils.Util;
import br.com.puc.alves.base.ClassifierRanking;
import br.com.puc.alves.utils.Statistics;
import static br.com.puc.alves.utils.Util.CSV_SEPARATOR;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AddClassification;

/**
 *
 * @author ssad
 */
public class RunningAllAlgorithms {
    final static Logger logger = Logger.getLogger(RunningAllAlgorithms.class);
    public static void main(String[] args) {
        try {
            RunningAllAlgorithms runningAll = new RunningAllAlgorithms();
            
            Classifier classifier = new NaiveBayes();
            runningAll.init(classifier, ClassifierRanking.NB);
            classifier = new RandomForest();
            runningAll.init(classifier, ClassifierRanking.RF);
            classifier = new J48();
            runningAll.init(classifier, ClassifierRanking.J48);
            classifier = new IBk();
            runningAll.init(classifier, ClassifierRanking.IBK);
            classifier = new SMO();
            runningAll.init(classifier, ClassifierRanking.SMO);
            classifier = new MultilayerPerceptron();
            runningAll.init(classifier, ClassifierRanking.MLP);
            classifier = new AdaBoostM1();
            runningAll.init(classifier, ClassifierRanking.ABM);
            
            runningAll.process();
        } catch(Exception e) {
            logger.error("Exception is ", e);
        }
    }
    
    private void init(Classifier classifier, String algorithmName) throws Exception {
        Instances instances = ConverterUtils.DataSource.read(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE + "/metaFeaturesWithBestAlgorithm-"+Util.MEASURE_TYPE+"-"+Util.algorithmAmount+".csv");
        Instances trainInstances = Util.getTrainInstancesBestAlgorithm(instances);
        
        //Remove attributes that were the measure of the execution algorithms
        while (instances.numAttributes() > 24) {
            instances.deleteAttributeAt(instances.numAttributes()-2);
        }
        
        //Select attribute by category (STATLOG/COMPLEXITY/NONE)
        Util.getInstancesFiltered(instances);
        
        String clsIndex = "last";
        if (clsIndex.length() == 0) {
            clsIndex = "last";
        }
        switch (clsIndex) {
            case "first":
                instances.setClassIndex(0);
                break;
            case "last":
                instances.setClassIndex(instances.numAttributes() - 1);
                break;
            default:
                instances.setClassIndex(Integer.parseInt(clsIndex) - 1);
                break;
        }
        logger.debug("Set classindex : " + instances.classIndex());
        // classifier
        // cls.setNumTrees(30);
        // other options
        int seed = 10;
        // int folds = Integer.parseInt(Utils.getOption("x", args));
        int folds = instances.size();
                
        // perform cross-validation and add predictions
        Instances predictedData = null;
        //Evaluation eval = new Evaluation(randData);
        for (int n = 0; n < folds; n++) {
            logger.debug("Fold #" + (n + 1)
                    + "\n=============\nGetting training data...");
            Instances train = (trainInstances == null) ? instances.trainCV(folds, n) : trainInstances;
            logger.debug("Getting testing data...");
            Instances test = instances.testCV(folds, n);
            // the above code is used by the StratifiedRemoveFolds filter, the
            // code below by the Explorer/Experimenter:
            // Instances train = randData.trainCV(folds, n, rand);
            // build and evaluate classifier
            //Classifier clsCopy = AbstractClassifier.makeCopy(classifier);
            //logger.debug("Building classifier...");
            //clsCopy.buildClassifier(train);
            //logger.debug("Evaluating model...");
            //eval.evaluateModel(clsCopy, test);
            // add predictions
            AddClassification filter = new AddClassification();
            logger.debug("Creating filter...");
            filter.setClassifier(classifier);
            filter.setOutputClassification(true);
            filter.setOutputDistribution(true);
            filter.setOutputErrorFlag(true);
            filter.setInputFormat(train);
            logger.debug("Training classifier...");
            Filter.useFilter(train, filter); // trains the classifier
            logger.debug("Performing predictions on testing data...");
            Instances pred = Filter.useFilter(test, filter); // perform
            // predictions
            // on test set
            if (predictedData == null) {
                predictedData = new Instances(pred, 0);
            }
            for (int j = 0; j < pred.numInstances(); j++) {
                predictedData.add(pred.instance(j));
            }
        }
        // output evaluation
        logger.debug("");
        logger.debug("=== Setup ===");
        logger.debug("Classifier: " + classifier.getClass().getName() + " ");

        logger.debug("Dataset: " + instances.relationName());
        logger.debug("Folds: " + folds);
        logger.debug("Seed: " + seed);
        logger.debug("");
        //logger.debug(eval.toSummaryString("=== " + folds
        //        + "-fold Cross-validation ===", true));
        // output "enriched" dataset
        // DataSink.write(Utils.getOption("o", args), predictedData);
        ConverterUtils.DataSink.write(Util.BEST_ALGORITHM_PREDICT + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + Util.META_BASE_TYPE + "/" + algorithmName + "-" + Util.algorithmAmount + "-" + Util.MEASURE_TYPE + ".arff", predictedData);
    }
    
    public void process() throws Exception {
        Instances instancesNB = ConverterUtils.DataSource.read(Util.BEST_ALGORITHM_PREDICT + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + Util.META_BASE_TYPE + "/" + ClassifierRanking.NB + "-" + Util.algorithmAmount + "-" + Util.MEASURE_TYPE + ".arff");
        Instances instancesRF = ConverterUtils.DataSource.read(Util.BEST_ALGORITHM_PREDICT + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + Util.META_BASE_TYPE + "/" + ClassifierRanking.RF + "-" + Util.algorithmAmount + "-" + Util.MEASURE_TYPE + ".arff");        
        Instances instancesJ48 = ConverterUtils.DataSource.read(Util.BEST_ALGORITHM_PREDICT + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + Util.META_BASE_TYPE + "/" + ClassifierRanking.J48 + "-" + Util.algorithmAmount + "-" + Util.MEASURE_TYPE + ".arff");
        Instances instancesIBK = ConverterUtils.DataSource.read(Util.BEST_ALGORITHM_PREDICT + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + Util.META_BASE_TYPE + "/" + ClassifierRanking.IBK + "-" + Util.algorithmAmount + "-" + Util.MEASURE_TYPE + ".arff");
        Instances instancesSMO = ConverterUtils.DataSource.read(Util.BEST_ALGORITHM_PREDICT + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + Util.META_BASE_TYPE + "/" + ClassifierRanking.SMO + "-" + Util.algorithmAmount + "-" + Util.MEASURE_TYPE + ".arff");
        Instances instancesMLP = ConverterUtils.DataSource.read(Util.BEST_ALGORITHM_PREDICT + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + Util.META_BASE_TYPE + "/" + ClassifierRanking.MLP + "-" + Util.algorithmAmount + "-" + Util.MEASURE_TYPE + ".arff");
        Instances instancesABM = ConverterUtils.DataSource.read(Util.BEST_ALGORITHM_PREDICT + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + Util.META_BASE_TYPE + "/" + ClassifierRanking.ABM + "-" + Util.algorithmAmount + "-" + Util.MEASURE_TYPE + ".arff");
        List<Bean> list = new ArrayList<>();
        List<String> lines = Util.getCsvToList(Util.getFilePath(Util.RANKING_RESULT, "kNN", 3, Util.META_BASE_NONE));
        Bean bean;
        for (int n = 0; n < instancesNB.numInstances(); n++) {
            //String dataSetName = instance.stringValue(0);
            bean = new Bean();
            bean.setDataSetName(lines.get(n+1).split(Util.CSV_SEPARATOR)[0]);
            bean.setClazz(instancesNB.get(n).stringValue(Util.getPredictorPosition(0)));
            bean.setNb(instancesNB.get(n).stringValue(Util.getPredictorPosition(1)));
            bean.setRf(getAlgorithmPredicted(instancesRF, n));
            bean.setJ48(getAlgorithmPredicted(instancesJ48, n));
            bean.setIbk(getAlgorithmPredicted(instancesIBK, n));
            bean.setSmo(getAlgorithmPredicted(instancesSMO, n));
            bean.setMlp(getAlgorithmPredicted(instancesMLP, n));
            bean.setAbm(getAlgorithmPredicted(instancesABM, n));
            //bean.setPredicted();
            list.add(bean);
        }
                
        List<double[]> results = new ArrayList<>();
        List<String> dataSets = new ArrayList<>();
        double[] rankTotal = new double[Util.algorithmAmount+1];
        for (String l : lines) {
            String[] r = l.split(Util.CSV_SEPARATOR);
            if (r[0].equals("DataSetName") || r[0].trim().equals("")) continue;
            double[] rank = getRank(r);
            setRankByBean(list, r[0], rank);
            setWidth(rank);
            increaseRank(rank);
            addTotalRank(rank, rankTotal);
            results.add(rank);
            dataSets.add(r[0]);
        }
        writeToCSV(dataSets, results, rankTotal);
        generateCsvToGraph(list);
        /*
        for (Bean b : list) {
            logger.debug(b);
        }
        */
    }
    
    private void addTotalRank(double[] rank, double[] rankTotal) {
        for (int i = 0; i < Util.algorithmAmount+1; i++) {
            rankTotal[i] += rank[i];
        }
    }
    
    private void setWidth(double[] rank) {
        for (int i = 0; i < Util.algorithmAmount; i++) {
            if (rank[Util.algorithmAmount] == rank[i]) {
                rank[i] = rank[i] + 0.5d;
                rank[Util.algorithmAmount] = rank[Util.algorithmAmount] + 0.5d;
            }
        }
    }
    
    private void increaseRank(double[] rank) {
        double value = rank[Util.algorithmAmount] + 0.5;
        for (int i = 0; i < Util.algorithmAmount; i++) {
            if (rank[i] >= value) {
                rank[i] = rank[i] + 1d;
            }
        }
    }
    
    private void setRankByBean(List<Bean> list, String dataSetName, double[] rank) {
        for (Bean b : list) {
            if (b.getDataSetName().equals(dataSetName)) {
                rank[Util.algorithmAmount] = rank[b.getMajor()];
            }
        }
    }
    private double[] getRank(String[] csvLineResult) {
        double[] rank = new double[Util.algorithmAmount+1];
        for (int i = 0; i < Util.algorithmAmount; i++) {
            rank[i] = Double.valueOf(csvLineResult[i*2+1]);
        }
        return rank;
    }
    
    private String getAlgorithmPredicted(Instances instances, int line) {
        String classPredicted;
        classPredicted = instances.get(line).stringValue(Util.getPredictorPosition(1));
        return classPredicted;
    }
    
    private void writeToCSV(List<String> dataSets, List<double[]> rank, double[] rankTotal) {
        try
        {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.getFilePath(Util.BEST_ALGORITHM_EXP, "BestAlgorithmVsRankMediaAllAlgorithms")), "UTF-8"))) {
                bw.write("dataSetName"+CSV_SEPARATOR+"RANK-NB"+CSV_SEPARATOR+"RANK-RF"+CSV_SEPARATOR+"RANK-J48"+CSV_SEPARATOR+"RANK-IBK"+CSV_SEPARATOR
                        +"RANK-SMO"+CSV_SEPARATOR+"RANK-MLP"+CSV_SEPARATOR+"RANK-ABM"+CSV_SEPARATOR+"RANK-XGB"+CSV_SEPARATOR+"RANK-ALL"+CSV_SEPARATOR);
                bw.newLine();
                StringBuffer oneLine;
                double[] rankMedio;
                for (int i = 0; i < dataSets.size(); i++) {
                    oneLine = new StringBuffer();
                    
                    oneLine.append(dataSets.get(i));
                    oneLine.append(CSV_SEPARATOR);
                    
                    rankMedio = rank.get(i);
                    
                    for (double d : rankMedio) {
                        oneLine.append(Utils.doubleToString(d, 3));
                        oneLine.append(CSV_SEPARATOR);
                    }
                    bw.write(oneLine.toString());
                    bw.newLine();
                }
                
                oneLine = new StringBuffer();
                oneLine.append("MEDIA");
                oneLine.append(CSV_SEPARATOR);
                for (double d : rankTotal) {
                    oneLine.append(Utils.doubleToString(d/dataSets.size(), 3));
                    oneLine.append(CSV_SEPARATOR);
                }
                bw.write(oneLine.toString());

                bw.newLine();
                oneLine = new StringBuffer();
                oneLine.append("StDev");
                oneLine.append(CSV_SEPARATOR);
                double[] stDev = getStDev(rank, rank.get(0).length);
                for (double d : stDev) {
                    oneLine.append(Utils.doubleToString(d, 3));
                    oneLine.append(CSV_SEPARATOR);
                }
                bw.write(oneLine.toString());
                
                bw.flush();
            }
        }
        catch (UnsupportedEncodingException e) {}
        catch (FileNotFoundException e){}
        catch (IOException e){}
    }
    
    private double[] getStDev(List<double[]> rankMedios, int size) {
        double[] stDev = new double[size];
        Statistics statistics;
        double[] values;
        for (int i = size-1; i > -1; i--){
            values = new double[rankMedios.size()];
            for (int j = 0; j < rankMedios.size(); j++) {
                values[j] = rankMedios.get(j)[i];
            }
            statistics = new Statistics(values);
            stDev[i] = statistics.getStdDev();
        }
        return stDev;
    }
    
    private int compare(String a, String b) {
        if (a.equals(b)) {
            return 1;
        } else {
            return 0;
        }
    }
    
    private void generateCsvToGraph(List<Bean> list) {
        try
        {   
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.getFilePath(Util.BEST_ALGORITHM_EXP, "validateGraphAllAlgorithms")), "UTF-8"))) {
                bw.write("dataSetName"+CSV_SEPARATOR);
                for (Bean b : list) {
                    bw.write(b.getDataSetName()+CSV_SEPARATOR);
                }
                bw.newLine();
                
                StringBuilder Line1 = new StringBuilder("NB"+CSV_SEPARATOR);
                StringBuilder Line2 = new StringBuilder("RF"+CSV_SEPARATOR);
                StringBuilder Line3 = new StringBuilder("J48"+CSV_SEPARATOR);
                StringBuilder Line4 = new StringBuilder("IBK"+CSV_SEPARATOR);
                StringBuilder Line5 = new StringBuilder("SVM"+CSV_SEPARATOR);
                StringBuilder Line6 = new StringBuilder("MLP"+CSV_SEPARATOR);
                StringBuilder Line7 = new StringBuilder("ABM"+CSV_SEPARATOR);
                
                
                for (Bean b : list) {
                    Line1.append(compare(b.getClazz(), b.getNb()));
                    Line1.append(CSV_SEPARATOR);
                    Line2.append(compare(b.getClazz(), b.getRf()));
                    Line2.append(CSV_SEPARATOR);
                    Line3.append(compare(b.getClazz(), b.getJ48()));
                    Line3.append(CSV_SEPARATOR);
                    Line4.append(compare(b.getClazz(), b.getIbk()));
                    Line4.append(CSV_SEPARATOR);
                    Line5.append(compare(b.getClazz(), b.getSmo()));
                    Line5.append(CSV_SEPARATOR);
                    Line6.append(compare(b.getClazz(), b.getMlp()));
                    Line6.append(CSV_SEPARATOR);
                    Line7.append(compare(b.getClazz(), b.getAbm()));
                    Line7.append(CSV_SEPARATOR);
                }
                bw.write(Line1.toString());
                bw.newLine();
                bw.write(Line2.toString());
                bw.newLine();
                bw.write(Line3.toString());
                bw.newLine();
                bw.write(Line4.toString());
                bw.newLine();
                bw.write(Line5.toString());
                bw.newLine();
                bw.write(Line6.toString());
                bw.newLine();
                bw.write(Line7.toString());
                bw.newLine();
                
                bw.flush();
            }
        }
        catch (UnsupportedEncodingException e) {}
        catch (FileNotFoundException e){}
        catch (IOException e){}
    }
    
    private class Bean {
        private String dataSetName;
        private String clazz;
        private String nb;
        private String rf;
        private String j48;
        private String ibk;
        private String smo;
        private String mlp;
        private String abm;
        private String predicted;
        private final int[] majoritory = new int[Util.algorithmAmount];

        
        
        private void setMajoritory(String predicted) {
            if (predicted.equals(Util.NB)) {
                majoritory[0] ++;
            }
            if (predicted.equals(Util.RF)) {
                majoritory[1] ++;
            }
            if (predicted.equals(Util.J48)) {
                majoritory[2] ++;
            }
            if (predicted.equals(Util.IBK)) {
                majoritory[3] ++;
            }
            if (predicted.equals(Util.SMO)) {
                majoritory[4] ++;
            }
            if (predicted.equals(Util.MLP)) {
                majoritory[5] ++;
            }
            if (predicted.equals(Util.ABM)) {
                majoritory[6] ++;
            }
            if (predicted.equals(Util.XGB)) {
                majoritory[7] ++;
            }
        }
        
        public String getClazz() {
            return clazz;
        }

        public void setClazz(String clazz) {
            this.clazz = clazz;
        }

        public String getNb() {
            return nb;
        }

        public void setNb(String nb) {
            this.nb = nb;
            setMajoritory(nb);
        }

        public String getRf() {
            return rf;
        }

        public void setRf(String rf) {
            this.rf = rf;
            setMajoritory(rf);
        }

        public String getJ48() {
            return j48;
        }

        public void setJ48(String j48) {
            this.j48 = j48;
            setMajoritory(j48);
        }

        public String getIbk() {
            return ibk;
        }

        public void setIbk(String ibk) {
            this.ibk = ibk;
            setMajoritory(ibk);
        }

        public String getSmo() {
            return smo;
        }

        public void setSmo(String smo) {
            this.smo = smo;
            setMajoritory(smo);
        }

        public String getMlp() {
            return mlp;
        }

        public void setMlp(String mlp) {
            this.mlp = mlp;
            setMajoritory(mlp);
        }

        public String getAbm() {
            return abm;
        }

        public void setAbm(String abm) {
            this.abm = abm;
            this.setMajoritory(abm);
        }        

        public String getPredicted() {
            return predicted;
        }

        public void setPredicted(String predicted) {
            this.predicted = predicted;
        }

        public String getDataSetName() {
            return dataSetName;
        }

        public void setDataSetName(String dataSetName) {
            this.dataSetName = dataSetName;
        }
        
        @Override
        public String toString() {
            return "Bean{" + "dataSet=" + dataSetName + "clazz=" + clazz + ", nb=" + nb + ", rf=" + rf + ", j48=" + j48 + ", ibk=" + ibk + ", smo=" + smo + ", mlp=" + mlp + ", abm=" + abm + ", predicted=" + predicted + ", majoritory=" + this.getMajor() + '}';
        }
        
        public int getMajor() {
            int amount = this.majoritory[0];
            int major = 0;
            for (int i = 1; i < Util.algorithmAmount; i++) {
                if (this.majoritory[i] > amount) {
                    amount = this.majoritory[i];
                    major = i;
                }
            }
            if (major == 0 && this.majoritory[0] == this.majoritory[1]) {
                logger.debug("NB X RF="+this.majoritory[0] + " X " + this.majoritory[1]);
                return 1;
            }
            return major;
        }
        
    }
}
