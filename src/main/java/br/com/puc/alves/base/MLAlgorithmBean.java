/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.base;

import java.io.Serializable;

/**
 *
 * @author luciano
 */
public class MLAlgorithmBean implements Comparable<MLAlgorithmBean>, Serializable {

    private MLAlgorithmEnum algorithmEnum;
    private double auc;
    private double rank;

    public MLAlgorithmBean(MLAlgorithmEnum algorithmEnum, double auc, double rank) {
        this.algorithmEnum = algorithmEnum;
        this.auc = auc;
        this.rank = rank;
    }

    public MLAlgorithmEnum getAlgorithmEnum() {
        return algorithmEnum;
    }

    public void setAlgorithmEnum(MLAlgorithmEnum algorithmEnum) {
        this.algorithmEnum = algorithmEnum;
    }

    public double getAuc() {
        return auc;
    }

    public void setAuc(double auc) {
        this.auc = auc;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }    

    @Override
    public int compareTo(MLAlgorithmBean obj) {
        if (this.auc < obj.getAuc()) {
            return 1;
        } else if (this.auc > obj.getAuc()) {
            return -1;
        } else {
            return 0;
        }
    }
}
