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
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 *
 * @author luciano
 */
public class SVMMain {

    final static Logger logger = Logger.getLogger(SVMMain.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SVMMain SvmMain = new SVMMain();
        SvmMain.init();
    }

    public void init() {
        String fileName = "";
        try {
            File[] files = new File(Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE).listFiles();
            Arrays.sort(files, (Object f1, Object f2) -> ((File) f1).getName().toLowerCase().compareTo(((File) f2).getName().toLowerCase()));
            Instances instances;
            List<Output> listOutputs = new ArrayList();
            Output output;
            for (File file : files) {
                if (!file.isDirectory() && file.getName().contains("arff") && file.getName().equals("CM1.arff")) {
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
                    
                    List<HyperParamSVM> model = getModel(train);
                    output = getEvaluation(test, train, fileName, model);
                    listOutputs.add(output);
                }
            }
            writeToCSV(listOutputs);
        } catch(Exception e) {
            logger.error("Error in file: "+fileName, e);
        }
    }

    private Output getEvaluation(Instances test, Instances train, String dataSetName, List<HyperParamSVM> model) throws Exception {
        Evaluation evaluation = new Evaluation(train);
        
        SMO smo = new SMO();
        HyperParamSVM hyperParamSVM = model.get(0);
        RBFKernel rbfk = new RBFKernel();
        rbfk.setGamma(hyperParamSVM.getGamma());
        smo.setKernel(rbfk);
        smo.setC(hyperParamSVM.getCost());
        smo.buildClassifier(train);
        double auc2;
        double auc1;
        
        evaluation.evaluateModel(smo, test);
        auc2 = evaluation.areaUnderROC(Util.DEFECTIVE);
        auc1 = evaluation.areaUnderROC(Util.DEFECT_FREE);
        
        hyperParamSVM = model.get(1);
        rbfk.setGamma(hyperParamSVM.getGamma());
        smo.setKernel(rbfk);
        smo.setC(hyperParamSVM.getCost());
        smo.buildClassifier(train);
        double pd;
        double pf;

        evaluation.evaluateModel(smo, test);
        pd = Util.getPD(evaluation);
        pf = Util.getPF(evaluation);
        
        logger.info("DataSet : " + dataSetName + " AUC = " + auc2);
        double balance = Util.getBalance(pd, pf);
        logger.info("DataSet : " + dataSetName + " Balance = "+balance);
        Output output = new Output(dataSetName, auc2, auc1, balance);
        return output;
    }
    
    public List<HyperParamSVM> getModel(Instances instances) throws Exception {
        List<HyperParamSVM> list = new ArrayList(2);
        List<HyperParamSVM> listOptions = Util.getListHyperParamSVM();
        HyperParamSVM hyperParameterSVMAUC = null;
        HyperParamSVM hyperParameterSVMBalance = null;
        double auc = 0;
        double balance = 0;
        Evaluation evaluation;
        Random random;
        
        SMO smo = new SMO();
               
        int i = 0;
        double n = Math.sqrt(instances.numAttributes());        
        RBFKernel rbfKernel = new RBFKernel();
        logger.debug("Amount of parameters: "+listOptions.size());
        for (HyperParamSVM s : listOptions) {
            double gamma = s.getGamma() * n;
            //logger.debug("Execution parameter (" + s.getCost() + ", "+ gamma + ")");
            rbfKernel.setGamma(gamma);
            smo.setKernel(rbfKernel);
            smo.setC(s.getCost());
            evaluation = new Evaluation(instances);
            random = new Random(i);
            evaluation.crossValidateModel(smo, instances, 10, random);
            double newAuc = evaluation.areaUnderROC(Util.DEFECTIVE);
            double newBalance = Util.getBalance(Util.getPD(evaluation), Util.getPF(evaluation));
            if (newAuc > auc) {
                logger.debug("Trocou AUC "+newAuc);
                hyperParameterSVMAUC = s;
                hyperParameterSVMAUC.setGamma(gamma);
                auc = newAuc;
            }
            if (newBalance > balance) {
                logger.debug("Trocou Balance "+newBalance);
                hyperParameterSVMBalance = s;
                hyperParameterSVMBalance.setGamma(gamma);
                balance = newBalance;
            }
            i++;
        }
        
        logger.debug("Optimal parameter AUC (" + hyperParameterSVMAUC.getCost() + ", "+ hyperParameterSVMAUC.getGamma() + ")");
        logger.debug("Optimal parameter BAL (" + hyperParameterSVMBalance.getCost() + ", "+ hyperParameterSVMBalance.getGamma() + ")");
        list.add(hyperParameterSVMAUC);
        list.add(hyperParameterSVMBalance);
        
        return list;
    }
    
    private void writeToCSV(List<Output> outputList)
    {
        try
        {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE +"/"+ "SVM.csv"), "UTF-8"))) {
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
