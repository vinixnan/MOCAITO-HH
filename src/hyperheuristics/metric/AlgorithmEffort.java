/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.metric;

import jmetal.base.SolutionSet;

/**
 *
 * @author vinicius
 */
public class AlgorithmEffort extends MetricHandler {

    private long timeExpend;
    private int evaluations;

    public AlgorithmEffort(int numObj) {
        super(numObj);
        this.timeExpend = 0;
        this.evaluations = 0;
    }

    @Override
    public double calculate(SolutionSet front) {

        if (this.evaluations > 0 && timeExpend > 0) {
            return (((double) timeExpend) / ((double) this.evaluations)) * (-1);//para nao mudar o if, pois o menor tempo é melhor
        }
        return 0D;
    }

    public long getTimeExpend() {
        return timeExpend;
    }

    public void setTimeExpend(long timeExpend) {
        this.timeExpend = timeExpend;
    }

    public int getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(int evaluations) {
        this.evaluations = evaluations;
    }

    
}
