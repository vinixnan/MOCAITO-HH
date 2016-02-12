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
public class RNI extends MetricHandler {

    private int populationSize;
    public RNI(int numObj, int populationSize) {
        super(numObj);
        this.populationSize=populationSize;
    }

    @Override
    public double calculate(SolutionSet front) {
        double toReturn=0;
        if(front.size() > 0)
            toReturn= ((double)front.size()/(double)this.populationSize);
        if(toReturn > 1.0)
            toReturn=1.0;
        return toReturn;
    }
}
