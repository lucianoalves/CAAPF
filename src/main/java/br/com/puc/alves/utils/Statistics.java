/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.utils;

import java.util.Arrays;

/**
 *
 * @author ssad
 */
public class Statistics {
    
    private final double[] data;
    
    public Statistics(double[] data) 
    {
        this.data = data;
    }
    
    public double getMean()
    {
        double sum = 0.0;
        for(double a : data)
            sum += a;
        return sum/data.length;
    }

    public double getVariance()
    {
        double mean = getMean();
        double temp = 0;
        for(double a :data)
            temp += (mean-a)*(mean-a);
        return temp/data.length;
    }

    public double getStdDev()
    {
        return Math.sqrt(getVariance());
    }
    
    public double median() 
    {
        Arrays.sort(data);

        if (data.length % 2 == 0) 
        {
           return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
        } 
        else 
        {
           return data[data.length / 2];
        }
    }
}
