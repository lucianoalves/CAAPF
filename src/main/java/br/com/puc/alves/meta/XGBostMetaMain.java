/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.meta;

import br.com.puc.alves.base.*;
import br.com.puc.alves.utils.Util;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import ml.dmlc.xgboost4j.java.Booster;
import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.IEvaluation;
import ml.dmlc.xgboost4j.java.XGBoost;
import ml.dmlc.xgboost4j.java.XGBoostError;
import org.apache.log4j.Logger;

/**
 *
 * @author luciano
 */
public class XGBostMetaMain {
    final static Logger logger = Logger.getLogger(XGBostMetaMain.class);
    public static class EvaluationBalance implements IEvaluation {
        final static Logger logger = Logger.getLogger(EvaluationBalance.class);
        
        String evalMetric = "custom_balance";
        
        public EvaluationBalance() {
        }

        @Override
        public String getMetric() {
          return evalMetric;
        }

        @Override
        public float eval(float[][] predicts, DMatrix dmat) {
          float balance = 0f;
          float[] labels;
          try {
            labels = dmat.getLabel();
          } catch (XGBoostError ex) {
            logger.error(ex);
            return -1f;
          }
          
          float aFP = 0f;
          float bFN = 0f;
          float cTN = 0f;
          float dTP = 0f;
          
          int nrow = predicts.length;
          for (int i = 0; i < nrow; i++) {
            if (labels[i] == 0f && predicts[i][0] <= 0) {
              aFP++;
            } else if (labels[i] == 1f && predicts[i][0] <= 0) {
              bFN++;
            } else if (labels[i] == 0f && predicts[i][0] > 0) {
              cTN++;
            } else if (labels[i] == 1f && predicts[i][0] > 0) {
              dTP++;
            }
          }
          
          float pd = 0f;
          if (bFN > 0 && dTP > 0) {
              pd = dTP / (bFN + dTP);
          }
          
          float pf = 0f;
          if (aFP > 0 && cTN > 0) {
              pf = cTN / (aFP + cTN);
          }
          
          balance = Util.getBalance(pd, pf);
          
          return balance;
        }
    }
    
    public double[] getXGBoostEvaluating(String datasetName, int seed, int numFolds) {
        double[] metrics = new double[2];
        try {
            DMatrix originalMat = new DMatrix(Util.META_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE + "/libSVM/metaFeaturesWithBestAlgorithm-"+Util.MEASURE_TYPE+"-"+Util.algorithmAmount+".txt");
            
            //set params
            HashMap<String, Object> params = new HashMap<>();
            
            params.put("silent", 1);
            params.put("objective", "multi:softmax");
            params.put("num_class", Util.algorithmAmount);
            
            //params.put("max_depth", 3);
            //params.put("subsample", 1.0);
            //params.put("gamma", 0.8);
            //params.put("colsample_bytree", 0.8);
            //params.put("min_child_weight", 0.8);
            //params.put("max_delta_step", 1);
            //params.put("alpha", 1.0);
            //params.put("lambda", 1.0);
            //params.put("eta", 0.05);
            //params.put("nthread", 6);
            params.put("seed", seed);
            
            /*
            params.put("eta", 1.0);
            params.put("max_depth", 3);
            params.put("silent", 1);
            params.put("nthread", 6);
            params.put("objective", "binary:logistic");
            params.put("gamma", 1.0);
            params.put("eval_metric", "auc");
            */
            //do 5-fold cross validation
            int round = 10;
            int nfold = numFolds;
            
            int rowNum =  new Long(originalMat.rowNum()).intValue();
            
            float[] result = new float[rowNum];
            
            
            for (int i = 0; i < rowNum; i++) {
                DMatrix trainMat = originalMat.slice(this.getTrainRowIndex(i,rowNum));
                
                int[] rowIndex = new int[1];
                rowIndex[0] = i;
                
                DMatrix testMat = originalMat.slice(rowIndex);
                
                java.util.Map<String, DMatrix> map = new HashMap();
                map.put("test", trainMat);
                                
                Booster booster = XGBoost.train(trainMat, params, round, map, null, null);
                
                float[][] f = booster.predict(testMat);
                result[i] = f[0][0] + 1;
            }
            
            for (int i = 0; i < rowNum; i++) {
                System.out.println(result[i]);
            }
            
            
            
            
            
            /*
            
            //set additional eval_metrics
            //metrics[0] = "AUC";
            IEvaluation evaluation = new EvaluationBalance();
            
            String[] evalHistBalance = XGBoost.crossValidation(trainMat, params, round, nfold, null, null, evaluation);
            String sBalance = evalHistBalance[9].replaceAll("[^\\.0123456789]","");
            double balance = new Double(sBalance.substring(sBalance.lastIndexOf(".")-1, sBalance.length()));
            logger.debug("Balance "+balance);
            metrics[1] = balance;
            
            //params.replace("objective", "binary:logistic");
            params.put("eval_metric", "auc");
            String[] evalHistAUC = XGBoost.crossValidation(trainMat, params, round, nfold, null, null, null);
            String sAUC = evalHistAUC[9].replaceAll("[^\\.0123456789]","");
            double auc = new Double(sAUC.substring(2, sAUC.lastIndexOf(".")-1));
            logger.debug("AUC "+auc);
            metrics[0] = auc;
            
            
            */
        } catch (XGBoostError ex) {
            java.util.logging.Logger.getLogger(XGBostMetaMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        return metrics;
    }
    
    private int[] getTrainRowIndex(int fold, int size) {
        int[] result = new int[size - 1];
        
        for (int i = 0; i < fold; i++) {
            result[i] = i;
        }
        
        for (int i = fold; i < size-1; i++) {
            result[i] = i+1;
        }
        
        return result;
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
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String dataSetName = "poi-2.0";
        XGBostMetaMain xgbm = new XGBostMetaMain();
        double metrics[] = xgbm.getXGBoostEvaluating(dataSetName, 8, 8);
        Algorithm algorithm = new Algorithm(MLAlgorithmEnum.XGB, metrics[0], metrics[1], new ArrayList<>());
        xgbm.saveClassifier(dataSetName, algorithm);
    }    
}
