/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.utils;

import br.com.puc.alves.base.ClassifierRanking;
import br.com.puc.alves.base.ModelBean;
import br.com.puc.alves.lessmann.HyperParamSVM;
import br.com.puc.alves.lessmann.TreeOptions;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.log4j.Logger;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 *
 * @author luciano
 */
public class Util {
    final static Logger logger = Logger.getLogger(Util.class);
    public final static String RESOURCES = "src/main/resources/";
    
    public final static String DB_NORMALIZED_YES = RESOURCES + "NORMALIZED/";
    public final static String DB_NORMALIZED_NO = RESOURCES + "NONE/";
    public final static String DB_NORMALIZED = DB_NORMALIZED_NO;
    
    public final static String STRING_DB_DF_PRED = "database-defect-prediction/";
    public final static String DB_DF_PRED = DB_NORMALIZED + STRING_DB_DF_PRED;
    public final static String DCOL = DB_NORMALIZED + "dcol-results/";
    public final static String BASE_NIVEL = DB_NORMALIZED + "nivel-base/";
    public final static String META_NIVEL = DB_NORMALIZED + "nivel-meta/meta-base/";
    //public final static String RANKING = "src/main/resources/nivel-meta/ranking/";
    public final static String RANKING_PREDICT = DB_NORMALIZED + "nivel-meta/ranking/prediction/";
    public final static String RANKING_RESULT = DB_NORMALIZED + "nivel-meta/ranking/result/";
    public final static String RANKING_EXP = DB_NORMALIZED + "nivel-meta/ranking/experiment/";
    public final static String BEST_ALGORITHM_PREDICT = DB_NORMALIZED + "nivel-meta/bestAlgorithm/prediction/";
    public final static String BEST_ALGORITHM_EXP = DB_NORMALIZED + "nivel-meta/bestAlgorithm/experiment/";
    public final static String RESULTS = DB_NORMALIZED + "results/";
    
    public final static String DB_TYPE_LOG = "apply-log/";
    public final static String DB_TYPE_OFF = "official/";
    public final static String DB_TYPE_NASA_LOG = "nasa-log/";
    public final static String DB_TYPE_NASA_OFF = "nasa-off/";
    public static String DB_TYPE = DB_TYPE_OFF;

    public final static String SEARCH_NONE = "NONE";
    public final static String SEARCH_BE = "BE";
    public final static String SEARCH_FS = "FS";
    public static String SEARCH_TYPE = SEARCH_NONE;

    public final static String META_BASE_NONE = "NONE";
    public final static String META_BASE_STATLOG = "STATLOG";
    public final static String META_BASE_COMPLEXITY = "COMPLEXITY";
    public static String META_BASE_TYPE = META_BASE_NONE;
    
    public final static String MEASURE_AUC = "AUC";
    public final static String MEASURE_BALANCE = "Balance";
    public static String MEASURE_TYPE = MEASURE_AUC;
    
    public final static String CSV_SEPARATOR = ",";

    public final static int numFolds = 10;
    public final static int numIterations = 10;

    public final static int DEFECT_FREE = 0;
    public final static int DEFECTIVE = 1;
    
    public final static int SOLUTION_1 = 1;
    public final static int SOLUTION_2 = 2;

    public final static String algorithms = ClassifierRanking.NB+CSV_SEPARATOR+ClassifierRanking.RF+CSV_SEPARATOR+ClassifierRanking.J48+CSV_SEPARATOR+ClassifierRanking.IBK+CSV_SEPARATOR+ClassifierRanking.SMO+CSV_SEPARATOR+ClassifierRanking.MLP;
    
    public final static int algorithmAmount = 5;
    
    public static boolean IS_AUC = true;

    public static double getPD(Evaluation evaluation) {
        double pd;
        double TP = evaluation.numTruePositives(Util.DEFECTIVE);
        double FN = evaluation.numFalseNegatives(Util.DEFECTIVE);
        pd = TP / (TP + FN);
        return pd;
    }

    public static double getPF(Evaluation evaluation) {
        double pf;
        double FP = evaluation.numFalsePositives(Util.DEFECTIVE);
        double TN = evaluation.numTrueNegatives(Util.DEFECTIVE);
        pf = FP / (FP + TN);
        return pf;
    }

    public static double getBalance(double pd, double pf) {
        double balance;
        double pd2 = Math.pow(1 - pd, 2);
        double pf2 = Math.pow(0 - pf, 2);
        balance = 1 - ((Math.sqrt(pd2 + pf2)) / Math.sqrt(2));
        return balance;
    }

    public static int[] getRankings() {
        int[] rankings = new int[Util.algorithmAmount];
        for (int i = 0; i < Util.algorithmAmount; i++) {
            rankings[i] = getRanking(rankings);
            logger.debug(rankings[i]);
        }
        return rankings;
    }

    private static int getRanking(int[] rankings) {
        int rank = 0;
        Random gerador = new Random();
        while (rank == 0) {
            rank = gerador.nextInt(Util.algorithmAmount+1);
            for (int r : rankings) {
                if (rank == r) {
                    rank = 0;
                    break;
                }
            }
        }
        return rank;
    }

    public static List<String> getCsvToList(String csvFile) {
        List<String> lines = new ArrayList<>();
        BufferedReader br = null;
        String line;
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                lines.add(line);
            }

        } catch (FileNotFoundException e) {
            logger.error("Exception is",e);
        } catch (IOException e) {
            logger.error("Exception is",e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error("Exception is",e);
                }
            }
        }
        return lines;
    }
    
    public static Instances getInstancesFiltered(Instances instances) {
        //Nothing to do if META_BASE_NONE
        //if (META_BASE_TYPE.equals(META_BASE_NONE))
        switch (META_BASE_TYPE) {
            case META_BASE_STATLOG:
                for (int i = 18; i > 4; i--) {
                    instances.deleteAttributeAt(i);
                }   break;
            case META_BASE_COMPLEXITY:
                for (int i = 22; i > 18; i--) {
                    instances.deleteAttributeAt(i);
                }   for (int i = 4; i > 0; i--) {
                    instances.deleteAttributeAt(i);
            }   break;
        }
        instances.deleteAttributeAt(0);
        return instances;
    }
    
    public static int getPredictorPosition(int position) {
        switch (META_BASE_TYPE) {
            case META_BASE_NONE:
                return 22 + position;
            case META_BASE_STATLOG:
                return 8 + position;
            case META_BASE_COMPLEXITY:
                return 14 + position;
            default:
                return 22 + position;
        }
    }
    
    public static String getFilePath(String base, String fileName) {
        return getFilePath(base, fileName, 0);
    }
    
    //Util.RANKING_EXP + Util.DB_TYPE + Util.SEARCH_TYPE + "/RankingAleatoryBy"+measure+"-" +Util.algorithmAmount + ".csv"
    //Util.RANKING_RESULT + Util.DB_TYPE+ Util.SEARCH_TYPE + "/" + Util.META_BASE_TYPE +"/rankingAlgorithmBy"+ measure + "-" + Util.algorithmAmount + "-K-1.csv"
    public static String getFilePath(String base, String fileName, int k) {
        String fullName = base + Util.DB_TYPE + Util.SEARCH_TYPE + "/" + Util.META_BASE_TYPE + "/" + fileName + Util.MEASURE_TYPE + "-" + Util.algorithmAmount;
        if (k > 0) {
            fullName += "-K-"+k;
        }
        fullName += ".csv";
        return fullName;
    }
    
    public static Instances getTrainInstancesRankingKNN(Instances testInstances, int algorithmRankingIndex) throws Exception {
        return null;
        /*
        Instances trainInstances = null;
        switch (DB_TYPE) {
            case DB_TYPE_NASA_LOG:
                trainInstances = ConverterUtils.DataSource.read(META_NIVEL + DB_TYPE_LOG + SEARCH_TYPE + "/" + "metaFeatures-"+algorithmAmount +".csv");
                break;
            case DB_TYPE_NASA_OFF:
                trainInstances = ConverterUtils.DataSource.read(Util.META_NIVEL + DB_TYPE_OFF + SEARCH_TYPE + "/" + "metaFeatures-"+algorithmAmount +".csv");
                break;
        }
        
        if (trainInstances != null) {
            for (Instance iTest : testInstances) {
                for (Instance iTrain : trainInstances) {
                    //logger.debug("iTest "+iTest.stringValue(0) + "   iTtrain "+iTrain.stringValue(0));
                    if (iTrain.stringValue(0).equals(iTest.stringValue(0))) {
                        trainInstances.remove(iTrain);
                        logger.debug("Size of Train Instances: "+trainInstances.size());
                        break;
                    }
                }
            }
            logger.debug("Size of Train Instances: "+trainInstances.size());
            
            while (trainInstances.numAttributes() > 24) {
                if ((trainInstances.numAttributes()-1) <= algorithmRankingIndex) {
                    trainInstances.deleteAttributeAt(trainInstances.numAttributes()-2);
                } else {
                    trainInstances.deleteAttributeAt(trainInstances.numAttributes()-1);
                }
            }
            getInstancesFiltered(trainInstances);
            String clsIndex = "last";
            if (clsIndex.length() == 0) {
                clsIndex = "last";
            }
            switch (clsIndex) {
                case "first":
                    trainInstances.setClassIndex(0);
                    break;
                case "last":
                    trainInstances.setClassIndex(trainInstances.numAttributes() - 1);
                    break;
                default:
                    trainInstances.setClassIndex(Integer.parseInt(clsIndex) - 1);
                    break;
            }
        }
        return trainInstances;
        */
    }
    
    public static Instances getTrainInstancesBestAlgorithm(Instances testInstances) throws Exception {
        Instances trainInstances = null;
        switch (DB_TYPE) {
            case DB_TYPE_NASA_LOG:
                trainInstances = ConverterUtils.DataSource.read(META_NIVEL + DB_TYPE_LOG + SEARCH_TYPE + "/" + "metaFeatures-"+algorithmAmount +".csv");
                break;
            case DB_TYPE_NASA_OFF:
                trainInstances = ConverterUtils.DataSource.read(Util.META_NIVEL + DB_TYPE_OFF + SEARCH_TYPE + "/" + "metaFeatures-"+algorithmAmount +".csv");
                break;
        }
        
        if (trainInstances != null) {
            for (Instance iTest : testInstances) {
                for (Instance iTrain : trainInstances) {
                    //logger.debug("iTest "+iTest.stringValue(0) + "   iTtrain "+iTrain.stringValue(0));
                    if (iTrain.stringValue(0).equals(iTest.stringValue(0))) {
                        trainInstances.remove(iTrain);
                        logger.debug("Size of Train Instances: "+trainInstances.size());
                        break;
                    }
                }
            }
            logger.debug("Size of Train Instances: "+trainInstances.size());
            while (trainInstances.numAttributes() > 24) {
                if (trainInstances.numAttributes() > 25) {
                    trainInstances.deleteAttributeAt(trainInstances.numAttributes()-3);
                } else {
                    if (Util.MEASURE_TYPE.equals(Util.MEASURE_AUC)) {
                        trainInstances.deleteAttributeAt(trainInstances.numAttributes()-1);
                    } else {
                        trainInstances.deleteAttributeAt(trainInstances.numAttributes()-2);
                    }
                }
            }
            
            getInstancesFiltered(trainInstances);
            String clsIndex = "last";
            if (clsIndex.length() == 0) {
                clsIndex = "last";
            }
            switch (clsIndex) {
                case "first":
                    trainInstances.setClassIndex(0);
                    break;
                case "last":
                    trainInstances.setClassIndex(trainInstances.numAttributes() - 1);
                    break;
                default:
                    trainInstances.setClassIndex(Integer.parseInt(clsIndex) - 1);
                    break;
            }
        }
        return trainInstances;
    }
    
    public static List<TreeOptions> getListTreeOptions() {
        List<TreeOptions> list = new ArrayList<>();
        
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    boolean p = i == 0;
                    boolean l = j == 0;
                    boolean s = k == 0;
                    
                    list.add(new TreeOptions(p, 0.05f, l, s));
                    list.add(new TreeOptions(p, 0.1f, l, s));
                    list.add(new TreeOptions(p, 0.15f, l, s));
                    list.add(new TreeOptions(p, 0.2f, l, s));
                    list.add(new TreeOptions(p, 0.25f, l, s));
                    list.add(new TreeOptions(p, 0.3f, l, s));
                    list.add(new TreeOptions(p, 0.35f, l, s));
                    list.add(new TreeOptions(p, 0.4f, l, s));
                    list.add(new TreeOptions(p, 0.45f, l, s));
                    list.add(new TreeOptions(p, 0.5f, l, s));
                    list.add(new TreeOptions(p, 0.55f, l, s));
                    list.add(new TreeOptions(p, 0.6f, l, s));
                    list.add(new TreeOptions(p, 0.65f, l, s));
                    list.add(new TreeOptions(p, 0.7f, l, s));
                }
            }
        }
        
        return list;
    }
    
    public static List<HyperParamSVM> getListHyperParamSVM() {
        List<HyperParamSVM> list = new ArrayList<>();
        
        double cost[] = HyperParamSVM.C;
        double gamma[] = HyperParamSVM.G;
        
        for (double c : cost) {
            for (double g : gamma) {
                list.add(new HyperParamSVM(c, g));
            }
        }
        return list;
    }
    
    public static void saveModel(String algorithmName, ModelBean bean) {
        ObjectOutputStream objectOutputStream = null;
        FileOutputStream outputStream;
        try{
            outputStream = new FileOutputStream(Util.BASE_NIVEL + Util.DB_TYPE + Util.SEARCH_TYPE +"/"+ algorithmName + ".model", true);
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(bean);
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
}