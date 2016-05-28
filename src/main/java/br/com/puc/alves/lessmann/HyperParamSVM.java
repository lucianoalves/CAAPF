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
public class HyperParamSVM {
    public static double C[] = {0.01,0.05,0.1,0.5,1,5,10,50,100,500,1000};
    public static double G[] = {0.5,5,10,15,25,50,100,250,500};
    public static int CACHE_SIZE = 250007;
    
    private double cost;
    private double gamma;

    public HyperParamSVM(double cost, double gama) {
        this.cost = cost;
        this.gamma = gama;
    }
    
    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }
}
