/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.sac;

import br.com.puc.alves.base.*;
import br.com.puc.alves.utils.Util;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.IEvaluation;
import ml.dmlc.xgboost4j.java.XGBoost;
import ml.dmlc.xgboost4j.java.XGBoostError;
import org.apache.log4j.Logger;

/**
 *
 * @author luciano
 */
public class SacXGBostMain {
    final static Logger logger = Logger.getLogger(SacXGBostMain.class);
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
            
            
            DMatrix trainMat = new DMatrix(SacUtils.SAC + SacUtils.ARFF + "/libSVM/"+datasetName+".txt");
            
            //set params
            HashMap<String, Object> params = new HashMap<>();
            
            params.put("silent", 1);
            params.put("objective", "binary:logitraw");
            
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
        } catch (XGBoostError ex) {
            java.util.logging.Logger.getLogger(SacXGBostMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        return metrics;
    }
    
    private void saveClassifier(String dataSetName, Algorithm algorithm) {
        ObjectOutputStream objectOutputStream = null;
        FileOutputStream outputStream;
        try{
            outputStream = new FileOutputStream(SacUtils.SAC + SacUtils.CLASSIFIERS +"/"+ dataSetName + "-" + algorithm.getName() + ".classifiers", true);
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
        SacXGBostMain xgbm = new SacXGBostMain();
        double metrics[] = xgbm.getXGBoostEvaluating(dataSetName, 8, 8);
        Algorithm algorithm = new Algorithm(MLAlgorithmEnum.XGB, metrics[0], metrics[1], new ArrayList<>());
        xgbm.saveClassifier(dataSetName, algorithm);
    }    
}
