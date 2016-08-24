/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.latex;

import br.com.puc.alves.base.MetaBase;
import br.com.puc.alves.utils.Util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.logging.Level;

/**
 *
 * @author luciano
 */
public class DataSetsMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File[] files = new File(Util.DB_DF_PRED + Util.DB_TYPE + Util.SEARCH_TYPE).listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String name = pathname.getName().toLowerCase();
                return name.endsWith(".arff") && pathname.isFile();
            }
        });
        
        Arrays.sort(files, (Object f1, Object f2) -> ((File) f1).getName().toLowerCase().compareTo(((File) f2).getName().toLowerCase()));
        
        BufferedWriter bw;
        
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util.LATEX + "dataSets.tex"), "UTF-8"));
            int n = files.length / 5;
            /*
            \begin{table*}[!htbp]
            \footnotesize
            \centering
            \caption{PROMISE Bases Públicas para Predição de Falhas de Software}
            \label{base-dados}
            \begin{tabular*}{\textwidth}{@{\extracolsep{\fill}} lllll}
            %\begin{tabular}{c|c|c|c|c}
            \toprule
            \multicolumn{5}{c}{Base de Dados}                                                             \\
            \midrule
            \textbf{1.} ant-1.3 & \textbf{2.} ant-1.4   & \textbf{3.} ant-1.5      & \textbf{4.} ant-1.6     & \textbf{5.} ant-1.7    \\
            \textbf{6.} ar1        & \textbf{7.} ar3          & \textbf{8.} ar4          & \textbf{9.} ar6         & \textbf{10.} arc        \\
            &                   & \textbf{71.} xerces-init  &                  &                \\ 
            \bottomrule
            \end{tabular*}
            \end{table*}
            */
            
            bw.write("\\begin{table*}[!htbp]");
            bw.newLine();
            bw.write("\\footnotesize");
            bw.newLine();
            bw.write("\\centering");
            bw.newLine();
            bw.write("\\caption{tera-PROMISE Base de Dados para PFS usadas pelo \\textit{framework} PFS-FMA}");
            bw.newLine();
            bw.write("\\label{data-sets}");
            bw.newLine();
            bw.write("\\begin{tabular*}{\\textwidth}{@{\\extracolsep{\\fill}} lllll}");
            bw.newLine();
            bw.write("\\toprule");
            bw.newLine();
            bw.write("\\multicolumn{5}{c}{Base de Dados}                                                             \\\\");
            bw.newLine();
            bw.write("\\midrule");  
            bw.newLine();
            
            String line = "";
            
            int c = 0;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < 5; j++) {     
                    if (c >= files.length) {
                        line += " &";
                    } else {
                        line += "\\textbf{"+(c+1)+".} "+files[c].getName().replace(".arff", "")+" & ";
                    }
                    c++;
                }
                line = line.substring(0, line.length()-2) + "   \\\\";
                bw.write(line);
                bw.newLine();
                line = "";
            }
            bw.write("\\bottomrule");
            bw.newLine();
            bw.write("\\end{tabular*}");
            bw.newLine();
            bw.write("\\end{table*}");

            bw.flush();
        
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(MetaBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
