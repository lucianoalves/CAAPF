/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.puc.alves.meta;

import org.apache.log4j.Logger;

/**
 *
 * @author ssad
 */
public class RankingMajoritaryBean implements Comparable<RankingMajoritaryBean> {
    final static Logger logger = Logger.getLogger(RankingMajoritaryBean.class);
    private String ranking;
    private int count;

    public RankingMajoritaryBean(String ranking) {
        this.ranking = ranking;
        this.count = 1;
    }
    
    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int compareTo(RankingMajoritaryBean o) {
        logger.debug("Objeto 1 "+this.count);
        logger.debug("Objeto 2 "+o.count);
        if (this.count == o.count) {
            return 0;
        }
        else if (this.count >= o.count) {
            return -1;
        } else {
            return 1;
        }
    }
    
    
}
