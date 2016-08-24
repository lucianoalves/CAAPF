/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.meta;

import br.com.puc.alves.base.MLAlgorithmEnum;
import br.com.puc.alves.utils.Util;
import java.util.Random;
import org.apache.log4j.Logger;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MLPClassifier;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AddClassification;

/**
 *
 * @author ssad
 */
public class RunningMLP1 {
    final static Logger logger = Logger.getLogger(RunningMLP1.class);
    public static void main(String[] args) {
        try {
            RunningMLP1 runningMLP1 = new RunningMLP1();
            Instances instances = runningMLP1.init();
        } catch(Exception e) {
            logger.error("Exception is",e);
        }
    }
    
    private Instances init() throws Exception {
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
        Classifier cls = new MLPClassifier();
        
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
            
            double[][] model = getModel(train);
            
            if (Util.MEASURE_TYPE.equals(Util.MEASURE_AUC)) {
                ((MLPClassifier)cls).setNumFunctions(new Double(model[0][0]).intValue());
                ((MLPClassifier)cls).setRidge(model[0][1]);
            } else {
                ((MLPClassifier)cls).setNumFunctions(new Double(model[1][0]).intValue());
                ((MLPClassifier)cls).setRidge(model[1][1]);
            }
            ((MLPClassifier)cls).buildClassifier(train);
            
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
        //ConverterUtils.DataSink.write(Util.BEST_ALGORITHM_PREDICT + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + Util.META_BASE_TYPE + "/" + MLAlgorithmEnum.MLP_1.name()  + "-" + Util.algorithmAmount + "-" + Util.MEASURE_TYPE + ".arff", predictedData);
        return predictedData;
    }
    
    public double[][] getModel(Instances instances) throws Exception {
        double[][] hyper = new double[2][2];
        double auc = 0;
        double balance = 0;
        Evaluation evaluation;
        Random random;
        MLPClassifier mLPClassifier = new MLPClassifier();
        mLPClassifier.setRidge(1);
        
        double[] ridges = {0.1,0.2};      
        int i = 0;
        
        for (int j = 4; j < 29; j++) {
            for (double r : ridges) {
                evaluation = new Evaluation(instances);
                random = new Random(i);
                mLPClassifier.setNumFunctions(j);
                mLPClassifier.setRidge(r);
                evaluation.crossValidateModel(mLPClassifier, instances, 10, random);
                double newAuc = evaluation.areaUnderROC(Util.DEFECTIVE);
                double newBalance = Util.getBalance(Util.getPD(evaluation), Util.getPF(evaluation));
                if (newAuc > auc) {
                    hyper[0][0] = j;
                    hyper[0][1] = r;
                    auc = newAuc;
                }
                if (newBalance > balance) {
                    hyper[1][0] = j;
                    hyper[1][1] = r;
                    balance = newBalance;
                }
                i++;
            }
            i++;
        }
        return hyper;
    }
}
