/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.test;

import br.com.puc.alves.base.MetaBase;
import br.com.puc.alves.meta.CrossValidationAddPrediction;
import br.com.puc.alves.meta.RankingAUC;
import br.com.puc.alves.meta.RankingAleatory;
import br.com.puc.alves.meta.RankingBalance;
import br.com.puc.alves.meta.RankingKNNvsRankMediaAllAlgorithm;
import br.com.puc.alves.meta.RankingMajoritary;
import br.com.puc.alves.meta.RunningAllAlgorithms;
import br.com.puc.alves.meta.RunningRandomForest;
import br.com.puc.alves.utils.ExperimentUtils;
import br.com.puc.alves.utils.Util;
import java.util.List;
import static br.com.puc.alves.utils.Util.CSV_SEPARATOR;
import org.apache.log4j.Logger;

/**
 *
 * @author ssad
 */
public class Experiment {
    final static Logger logger = Logger.getLogger(Experiment.class);
    
    private final ExperimentUtils experimentUtils = new ExperimentUtils();
    
    @SuppressWarnings("static-access")
    public static void main(String[] args) {    
        Experiment experiment = new Experiment();
        //logger.info("Generating Meta Base");
        ///experiment.generateMetaBase();        
        logger.info("Predicting with K-NN");
        experiment.generateRankWithKNN();
        logger.info("Generating ranking and calculate spearman");
        experiment.generateRankingAndSpearman();
        logger.info("Running experiments");
        experiment.executingExperiments();
        
        CollectResults collectResults = new CollectResults();
        collectResults.main(null);
    }
    
    @SuppressWarnings("static-access")
    private void generateMetaBase() {
        List<String> experiments = experimentUtils.getExperiment(0);
        //Generate Meta Base
        MetaBase main = new MetaBase();
        for (String s : experiments) {
            String[] split = s.split(CSV_SEPARATOR);
            Util.DB_TYPE = split[0];
            Util.SEARCH_TYPE = split[1];
            logger.info("Experiment -> databases "+ Util.DB_TYPE.toUpperCase() + " attr sel "+Util.SEARCH_TYPE);
            main.main(null);
        }
    }
    
    @SuppressWarnings("static-access")
    private void generateRankWithKNN() {
        List<String> experiments = experimentUtils.getExperiment(1);
        CrossValidationAddPrediction crossValidationAddPrediction = new CrossValidationAddPrediction();
        for (String s : experiments) {
            String[] split = s.split(CSV_SEPARATOR);
            Util.DB_TYPE = split[0];
            Util.SEARCH_TYPE = split[1];
            Util.META_BASE_TYPE = split[2];
            logger.info("Experiment -> databases "+ Util.DB_TYPE.toUpperCase() + " attr sel "+Util.SEARCH_TYPE + " meta-base " + Util.META_BASE_TYPE);
            crossValidationAddPrediction.main(null);
        }
    }
    
    @SuppressWarnings("static-access")
    private void generateRankingAndSpearman() {
        List<String> experiments = experimentUtils.getExperiment(1);
        RankingAUC rankingAUC = new RankingAUC();
        RankingBalance rankingBalance = new RankingBalance();
        for (String s : experiments) {
            String[] split = s.split(CSV_SEPARATOR);
            Util.DB_TYPE = split[0];
            Util.SEARCH_TYPE = split[1];
            Util.META_BASE_TYPE = split[2];
            logger.info("Experiment -> databases "+ Util.DB_TYPE.toUpperCase() + " attr sel "+Util.SEARCH_TYPE + " meta-base " + Util.META_BASE_TYPE);
            rankingAUC.main(null);
            rankingBalance.main(null);
        }
    }
    
    @SuppressWarnings("static-access")
    private  void executingExperiments() {
        List<String> experiments = experimentUtils.getExperiment(2);
        RankingAleatory rankingAleatory = new RankingAleatory();
        RankingMajoritary rankingMajoritary = new RankingMajoritary();
        RankingKNNvsRankMediaAllAlgorithm rankingKNNvsRankMediaAllAlgorithm = new RankingKNNvsRankMediaAllAlgorithm();
        RunningAllAlgorithms runningAllAlgorithms = new RunningAllAlgorithms();
        RunningRandomForest runningRandomForest = new RunningRandomForest();
        for (String s : experiments) {
            String[] split = s.split(CSV_SEPARATOR);
            Util.DB_TYPE = split[0];
            Util.SEARCH_TYPE = split[1];
            Util.META_BASE_TYPE = split[2];
            Util.MEASURE_TYPE = split[3];
            logger.info("Experiment -> databases "+ Util.DB_TYPE.toUpperCase() + " attr sel "+Util.SEARCH_TYPE + " meta-base " + Util.META_BASE_TYPE+ " measure "+Util.MEASURE_TYPE.toUpperCase());
            rankingAleatory.main(null);
            rankingMajoritary.main(null);
            rankingKNNvsRankMediaAllAlgorithm.main(null);
            runningAllAlgorithms.main(null);
            runningRandomForest.main(null);
        }
    }    
}