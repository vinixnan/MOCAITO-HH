/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.core;

/**
 *
 * @author vinicius
 */
public class AlgorithmRanking implements Comparable<AlgorithmRanking> {

    private Improvement imp;
    private AlgorithmHH alg;
    private double value;
    private int ranking;
    private int numObj;

    public AlgorithmRanking(Improvement imp, AlgorithmHH alg, int numObj) {
        this.imp = imp;
        this.alg = alg;
        this.numObj = numObj;
    }

    public void calcMetric() {
        if (this.imp != null) {
            this.value = this.imp.getLastValue();
        } else {
            this.value = 0;
        }
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public AlgorithmHH getAlg() {
        return alg;
    }

    @Override
    public int compareTo(AlgorithmRanking o) {
        //maior hypervolume
        if (this.getValue() > o.getValue()) {
            return -1;
        } else if (this.getValue() < o.getValue()) {
            return 1;
        }
        return 0;
    }

    public Improvement getImp() {
        return imp;
    }

    
}
