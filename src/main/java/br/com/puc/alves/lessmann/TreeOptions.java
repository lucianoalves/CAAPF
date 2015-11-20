/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.lessmann;

/**
 *
 * @author luciano
 */
public class TreeOptions {
    private boolean pruned;
    private float confidenceLevel;
    private boolean laplacian;
    private boolean subtree;

    public TreeOptions(boolean pruned, float confidenceLevel, boolean laplacian, boolean subtree) {
        this.pruned = pruned;
        this.confidenceLevel = confidenceLevel;
        this.laplacian = laplacian;
        this.subtree = subtree;
    }

    public boolean isPruned() {
        return pruned;
    }

    public void setPruned(boolean pruned) {
        this.pruned = pruned;
    }

    public float getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(float confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public boolean isLaplacian() {
        return laplacian;
    }

    public void setLaplacian(boolean laplacian) {
        this.laplacian = laplacian;
    }

    public boolean isSubtree() {
        return subtree;
    }

    public void setSubtree(boolean subtree) {
        this.subtree = subtree;
    }
}
