/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.test;

import br.com.puc.alves.utils.ExperimentUtils;
import br.com.puc.alves.utils.Util;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author ssad
 */
public class CollectResults {
    final static Logger logger = Logger.getLogger(Util.class);
    private final ExperimentUtils experimentUtils = new ExperimentUtils();
    public static void main(String[] args) {
        CollectResults collectResults = new CollectResults();
        collectResults.process();
    }
    
    private void process() {
        List<String> list = experimentUtils.getExperiment(2);
        getRankingAleatoryResults(list);
        getRankingMajoritoryResults(list);
        getBestAlgorithmRandomForestResults(list);
        getBestAlgorithmEnsemble7Results(list);
    }
    
    private void getRankingAleatoryResults(List<String> experiments) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.RESULTS+"RankingAleatory.csv"), "UTF-8"));
            bw.write("TYPE,MEASURE,RA,PFS-MA-R");
            bw.newLine();
                      //String firstExperiment = list.get(0);
            //String[] firstExperimentSplit = firstExperiment.split(Util.CSV_SEPARATOR);
            List<String> experiment;
            for (String s : experiments) {
                String[] split = s.split(Util.CSV_SEPARATOR);
                
                Util.DB_TYPE = split[0];
                Util.SEARCH_TYPE = split[1];
                Util.META_BASE_TYPE = split[2];
                Util.MEASURE_TYPE = split[3];
                experiment = Util.getCsvToList(Util.getFilePath(Util.RANKING_EXP, "RankingAleatoryBy"));
                String type = s.replaceAll(Util.CSV_SEPARATOR, "-") + Util.CSV_SEPARATOR;
                String mediaLine = experiment.get(experiment.size() - 2);
                String line1 = mediaLine.replace(",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,", "");
                String stDevLine = experiment.get(experiment.size() - 1);
                String line2 = stDevLine.replace(",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,", "");
                
                bw.write(type);
                bw.write(line1);
                bw.newLine();
                
                bw.write(type);
                bw.write(line2);
                bw.newLine();
                bw.newLine();
            }
            bw.flush();
        } catch (UnsupportedEncodingException ex) {
            logger.error("Error ", ex);
        } catch (FileNotFoundException ex) {
            logger.error("Error ", ex);
        } catch (IOException ex) {
            logger.error("Error ", ex);
        }
    }
    
    private void getRankingMajoritoryResults(List<String> experiments) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.RESULTS+"RankingMajoritory.csv"), "UTF-8"));
            bw.write("TYPE,MEASURE,RMAJ,PFS-MA-R");
            bw.newLine();
                      //String firstExperiment = list.get(0);
            //String[] firstExperimentSplit = firstExperiment.split(Util.CSV_SEPARATOR);
            List<String> experiment;
            for (String s : experiments) {
                String[] split = s.split(Util.CSV_SEPARATOR);
                
                Util.DB_TYPE = split[0];
                Util.SEARCH_TYPE = split[1];
                Util.META_BASE_TYPE = split[2];
                Util.MEASURE_TYPE = split[3];
                experiment = Util.getCsvToList(Util.getFilePath(Util.RANKING_EXP, "RankingMajoritoryBy"));
                String type = s.replaceAll(Util.CSV_SEPARATOR, "-") + Util.CSV_SEPARATOR;
                String mediaLine = experiment.get(experiment.size() - 2);
                String line1 = mediaLine.replace(",,", "");
                String stDevLine = experiment.get(experiment.size() - 1);
                String line2 = stDevLine.replace(",,", "");
                
                bw.write(type);
                bw.write(line1);
                bw.newLine();
                
                bw.write(type);
                bw.write(line2);
                bw.newLine();
                bw.newLine();
            }
            bw.flush();
        } catch (UnsupportedEncodingException ex) {
            logger.error("Error ", ex);
        } catch (FileNotFoundException ex) {
            logger.error("Error ", ex);
        } catch (IOException ex) {
            logger.error("Error ", ex);
        }
    }
    
    private void getBestAlgorithmRandomForestResults(List<String> experiments) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.RESULTS+"BestAlgorithmRandomForest.csv"), "UTF-8"));
            bw.write("TYPE,MEASURE,NB,RF,C-4.5,KNN,SVM,MLP,AB,PFS-MA-RF");
            bw.newLine();
                      //String firstExperiment = list.get(0);
            //String[] firstExperimentSplit = firstExperiment.split(Util.CSV_SEPARATOR);
            List<String> experiment;
            for (String s : experiments) {
                String[] split = s.split(Util.CSV_SEPARATOR);
                
                Util.DB_TYPE = split[0];
                Util.SEARCH_TYPE = split[1];
                Util.META_BASE_TYPE = split[2];
                Util.MEASURE_TYPE = split[3];
                experiment = Util.getCsvToList(Util.getFilePath(Util.BEST_ALGORITHM_EXP, "ValidateRandomForestMedia"));
                String type = s.replaceAll(Util.CSV_SEPARATOR, "-") + Util.CSV_SEPARATOR;
                
                String[] mediaLine = experiment.get(experiment.size() - 2).split(Util.CSV_SEPARATOR);
                String line1 = mediaLine[0] + Util.CSV_SEPARATOR + mediaLine[9] + Util.CSV_SEPARATOR + mediaLine[10] + Util.CSV_SEPARATOR + mediaLine[11] + 
                        Util.CSV_SEPARATOR + mediaLine[12] + Util.CSV_SEPARATOR + mediaLine[13] + Util.CSV_SEPARATOR + mediaLine[14] + 
                        Util.CSV_SEPARATOR + mediaLine[15] + Util.CSV_SEPARATOR + mediaLine[16] + Util.CSV_SEPARATOR;
                
                String[] stDevLine = experiment.get(experiment.size() - 1).split(Util.CSV_SEPARATOR);
                String line2 = stDevLine[0] + Util.CSV_SEPARATOR + stDevLine[9] + Util.CSV_SEPARATOR + stDevLine[10] + Util.CSV_SEPARATOR + stDevLine[11] + 
                        Util.CSV_SEPARATOR + stDevLine[12] + Util.CSV_SEPARATOR + stDevLine[13] + Util.CSV_SEPARATOR + stDevLine[14] + 
                        Util.CSV_SEPARATOR + stDevLine[15] + Util.CSV_SEPARATOR + stDevLine[16] + Util.CSV_SEPARATOR;
                
                
                bw.write(type);
                bw.write(line1);
                bw.newLine();
                
                bw.write(type);
                bw.write(line2);
                bw.newLine();
                bw.newLine();
            }
            bw.flush();
        } catch (UnsupportedEncodingException ex) {
            logger.error("Error ", ex);
        } catch (FileNotFoundException ex) {
            logger.error("Error ", ex);
        } catch (IOException ex) {
            logger.error("Error ", ex);
        }
    }
    
    private void getBestAlgorithmEnsemble7Results(List<String> experiments) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.RESULTS+"BestAlgorithmEnsemble7.csv"), "UTF-8"));
            bw.write("TYPE,MEASURE,NB,RF,C-4.5,KNN,SVM,MLP,AB,PFS-MA-EN7");
            bw.newLine();
                      //String firstExperiment = list.get(0);
            //String[] firstExperimentSplit = firstExperiment.split(Util.CSV_SEPARATOR);
            List<String> experiment;
            for (String s : experiments) {
                String[] split = s.split(Util.CSV_SEPARATOR);
                
                Util.DB_TYPE = split[0];
                Util.SEARCH_TYPE = split[1];
                Util.META_BASE_TYPE = split[2];
                Util.MEASURE_TYPE = split[3];
                experiment = Util.getCsvToList(Util.getFilePath(Util.BEST_ALGORITHM_EXP, "BestAlgorithmVsRankMediaAllAlgorithms"));
                String type = s.replaceAll(Util.CSV_SEPARATOR, "-") + Util.CSV_SEPARATOR;
                
                String[] mediaLine = experiment.get(experiment.size() - 2).split(Util.CSV_SEPARATOR);
                String line1 = mediaLine[0] + Util.CSV_SEPARATOR + mediaLine[1] + Util.CSV_SEPARATOR + mediaLine[2] + Util.CSV_SEPARATOR + mediaLine[3] + 
                        Util.CSV_SEPARATOR + mediaLine[4] + Util.CSV_SEPARATOR + mediaLine[5] + Util.CSV_SEPARATOR + mediaLine[6] + 
                        Util.CSV_SEPARATOR + mediaLine[7] + Util.CSV_SEPARATOR + mediaLine[8] + Util.CSV_SEPARATOR;
                
                String[] stDevLine = experiment.get(experiment.size() - 1).split(Util.CSV_SEPARATOR);
                String line2 = stDevLine[0] + Util.CSV_SEPARATOR + stDevLine[1] + Util.CSV_SEPARATOR + stDevLine[2] + Util.CSV_SEPARATOR + stDevLine[3] + 
                        Util.CSV_SEPARATOR + stDevLine[4] + Util.CSV_SEPARATOR + stDevLine[5] + Util.CSV_SEPARATOR + stDevLine[6] + 
                        Util.CSV_SEPARATOR + stDevLine[7] + Util.CSV_SEPARATOR + stDevLine[8] + Util.CSV_SEPARATOR;
                
                
                bw.write(type);
                bw.write(line1);
                bw.newLine();
                
                bw.write(type);
                bw.write(line2);
                bw.newLine();
                bw.newLine();
            }
            bw.flush();
        } catch (UnsupportedEncodingException ex) {
            logger.error("Error ", ex);
        } catch (FileNotFoundException ex) {
            logger.error("Error ", ex);
        } catch (IOException ex) {
            logger.error("Error ", ex);
        }
    }
}
