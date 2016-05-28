/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.base;

/**
 *
 * @author luciano
 */
public class ModelBean {
    private double[] auc;
    private double[] balance;

    public ModelBean(double[] auc, double[] balance) {
        this.auc = auc;
        this.balance = balance;
    }

    public double[] getAuc() {
        return auc;
    }

    public void setAuc(double[] auc) {
        this.auc = auc;
    }

    public double[] getBalance() {
        return balance;
    }

    public void setBalance(double[] balance) {
        this.balance = balance;
    }
    
    
}
