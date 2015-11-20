/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.base;

/**
 *
 * @author luciano
 */
public enum MLAlgorithmEnum {
    LDA(1),QDA(2),LogReg(3),NB(4),BayesNet(5),LARS(6),RVM(7),K_NN(8),K_(9),MLP_1(10),MLP_2(11),RBF_NET(12),SVM(13),L_SVM(14),
    LS_SVM(15),LP(16),VP(17),C45(18),CART(19),ADT(20),RND_FOR(21),LMT(22);
    
    private final Integer ordinal;
    private MLAlgorithmEnum(Integer ordinal) {
        this.ordinal = ordinal;
    }
}
