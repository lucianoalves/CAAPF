/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.utils;

import static br.com.puc.alves.utils.Util.CSV_SEPARATOR;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ssad
 */
public class ExperimentUtils {
    public List<String> getExperiment(int experiment) {
        List<String> list = new ArrayList<>();
               
        list.add(Util.DB_TYPE_LOG + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_AUC);
        list.add(Util.DB_TYPE_NASA_LOG + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_AUC);
        list.add(Util.DB_TYPE_NASA_OFF + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_AUC);
        list.add(Util.DB_TYPE_OFF + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_AUC);
        
        list.add(Util.DB_TYPE_LOG + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_AUC);
        list.add(Util.DB_TYPE_NASA_LOG + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_AUC);
        list.add(Util.DB_TYPE_NASA_OFF + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_AUC);
        list.add(Util.DB_TYPE_OFF + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_AUC);
        
        list.add(Util.DB_TYPE_LOG + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_AUC);
        list.add(Util.DB_TYPE_NASA_LOG + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_AUC);
        list.add(Util.DB_TYPE_NASA_OFF + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_AUC);
        //list.add(Util.DB_TYPE_OFF + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_AUC);
        
        if (experiment >= 1) {
            list.add(Util.DB_TYPE_LOG + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_AUC);
            list.add(Util.DB_TYPE_NASA_LOG + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_AUC);
            list.add(Util.DB_TYPE_NASA_OFF + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_AUC);
            list.add(Util.DB_TYPE_OFF + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_AUC);
            list.add(Util.DB_TYPE_LOG + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_AUC);
            list.add(Util.DB_TYPE_NASA_LOG + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_AUC);
            list.add(Util.DB_TYPE_NASA_OFF + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_AUC);
            list.add(Util.DB_TYPE_OFF + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_AUC);
            list.add(Util.DB_TYPE_LOG + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_AUC);
            list.add(Util.DB_TYPE_NASA_LOG + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_AUC);
            list.add(Util.DB_TYPE_NASA_OFF + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_AUC);
            //list.add(Util.DB_TYPE_OFF + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_AUC);
            
            list.add(Util.DB_TYPE_LOG + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_AUC);
            list.add(Util.DB_TYPE_NASA_LOG + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_AUC);
            list.add(Util.DB_TYPE_NASA_OFF + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_AUC);
            list.add(Util.DB_TYPE_OFF + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_AUC);
            list.add(Util.DB_TYPE_LOG + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_AUC);
            list.add(Util.DB_TYPE_NASA_LOG + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_AUC);
            list.add(Util.DB_TYPE_NASA_OFF + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_AUC);
            list.add(Util.DB_TYPE_OFF + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_AUC);
            list.add(Util.DB_TYPE_LOG + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_AUC);
            list.add(Util.DB_TYPE_NASA_LOG + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_AUC);
            list.add(Util.DB_TYPE_NASA_OFF + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_AUC);
            //list.add(Util.DB_TYPE_OFF + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_AUC);
        }
        
        if (experiment >= 2) {
            list.add(Util.DB_TYPE_LOG + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_NASA_LOG + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_NASA_OFF + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_OFF + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_LOG + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_NASA_LOG + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_NASA_OFF + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_OFF + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_LOG + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_NASA_LOG + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_NASA_OFF + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            //list.add(Util.DB_TYPE_OFF + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_COMPLEXITY + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_LOG + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_NASA_LOG + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_NASA_OFF + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_OFF + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_LOG + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_NASA_LOG + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_NASA_OFF + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_OFF + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_LOG + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_NASA_LOG + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_NASA_OFF + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            //list.add(Util.DB_TYPE_OFF + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_NONE + CSV_SEPARATOR + Util.MEASURE_BALANCE);            
            list.add(Util.DB_TYPE_LOG + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_NASA_LOG + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_NASA_OFF + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_OFF + CSV_SEPARATOR + Util.SEARCH_BE + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_LOG + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_NASA_LOG + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_NASA_OFF + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_OFF + CSV_SEPARATOR + Util.SEARCH_FS + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_LOG + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_NASA_LOG + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            list.add(Util.DB_TYPE_NASA_OFF + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_BALANCE);
            //list.add(Util.DB_TYPE_OFF + CSV_SEPARATOR + Util.SEARCH_NONE + CSV_SEPARATOR + Util.META_BASE_STATLOG + CSV_SEPARATOR + Util.MEASURE_BALANCE);
        }        
        return list;
    }
}