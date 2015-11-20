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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import weka.classifiers.Classifier;
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
public class RunningRandomForest {
    final static Logger logger = Logger.getLogger(RunningRandomForest.class);
    public static void main(String[] args) {
        try {
            RunningRandomForest runningRandomForest = new RunningRandomForest();
            Instances instances = runningRandomForest.init();
            Map<String, double[]> dataSets = runningRandomForest.process(instances);
            runningRandomForest.writeToCSV(dataSets);
        } catch(Exception e) {
            logger.error("Exception is",e);
        }
    }
    
    private Instances init() throws Exception {
        Instances instances = ConverterUtils.DataSource.read(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE + "/metaFeatures-"+Util.algorithmAmount+".csv");
        Instances trainInstances = Util.getTrainInstancesBestAlgorithm(instances);
        
        while (instances.numAttributes() > 24) {
            if (instances.numAttributes() > 25) {
                instances.deleteAttributeAt(instances.numAttributes()-3);
            } else {
                if (Util.MEASURE_TYPE.equals(Util.MEASURE_AUC)) {
                    instances.deleteAttributeAt(instances.numAttributes()-1);
                } else {
                    instances.deleteAttributeAt(instances.numAttributes()-2);
                }
            }
            
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
        Classifier cls = new RandomForest();
        
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
            //Classifier clsCopy = AbstractClassifier.makeCopy(cls);
            //logger.debug("Building classifier...");
            //clsCopy.buildClassifier(train);
            //logger.debug("Evaluating model...");
            //eval.evaluateModel(clsCopy, test);
            // add predictions
            AddClassification filter = new AddClassification();
            logger.debug("Creating filter...");
            filter.setClassifier(cls);
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
            
            logger.debug("Predict size: "+pred.size());
            
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
        logger.debug("Classifier: " + cls.getClass().getName() + " ");

        logger.debug("Dataset: " + instances.relationName());
        logger.debug("Folds: " + folds);
        logger.debug("Seed: " + seed);
        logger.debug("");
        //logger.debug(eval.toSummaryString("=== " + folds
        //        + "-fold Cross-validation ===", true));
        // output "enriched" dataset
        // DataSink.write(Utils.getOption("o", args), predictedData);
        ConverterUtils.DataSink.write(Util.BEST_ALGORITHM_PREDICT + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + Util.META_BASE_TYPE + "/RandomForestOnly" + "-" + Util.algorithmAmount + "-" + Util.MEASURE_TYPE + ".arff", predictedData);
        return predictedData;
    }
    
    public Map<String, double[]> process(Instances instances) {
        List<String> lines = Util.getCsvToList(Util.getFilePath(Util.RANKING_RESULT, "rankingAlgorithmBy", 1));
        
        Map<String, double[]> dataSets = new LinkedHashMap<>();
        double[] values; 
        int n = 0;
        for (String l : lines) {
            String[] r = l.split(Util.CSV_SEPARATOR);
            if (r[0].equals("DataSetName") || r[0].trim().equals("")) continue;
            
            String rfPredicted = instances.get(n).stringValue(Util.getPredictorPosition(1));
            n++;
            int order = getOrderByAlgorithm(rfPredicted);
            
            values = new double[16];
            values[0] = Double.valueOf(r[18]);
            values[1] = Double.valueOf(r[19]);
            values[2] = Double.valueOf(r[20]);
            values[3] = Double.valueOf(r[21]);
            values[4] = Double.valueOf(r[22]);
            values[5] = Double.valueOf(r[23]);
            values[6] = Double.valueOf(r[24]);
            
            values[7] = values[order-1];
            
            values[8] = Double.valueOf(r[1]);
            values[9] = Double.valueOf(r[3]);
            values[10] = Double.valueOf(r[5]);
            values[11] = Double.valueOf(r[7]);
            values[12] = Double.valueOf(r[9]);
            values[13] = Double.valueOf(r[11]);
            values[14] = Double.valueOf(r[13]);
            values[15] = values[order+Util.algorithmAmount];
            
            double value = values[15];
            for (int i = 8; i < (8 + Util.algorithmAmount); i++) {
                if (value == values[i]) {
                    values[15] += 0.5;
                    values[i] += 0.5;
                }
            }

            value += 1d;
            for (int i = 8; i < (8 + Util.algorithmAmount); i++) {
                if (values[i] >= value) {
                    values[i] = values[i] + 1d;
                }
            }
            dataSets.put(r[0], values);
        }
        return dataSets;
    }
    
    private int getOrderByAlgorithm(String algorithmName) {
        int order = 0;
        switch(algorithmName) {
            case ClassifierRanking.RF: 
                order = 2; 
                break;
            case ClassifierRanking.NB:
                order = 1; 
                break;
            case ClassifierRanking.J48:
                order = 3; 
                break;
            case ClassifierRanking.IBK:
                order = 4; 
                break;
            case ClassifierRanking.SMO:
                order = 5;
                break;
            case ClassifierRanking.MLP:
                order = 6; 
                break;
            case ClassifierRanking.ABM:
                order = 7; 
                break;
            default:
                throw new NumberFormatException("Invalid algorithm name " + algorithmName);
        }
            
        return order;
    }
    
    private void writeToCSV(Map<String, double[]> dataSets) {
        try
        {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.getFilePath(Util.BEST_ALGORITHM_EXP, "ValidateRandomForestMedia")), "UTF-8"))) {
                bw.write("dataSetName"+CSV_SEPARATOR+"NB"+CSV_SEPARATOR+"RF"+CSV_SEPARATOR+"J48"+CSV_SEPARATOR+"IBK"+CSV_SEPARATOR+"SMO"+CSV_SEPARATOR+
                        "MLP"+CSV_SEPARATOR+"ABM"+CSV_SEPARATOR+"METHOD VALUE"+CSV_SEPARATOR+"RANK-NB"+CSV_SEPARATOR+"RANK-RF"+CSV_SEPARATOR+
                        "RANK-J48"+CSV_SEPARATOR+"RANK-IBK"+CSV_SEPARATOR+"RANK-SMO"+CSV_SEPARATOR+"RANK-MLP"+CSV_SEPARATOR+"RANK-ABM"+CSV_SEPARATOR+"RANK-METHOD");
                bw.newLine();
                StringBuffer oneLine;
                
                double[] media = new double[16];
                for(Map.Entry<String, double[]> entry : dataSets.entrySet()) {
                    oneLine = new StringBuffer();
                    oneLine.append(entry.getKey());
                    oneLine.append(CSV_SEPARATOR);
                    
                    double[] values = entry.getValue();
                    int i = 0;
                    for (double d : values) {
                        oneLine.append(Utils.doubleToString(d, 3));
                        oneLine.append(CSV_SEPARATOR);
                        media[i] += d;
                        i++;
                    }
                    bw.write(oneLine.toString());
                    bw.newLine();
                }
                oneLine = new StringBuffer("MEDIA"+CSV_SEPARATOR);
                
                for (double d : media) {
                    oneLine.append(Utils.doubleToString(d/dataSets.size(), 3));
                    oneLine.append(CSV_SEPARATOR);
                }
                bw.write(oneLine.toString());
                
                bw.newLine();
                oneLine = new StringBuffer();
                oneLine.append("StDev");
                oneLine.append(CSV_SEPARATOR);
                List<double[]> results = new ArrayList(dataSets.values());
                double[] stDev;
                stDev = getStDev(results, results.get(0).length);
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
}
