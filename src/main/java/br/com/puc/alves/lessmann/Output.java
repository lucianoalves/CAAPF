/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.lessmann;

/**
 *
 * @author luciano
 */
public class Output {
    public String dataSetName;
    public double aucDefective;
    public double aucNonDefective;
    public double balance;

    public Output(String dataSetName, double aucDefective, double aucNonDefective, double balance) {
        this.dataSetName = dataSetName;
        this.aucDefective = aucDefective;
        this.aucNonDefective = aucNonDefective;
        this.balance = balance;
    }

    public String getDataSetName() {
        return dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }

    public double getAucDefective() {
        return aucDefective;
    }

    public void setAucDefective(double aucDefective) {
        this.aucDefective = aucDefective;
    }

    public double getAucNonDefective() {
        return aucNonDefective;
    }

    public void setAucNonDefective(double aucNonDefective) {
        this.aucNonDefective = aucNonDefective;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}