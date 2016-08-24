package br.com.puc.alves.base;

import java.io.Serializable;
import java.util.List;
import weka.classifiers.Evaluation;

/**
 * Created by alves on 6/15/14.
 */
public class Algorithm implements Serializable {
    private static final long serialVersionUID = -4619205209643664108L;
    
    public final static String NB = "NaiveBayes";
    public final static String RF = "RandomForest";
    public final static String C45 = "J48_C45";
    public final static String IBK = "IBk_KNN";
    public final static String SVM = "SMO_SVM";
    public final static String MLP = "MultilayerPerceptron";
    public final static String AB = "AdaBostM1";
    public final static String ONE_R = "OneR";
    public final static String RIPPER = "Ripper";
    public final static String XGB = "XGBoost";
    
    private MLAlgorithmEnum name;
    private double areaROC;
    private double balance;
    private List<Evaluation> listEvaluation;
      
    public Algorithm(MLAlgorithmEnum name, double areaROC, double balance, List<Evaluation> listEvaluations) {
        this.name = name;
        this.areaROC = areaROC;
        this.balance = balance;
        this.listEvaluation = listEvaluations;
    }
    
    public MLAlgorithmEnum getName() {
        return name;
    }

    public void setName(MLAlgorithmEnum name) {
        this.name = name;
    }

    public double getAreaROC() {
        return areaROC;
    }

    public void setAreaROC(double areaROC) {
        this.areaROC = areaROC;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public List<Evaluation> getListEvaluation() {
        return listEvaluation;
    }

    public void setListEvaluation(List<Evaluation> listEvaluation) {
        this.listEvaluation = listEvaluation;
    }
}