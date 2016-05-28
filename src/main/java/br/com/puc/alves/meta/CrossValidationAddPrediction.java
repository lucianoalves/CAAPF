/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.puc.alves.meta;

import br.com.puc.alves.utils.Util;
import br.com.puc.alves.base.MLAlgorithmEnum;
import org.apache.log4j.Logger;
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AddClassification;

/**
 * Performs a single run of cross-validation and adds the prediction on the test
 * set to the dataset.
 * 
* Command-line parameters:
 * <ul>
 * <li>-t filename - the dataset to use</li>
 * <li>-o filename - the output file to store dataset with the predictions
 * in</li>
 * <li>-x int - the number of folds to use</li>
 * <li>-s int - the seed for the random number generator</li>
 * <li>-c int - the class index, "first" and "last" are accepted as well; "last"
 * is used by default</li>
 * <li>-W classifier - classname and options, enclosed by double quotes; the
 * classifier to cross-validate</li>
 * </ul>
 *
 * Example command-line:
 * 
* <pre>
 * java CrossValidationAddPrediction -t anneal.arff -c last -o predictions.arff -x 10 -s 1 -W "weka.classifiers.trees.J48 -C 0.25"
 * </pre>
 * 
* @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CrossValidationAddPrediction {

    final static Logger logger = Logger.getLogger(CrossValidationAddPrediction.class);
    
    /**
     * Performs the cross-validation. See Javadoc of class for information on
     * command-line parameters.
     *     
* @param args the command-line parameters
     */
    public static void main(String[] args) {
        try {
            CrossValidationAddPrediction prediction = new CrossValidationAddPrediction();
            for (int k =1; k < 2; k++)
            {
                for (int i = 0; i < Util.algorithmAmount; i++) {
                    prediction.process(i+23+Util.algorithmAmount, MLAlgorithmEnum.values()[i], k);
                }
            }
        } catch (Exception e) {
            logger.error("Error in running KNN like MA", e);
        }
    }
    
    public void process(int algorithmRankingIndex, MLAlgorithmEnum algorithmEnum, int k) throws Exception {
        // loads data and set class index
        Instances instances = DataSource.read(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + "metaFeaturesWithRanking-"+Util.MEASURE_TYPE+"-"+Util.algorithmAmount +".csv");
        
        Instances trainInstances = Util.getTrainInstancesRankingKNN(instances, algorithmRankingIndex);
                
        logger.debug("Read instances");
             
        while (instances.numAttributes() > 24) {
            if ((instances.numAttributes()-1) <= algorithmRankingIndex) {
                instances.deleteAttributeAt(instances.numAttributes()-2);
            } else {
                instances.deleteAttributeAt(instances.numAttributes()-1);
            }
        }
        //Select attribute by category (STATLOG/COMPLEXITY/NONE)
        //Remove attribute ID that is data set name
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
        Classifier cls = new IBk(k);
        if (k > 1) {
            String[] oldOptions = ((IBk)cls).getOptions();
            String[] newOptions = new String[7];
            int i = 0;
            for (String o : oldOptions) {
                newOptions[i] = o;
                i++;
            }
            newOptions[6] = "-I";
            //newOptions[7] = String.valueOf(IBk.WEIGHT_INVERSE);
            //Tag[[]
            //SelectedTag selectedTag = new SelectedTag(1, tags)
            ((IBk)cls).setOptions(newOptions);
        }
        // cls.setNumTrees(30);
        // other options
        int seed = 10;
        // int folds = Integer.parseInt(Utils.getOption("x", args));
        int folds = instances.size();
        // randomize data
        //Random rand = new Random(seed);
        //Instances randData = new Instances(instances);
        //randData.randomize(rand);
        //logger.debug("Randomized data");
        //if (randData.classAttribute().isNominal()) {
        //    logger.debug("Class attribute is nominal. Stratifying...");
        //    randData.stratify(folds);
        //}
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
             //on test set
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
        DataSink.write(Util.RANKING_PREDICT + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + Util.META_BASE_TYPE + "/predictions-"+ algorithmEnum + "-" + Util.algorithmAmount + "-RANK-" + Util.MEASURE_TYPE + "-K-"+k+".arff", predictedData);
    }
}