/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.base;

import br.com.puc.alves.utils.Util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import weka.classifiers.Evaluation;

/**
 *
 * @author ssad
 */
public class UpdateClassifiers {
    final static Logger logger = Logger.getLogger(UpdateClassifiers.class);
    public static void main(String[] args) {
        UpdateClassifiers updateClassifiers = new UpdateClassifiers();
        
        File files = new File(Util.BASE_NIVEL + Util.DB_TYPE);
        for (File file : files.listFiles()) {
            List<ClassifierRanking> list = updateClassifiers.read(file);
            file.delete();
            updateClassifiers.change(list);
            updateClassifiers.write(list, file.getName());
        }
    }
    
    public List<ClassifierRanking> read(File file) {
        ObjectInputStream objectinputstream = null;
        FileInputStream streamIn = null;
        try {
            streamIn = new FileInputStream(file);
            objectinputstream = new ObjectInputStream(streamIn);
            return (List<ClassifierRanking>) objectinputstream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Exception is", e);
        } finally {
            try {
                if (streamIn != null) streamIn.close();
                if (objectinputstream != null) objectinputstream.close();
            } catch (Exception ex) {
                logger.error("Exception is", ex);
            }
        }
        return null;
    }
    public void write(List<ClassifierRanking> listClassifiers, String fileName) {
        ObjectOutputStream objectOutputStream = null;
        FileOutputStream outputStream;
        try{
            outputStream = new FileOutputStream(Util.BASE_NIVEL + Util.DB_TYPE + fileName, true);
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(listClassifiers);
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
    
    public void change (List<ClassifierRanking> listClassifiers) {
        for (ClassifierRanking cr : listClassifiers) {
            List<Evaluation> listEvaluation = cr.getListEvaluation();
            double auc = 0D;
            double pd = 0D;
            double pf = 0D;
            for (Evaluation e : listEvaluation) {
                //auc = auc + e.areaUnderROC(Util.DEFECTIVE);
                pd = pd + Util.getPD(e);
                pf = pf + Util.getPF(e);
            }
            cr.setAreaROC(auc / Util.numIterations);
            cr.setBalance(Util.getBalance(pd / Util.numIterations, pf / Util.numIterations));
        }
        Collections.sort(listClassifiers);
    }
    
}
