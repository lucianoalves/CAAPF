package br.com.puc.alves.base;

import java.util.List;

/**
 * Created by alves on 4/13/14.
 */
public class MetaFeatures {

    public String dataSetName;
    public int example;
    public int attribute;
    public int clazz;
    public int binaryAttributes;

    /*
        F1:  Maximum Fisher's discriminant ratio
     */
    public double F1;

    /*
        F1v: Directional-vector maximum Fisher's discriminant ratio
     */
    public double F1v;

    /*
        F2:  Overlap of the per-class bounding boxes
     */
    public double F2;

    /*
        F3:  Maximum (individual) feature efficiency
     */
    public double F3;

    /*
        F4:  Collective feature efficiency (sum of each feature efficiency)
     */
    public double F4;

    /*
        L1:  Minimized sum of the error distance of a linear classifier (linear SMO)
     */
    public double L1;

    /*
        L2:  Training error of a linear classifier (linear SMO)
     */
    public double L2;

    /*
        L3:  Nonlinearity of a linear classifier (linear SMO)
     */
    public double L3;

    /*
        N1:  Fraction of points on the class boundary
     */
    public double N1;

    /*
        N2:  Ratio of average intra/inter class nearest neighbor distance
     */
    public double N2;

    /*
        N3:  Leave-one-out error rate of the one-nearest neighbor classifier
     */
    public double N3;

    /*
        N4:  Nonlinearity of the one-nearest neighbor classifier
     */
    public double N4;

    /*
        T1:  Fraction of maximum covering spheres
     */
    public double T1;

    /*
        T2:  Average number of points per dimension
     */
    public double T2;

    public double skew;
    public double kurtosis;
    public double multipleCorrelation;
    public double sDRatio;
    
    public List<ClassifierRanking> rankings;

    public ClassifierRanking classifierAUC;
    public ClassifierRanking classifierBalance;
    
    public String getDataSetName() {
        return dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }

    public int getExample() {
        return example;
    }

    public void setExample(int example) {
        this.example = example;
    }

    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    public int getClazz() {
        return clazz;
    }

    public void setClazz(int clazz) {
        this.clazz = clazz;
    }

    public int getBinaryAttributes() {
        return binaryAttributes;
    }

    public void setBinaryAttributes(int binaryAttributes) {
        this.binaryAttributes = binaryAttributes;
    }

    public double getF1() {
        return F1;
    }

    public void setF1(double f1) {
        F1 = f1;
    }

    public double getF1v() {
        return F1v;
    }

    public void setF1v(double f1v) {
        F1v = f1v;
    }

    public double getF2() {
        return F2;
    }

    public void setF2(double f2) {
        F2 = f2;
    }

    public double getF3() {
        return F3;
    }

    public void setF3(double f3) {
        F3 = f3;
    }

    public double getF4() {
        return F4;
    }

    public void setF4(double f4) {
        F4 = f4;
    }

    public double getL1() {
        return L1;
    }

    public void setL1(double l1) {
        L1 = l1;
    }

    public double getL2() {
        return L2;
    }

    public void setL2(double l2) {
        L2 = l2;
    }

    public double getL3() {
        return L3;
    }

    public void setL3(double l3) {
        L3 = l3;
    }

    public double getN1() {
        return N1;
    }

    public void setN1(double n1) {
        N1 = n1;
    }

    public double getN2() {
        return N2;
    }

    public void setN2(double n2) {
        N2 = n2;
    }

    public double getN3() {
        return N3;
    }

    public void setN3(double n3) {
        N3 = n3;
    }

    public double getN4() {
        return N4;
    }

    public void setN4(double n4) {
        N4 = n4;
    }

    public double getT1() {
        return T1;
    }

    public void setT1(double t1) {
        T1 = t1;
    }

    public double getT2() {
        return T2;
    }

    public void setT2(double t2) {
        T2 = t2;
    }

    public double getSkew() {
        return skew;
    }

    public void setSkew(double skew) {
        this.skew = skew;
    }

    public double getKurtosis() {
        return kurtosis;
    }

    public void setKurtosis(double kurtosis) {
        this.kurtosis = kurtosis;
    }

    public double getMultipleCorrelation() {
        return multipleCorrelation;
    }

    public void setMultipleCorrelation(double multipleCorrelation) {
        this.multipleCorrelation = multipleCorrelation;
    }

    public double getsDRatio() {
        return sDRatio;
    }

    public void setsDRatio(double sDRatio) {
        this.sDRatio = sDRatio;
    }

    public List<ClassifierRanking> getRankings() {
        return rankings;
    }

    public void setRankings(List<ClassifierRanking> rankings) {
        this.rankings = rankings;
    }

    public ClassifierRanking getClassifierAUC() {
        return classifierAUC;
    }

    public void setClassifierAUC(ClassifierRanking classifierAUC) {
        this.classifierAUC = classifierAUC;
    }

    public ClassifierRanking getClassifierBalance() {
        return classifierBalance;
    }

    public void setClassifierBalance(ClassifierRanking classifierBalance) {
        this.classifierBalance = classifierBalance;
    }

    

    @Override
    public String toString() {
        return "MetaFeatures{" + "dataSetName=" + dataSetName + ", example=" + example + ", attribute=" + attribute + ", clazz=" + clazz + ", binaryAttributes=" + binaryAttributes + ", F1=" + F1 + ", F1v=" + F1v + ", F2=" + F2 + ", F3=" + F3 + ", F4=" + F4 + ", L1=" + L1 + ", L2=" + L2 + ", L3=" + L3 + ", N1=" + N1 + ", N2=" + N2 + ", N3=" + N3 + ", N4=" + N4 + ", T1=" + T1 + ", T2=" + T2 + ", skew=" + skew + ", kurtosis=" + kurtosis + ", multipleCorrelation=" + multipleCorrelation + ", sDRatio=" + sDRatio + ", rankings=" + rankings + ", classifierAUC=" + classifierAUC + ", classifierBalance=" + classifierBalance + '}';
    }
}
