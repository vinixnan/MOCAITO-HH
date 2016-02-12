/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.selectors;

import hyperheuristics.core.AlgorithmHH;
import hyperheuristics.core.ArchivedAlgorithmHH;
import hyperheuristics.core.HyperHeuristicSelector;
import hyperheuristics.core.PopulationWorks;
import hyperheuristics.metric.HypervolumeCalculator;
import hyperheuristics.metric.R2Calculator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import jmetal.base.SolutionSet;
import jmetal.util.Ranking;

/**
 *
 * @author vinicius
 */
public class HVImpChoice extends HyperHeuristicSelector {

    protected double[] lastHypervolumeValue;
    protected double[] improvementValue;
    protected double[] maximumValues;
    protected double[] minimumValues;

    public HVImpChoice(ArrayList<AlgorithmHH> algs, int numObj, int evaluationsPerTime, int populationsize, double[] maximumValues, double[] minimumValues, int runHH) {
        super(algs, numObj, evaluationsPerTime, populationsize);
        this.lastHypervolumeValue = new double[3];//NSGAII IBEA SPEA2
        this.improvementValue = new double[3];//NSGAII IBEA SPEA2
        this.maximumValues = maximumValues;
        this.minimumValues = minimumValues;
    }

    public double[] calcIndicators(AlgorithmHH algorithm) {
        HypervolumeCalculator hypervolume = new HypervolumeCalculator(numObj);
        R2Calculator r2 = new R2Calculator(numObj);
        SolutionSet sol;
        if (algorithm instanceof ArchivedAlgorithmHH) {
            sol = ((ArchivedAlgorithmHH) algorithm).getArchive();
        } else {
            sol = algorithm.getPopulation();
        }
        Ranking ranking = new Ranking(sol);
        sol = ranking.getSubfront(0);
        sol = PopulationWorks.removeDominadas(sol);
        sol = PopulationWorks.removeRepetidas(sol);
        hypervolume.addParetoFront(sol);
        r2.addParetoFront(sol);
        double hyp = hypervolume.executeWithDefinedMaxValues(sol, minimumValues, maximumValues);
        double r2val = r2.executeWithDefinedMaxValues(sol, minimumValues, maximumValues);
        hypervolume.clear();
        r2.clear();
        double values[] = new double[3];
        values[0] = hyp;
        values[1] = r2val;
        values[2] = sol.size();
        return values;
    }

    protected int getBiggerValue() {
        double bigger = Double.MIN_VALUE;
        int winner = Integer.MIN_VALUE;
        for (int i = 0; i < this.getAlgs().size(); i++) {
            AlgorithmHH algorithm = this.algs.get(i);
            double value = this.improvementValue[i] + (algorithm.getRoundEstimatedTime() * 0.01);
            if (bigger < value) {
                bigger = value;
                winner = i;
            }
        }
        if (winner == Integer.MIN_VALUE) {
            //Case there is no improvement
            Random r = new Random();
            winner = r.nextInt(this.algs.size());
        }
        return winner;
    }

    public void updateHypervolume(AlgorithmHH algorithm) {
        double values[] = this.calcIndicators(algorithm);
        double _new = values[0];
        int posAlg = this.algs.indexOf(algorithm);
        double _old = lastHypervolumeValue[posAlg];
        improvementValue[posAlg] = ((_new - _old) / (Math.pow(1 - _old, 2)));
        lastHypervolumeValue[posAlg] = _new;
    }

    public void updateLastHypervolume(int pos, double value) {
        lastHypervolumeValue[pos] = value;
    }

    @Override
    public AlgorithmHH chooseAlg() {
        System.err.println(Arrays.toString(this.improvementValue));
        System.err.println(Arrays.toString(this.lastHypervolumeValue));
        int pos = this.getBiggerValue();
        return this.selectAlg(pos);
    }
}
