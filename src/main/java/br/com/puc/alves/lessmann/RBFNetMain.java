/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.lessmann;

import br.com.puc.alves.utils.Util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.log4j.Logger;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.RBFNetwork;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 *
 * @author luciano
 */
public class RBFNetMain {

    final static Logger logger = Logger.getLogger(RBFNetMain.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RBFNetMain naiveBayesMain = new RBFNetMain();
        naiveBayesMain.init();
    }

    public void init() {
        File[] files = new File(Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE).listFiles();
        Arrays.sort(files, (Object f1, Object f2) -> ((File) f1).getName().toLowerCase().compareTo(((File) f2).getName().toLowerCase()));
        Instances instances;
        List<Output> listOutputs = new ArrayList();
        Output output;
        for (File file : files) {
            String fileName = "";
            try {
                if (!file.isDirectory() && file.getName().contains("arff")) {
                    fileName = file.getName();
                    instances = new ConverterUtils.DataSource(Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + fileName).getDataSet();
                    if (instances.classIndex() == -1) {
                        instances.setClassIndex(instances.numAttributes() -1);
                    }

                    instances.randomize(new Random(1));
                    int trainSize = (int) Math.round(instances.numInstances() * 2/3);
                    int testSize = instances.numInstances() - trainSize;
                    Instances train = new Instances(instances, 0, trainSize);
                    logger.debug("TRAIN " + train.size());

                    Instances test = new Instances(instances, trainSize, testSize);
                    logger.debug("TEST " + test.size());

                    int[] model = getModel(train);
                    output = getEvaluation(test, train, fileName, model);
                    listOutputs.add(output);
                }
            } catch(Exception e) {
                output = new Output(fileName, 0, 0, 0);
                listOutputs.add(output);
                logger.error("Error in file: "+fileName, e);
            }
        }
        writeToCSV(listOutputs);
        
    }

    private Output getEvaluation(Instances test, Instances train, String dataSetName, int[] model) throws Exception {
        Evaluation evaluation = new Evaluation(train);
        
        RBFNetwork rBFNetwork = new RBFNetwork();
        rBFNetwork.setNumClusters(model[0]);
        rBFNetwork.buildClassifier(train);
        double auc2;
        double auc1;
        
        evaluation.evaluateModel(rBFNetwork, test);
        auc2 = evaluation.areaUnderROC(Util.DEFECTIVE);
        auc1 = evaluation.areaUnderROC(Util.DEFECT_FREE);
        
        rBFNetwork.setNumClusters(model[1]);
        rBFNetwork.buildClassifier(train);
        double pd;
        double pf;

        evaluation.evaluateModel(rBFNetwork, test);
        pd = Util.getPD(evaluation);
        pf = Util.getPF(evaluation);
        
        logger.info("AUC = " + auc2);
        double balance = Util.getBalance(pd, pf);
        logger.info("Balance = "+balance);
        Output output = new Output(dataSetName, auc2, auc1, balance);
        return output;
    }
    
    public int[] getModel(Instances instances) throws Exception {
        int[] k = new int[2];
        double auc = 0;
        double balance = 0;
        Evaluation evaluation;
        Random random;
        RBFNetwork rBFNetwork = new RBFNetwork();
        for (int i = 1; i < 16; i+=2) {
            rBFNetwork.setNumClusters(i);
            evaluation = new Evaluation(instances);
            random = new Random(i);
            evaluation.crossValidateModel(rBFNetwork, instances, 10, random);
            double newAuc = evaluation.areaUnderROC(Util.DEFECTIVE);
            double newBalance = Util.getBalance(Util.getPD(evaluation), Util.getPF(evaluation));
            if (newAuc > auc) {
                k[0] = i;
                auc = newAuc;
            }
            if (newBalance > balance) {
                k[1] = i;
                balance = newBalance;
            }
        }
        return k;
    }
    
    private void writeToCSV(List<Output> outputList)
    {
        try
        {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE +"/"+ "RBFNet.csv"), "UTF-8"))) {
                StringBuffer oneLine = new StringBuffer();
                
                Field[] fields = Output.class.getFields();
                for (Field field : fields) {
                    oneLine.append(field.getName());
                    oneLine.append(Util.CSV_SEPARATOR);
                }                
                bw.write(oneLine.toString());
                bw.newLine();
                
                for (Output output : outputList)
                {               
                    oneLine = new StringBuffer();
                    oneLine.append(output.getDataSetName());
                    oneLine.append(Util.CSV_SEPARATOR);
                    oneLine.append(output.getAucDefective());
                    oneLine.append(Util.CSV_SEPARATOR);
                    oneLine.append(output.getAucNonDefective());
                    oneLine.append(Util.CSV_SEPARATOR);
                    oneLine.append(output.getBalance());
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
