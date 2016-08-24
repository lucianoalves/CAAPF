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
    //LDA(1),QDA(2),LogReg(3),NB(4),BayesNet(5),LARS(6),RVM(7),K_NN(8),KStar(9),MLP_1(10),MLP_2(11),RBF_NET(12),SVM(13),L_SVM(14),
    //LS_SVM(15),LP(16),VP(17),C45(18),CART(19),ADT(20),RND_FOR(21),LMT(22);
    
    //LDA(1),QDA(2),LogReg(3),NB(4),BayesNet(5),LARS(6),K_NN(7),KStar(8),MLP_1(9),RBF_NET(10),SVM(11),
    //LS_SVM(12),VP(13),C45(14),CART(15),ADT(16),RND_FOR(17),LMT(18);
    
    //LogReg(1),LARS(2),MLP_1(3),LS_SVM(4),RND_FOR(5);
    NB(1), RND_FOR(2), C45(3), K_NN(4), SVM(5), MLP(6), AB(7), XGB(8);
    
    private final Integer ordinal;
    private MLAlgorithmEnum(Integer ordinal) {
        this.ordinal = ordinal;
    }
}
