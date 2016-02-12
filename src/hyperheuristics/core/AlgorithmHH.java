/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.core;

import jmetal.base.Algorithm;
import jmetal.base.Operator;
import jmetal.base.Problem;
import jmetal.base.SolutionSet;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Distance;
import jmetal.util.JMException;

/**
 *
 * @author vinicius
 */
public abstract class AlgorithmHH extends Algorithm implements Comparable<AlgorithmHH> {

    protected String methodName;
    protected SolutionSet population;

    protected Operator mutationOperator;
    protected Operator crossoverOperator;
    protected Operator selectionOperator;

    protected int populationSize;
    protected int maxEvaluations;
    protected int evaluations;
    protected int requiredEvaluations;
    protected Distance distance;
    protected QualityIndicator indicators;

    protected long initWaitTime;
    protected long endWaitTime;
    protected int roundEstimatedTime;

    private int algranking;
    private double cf;
    
    protected Problem problem_;

    /**
     * Configure and init population.
     *
     * @throws jmetal.util.JMException
     */
    public abstract void executeMethod() throws JMException;
    
    /**
     * Configure and init population.
     */
    public abstract void initPopulation();
    
    
    public AlgorithmHH(Problem problem) {
        problem_=problem;
        this.algranking = 1;
        this.cf = 0;
        this.endWaitTime=0;
        this.initWaitTime=0;
        this.roundEstimatedTime=0;
    }

    public int getAlgranking() {
        return algranking;
    }

    public void setAlgranking(int algranking) {
        this.algranking = algranking;
    }

    public double getCf() {
        return cf;
    }

    public void setCf(double cf) {
        this.cf = cf;
    }

    @Override
    public int compareTo(AlgorithmHH o) {
        //maior hypervolume
        if (this.getCf() > o.getCf()) {
            return -1;
        } else if (this.getCf() < o.getCf()) {
            return 1;
        }
        return 0;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    protected SolutionSet clonePopulation(SolutionSet pop) {
        return pop.union(new SolutionSet());
    }

    public SolutionSet getPopulation() {
        return this.clonePopulation(population);
    }

    public void setPopulation(SolutionSet population) {
        this.population = this.clonePopulation(population);
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public int getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(int evaluations) {
        this.evaluations = evaluations;
    }

    public int getRequiredEvaluations() {
        return requiredEvaluations;
    }

    public void setRequiredEvaluations(int requiredEvaluations) {
        this.requiredEvaluations = requiredEvaluations;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public double getEstimatedTime() {
        long t=this.endWaitTime-this.initWaitTime;
        double time=((double)(t))/1000.0;
        return (time);//converter para segundos
    }

    public int getRoundEstimatedTime() {
        return roundEstimatedTime;
    }
    
    public void restartEstimatedTime() {
        this.initWaitTime=System.currentTimeMillis();
        this.endWaitTime=0;
    }
    
    public void endTimeCount(){
        this.endWaitTime=System.currentTimeMillis();
    }
    
    public void incrementRound(){
        this.roundEstimatedTime++;
    }
    
    public int getMaxEvaluations() {
        return maxEvaluations;
    }
    
    public void noTimeCount(){
        this.endWaitTime=0;
        this.initWaitTime=0;
        this.roundEstimatedTime=0;
    }

    public void setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
    }
    
    public SolutionSet getNonDominatedPopulation(){
        SolutionSet resp=PopulationWorks.removeDominadas(population);
        resp=PopulationWorks.removeRepetidas(resp);
        resp.setCapacity(this.populationSize);
        return resp;
    }
}
