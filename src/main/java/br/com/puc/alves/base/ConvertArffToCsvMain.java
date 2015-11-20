/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.base;

import br.com.puc.alves.utils.Util;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.apache.log4j.Logger;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVSaver;

/**
 *
 * @author luciano
 */
public class ConvertArffToCsvMain {

    final static Logger logger = Logger.getLogger(ConvertArffToCsvMain.class);
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        String[] s = new String[4];
        s[0] = "-i";
        s[2] = "-o";
        File[] files = new File(Util.DB_DF_PRED+Util.DB_TYPE+Util.SEARCH_TYPE).listFiles();
        Arrays.sort(files, (Object f1, Object f2) -> ((File) f1).getName().toLowerCase().compareTo(((File) f2).getName().toLowerCase()));
        ArffLoader loader;
        CSVSaver saver;
        for (File f : files) {
            if (f.getName().equals("csv")) {
                continue;
            }           
            loader = new ArffLoader();
            loader.setSource(f);
            saver = new CSVSaver();
            saver.setInstances(loader.getDataSet());
            saver.setFile(new File(Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE + "/csv/" + f.getName().replace(".arff", "") + ".csv"));
            saver.writeBatch();
        }
    }
    
}
