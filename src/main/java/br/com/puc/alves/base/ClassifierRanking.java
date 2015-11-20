package br.com.puc.alves.base;

import br.com.puc.alves.utils.Util;
import java.io.Serializable;
import java.util.List;
import weka.classifiers.Evaluation;

/**
 * Created by alves on 6/15/14.
 */
public class ClassifierRanking implements Comparable<ClassifierRanking>, Serializable {
    private static final long serialVersionUID = -4619205209643664108L;
    
    public final static String NB = "NaiveBayes";
    public final static String RF = "RandomForest";
    public final static String J48 = "J48_C45";
    public final static String IBK = "IBk_KNN";
    public final static String SMO = "SMO_SVM";
    public final static String MLP = "MultilayerPerceptron";
    public final static String ABM = "AdaBostM1";
    
    private String name;
    private double areaROC;
    private double balance;
    private double rankingAUC;
    private double rankingBalance;
    private List<Evaluation> listEvaluation;
      
    public ClassifierRanking(String name, double areaROC, double balance, double rankingAUC, double rankingBalance, List<Evaluation> listEvaluations) {
        this.name = name;
        this.areaROC = areaROC;
        this.balance = balance;
        this.rankingAUC = rankingAUC;
        this.rankingBalance = rankingBalance;
        this.listEvaluation = listEvaluations;
    }
    
    @Override
    public int compareTo(ClassifierRanking obj) {
        if (Util.IS_AUC) {        
            if (this.areaROC < obj.getAreaROC()) {
                return 1;
            } else if (this.areaROC > obj.getAreaROC()) {
                return -1;
            } else {
                return 0;
            }
        } else {
            if (this.balance < obj.getBalance()) {
                return 1;
            } else if (this.balance > obj.getBalance()) {
                return -1;
            } else {
                return 0;
            }
        }
        
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        super.clone();
        ClassifierRanking classifierRanking = new ClassifierRanking(this.name, this.areaROC, this.balance, this.rankingAUC, this.rankingBalance, this.listEvaluation);
        return classifierRanking;
    }

    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
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

    public double getRankingAUC() {
        return rankingAUC;
    }

    public void setRankingAUC(double rankingAUC) {
        this.rankingAUC = rankingAUC;
    }

    public double getRankingBalance() {
        return rankingBalance;
    }

    public void setRankingBalance(double rankingBalance) {
        this.rankingBalance = rankingBalance;
    }

    public List<Evaluation> getListEvaluation() {
        return listEvaluation;
    }

    public void setListEvaluation(List<Evaluation> listEvaluation) {
        this.listEvaluation = listEvaluation;
    }

    @Override
    public String toString() {
        return name;
    }
    
    
}