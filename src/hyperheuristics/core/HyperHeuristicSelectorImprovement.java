/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.core;

import hyperheuristics.metric.MetricHandler;
import java.util.ArrayList;
import jmetal.base.SolutionSet;

/**
 *
 * @author vinicius
 */
public class HyperHeuristicSelectorImprovement extends HyperHeuristicSelector {

    public HyperHeuristicSelectorImprovement(ArrayList<AlgorithmHH> algs, int numObj, int evaluationsPerTime, int populationsize) {
        super(algs, numObj, evaluationsPerTime, populationsize);
    }

    @Override
    protected double calcImprovement(AlgorithmHH alg, MetricHandler metric, SolutionSet allPop) {
        double newH=this.calcAtualMetricValue(alg, metric, allPop);
        double oldH=this.calcOldMetricValue(alg, metric, allPop);
        return (newH-oldH)/newH;
    }

    @Override
    protected double calcOldMetricValue(AlgorithmHH alg, MetricHandler metric, SolutionSet allPop) {
        metric.clear();
        metric.addParetoFront(allPop);
        metric.addParetoFront(alg.getNonDominatedPopulation());
        metric.addParetoFront(this.olderPopulation.get(alg));
        double oldH = metric.calculate(this.olderPopulation.get(alg));
        return oldH;
    }

    @Override
    protected double calcAtualMetricValue(AlgorithmHH alg, MetricHandler metric, SolutionSet allPop) {
        metric.clear();
        metric.addParetoFront(allPop);
        metric.addParetoFront(alg.getNonDominatedPopulation());
        metric.addParetoFront(this.olderPopulation.get(alg));
        double newH = metric.calculate(alg.getNonDominatedPopulation());//VER
        return newH;
    }
}
