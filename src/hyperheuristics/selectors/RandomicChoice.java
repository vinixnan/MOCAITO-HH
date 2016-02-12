/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.selectors;

import hyperheuristics.core.AlgorithmHH;
import hyperheuristics.core.HyperHeuristicSelector;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author vinicius
 */
public class RandomicChoice extends HyperHeuristicSelector {

    public RandomicChoice(ArrayList<AlgorithmHH> algs, int numObj, int evaluationsPerTime, int populationsize) {
        super(algs, numObj, evaluationsPerTime, populationsize);
    }

    @Override
    public AlgorithmHH chooseAlg() {
        Random gerador = new Random();
        int pos = gerador.nextInt(this.algs.size());
        AlgorithmHH alg = this.algs.get(pos);
        return this.selectAlg(pos);
    }
}
