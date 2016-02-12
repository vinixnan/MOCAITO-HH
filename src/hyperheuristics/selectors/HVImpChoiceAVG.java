/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.selectors;

import hyperheuristics.core.AlgorithmHH;
import java.util.ArrayList;

/**
 *
 * @author vinicius
 */
public class HVImpChoiceAVG extends HVImpChoice {

    protected ArrayList<ArrayList<Double>> allImprovement;
    protected ArrayList<ArrayList<Double>> allHypValues;

    public HVImpChoiceAVG(ArrayList<AlgorithmHH> algs, int numObj, int evaluationsPerTime, int populationsize, double[] maximumValues, double[] minimumValues, int runHH) {
        super(algs, numObj, evaluationsPerTime, populationsize, maximumValues, minimumValues, runHH);
        this.allImprovement = new ArrayList<>();
        this.allImprovement.add(new ArrayList<Double>());
        this.allImprovement.add(new ArrayList<Double>());
        this.allImprovement.add(new ArrayList<Double>());
        this.allHypValues = new ArrayList<>();
        this.allHypValues.add(new ArrayList<Double>());
        this.allHypValues.add(new ArrayList<Double>());
        this.allHypValues.add(new ArrayList<Double>());
    }

    @Override
    public void updateLastHypervolume(int pos, double value) {
        this.allHypValues.get(pos).add(value);
    }

    @Override
    public void updateHypervolume(AlgorithmHH algorithm) {
        double values[] = this.calcIndicators(algorithm);
        double _new = values[0];
        int posAlg = this.algs.indexOf(algorithm);
        double _old = this.allHypValues.get(posAlg).get(this.allHypValues.get(posAlg).size() - 1);
        double imp = ((_new - _old) / (Math.pow(1 - _old, 2)));
        this.allImprovement.get(posAlg).add(imp);
        ArrayList<Double> allimpalg = this.allImprovement.get(posAlg);
        double sum = 0;
        int size = allimpalg.size();
        for (Double impalg : allimpalg) {
            sum += impalg;
        }
        improvementValue[posAlg] = sum / size;
        this.allHypValues.get(posAlg).add(_new);
    }

}
