/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.metric;

import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import jmetal.qualityIndicator.R2;

/**
 *
 * @author vinicius
 */
public class R2Calculator extends Calculator {

    private final R2 r2;

    private final int numberOfObjectives;

    private final Solution referencePoint;

    public R2Calculator(int numberOfObjectives) {
        this(numberOfObjectives, 0);
    }

    public R2Calculator(int numberOfObjectives, double offset) {
        this.numberOfObjectives = numberOfObjectives;
        this.r2 = new R2();
        referencePoint = new Solution(numberOfObjectives);
        for (int i = 0; i < numberOfObjectives; i++) {
            referencePoint.setObjective(i, offset);
        }
    }

    @Override
    public double execute(String frontPath) {
        return execute(metricsUtil.readNonDominatedSolutionSet(frontPath));
    }

    @Override
    public double execute(SolutionSet front) {
        if (internalPopulation.size() != 0) {
            double[] maximumValues = metricsUtil.getMaximumValues(internalPopulation.writeObjectivesToMatrix(), numberOfObjectives);
            double[] minimumValues = metricsUtil.getMinimumValues(internalPopulation.writeObjectivesToMatrix(), numberOfObjectives);
            double[][] normalizedFront = metricsUtil.getNormalizedFront(front.writeObjectivesToMatrix(), maximumValues, minimumValues);
            return r2.calculate(normalizedFront);
        }
        return 0D;
    }

    public double executeWithDefinedMaxValues(SolutionSet front, double[] minimumValues, double[] maximumValues) {
        if (internalPopulation.size() != 0) {
            double[][] normalizedFront = metricsUtil.getNormalizedFront(front.writeObjectivesToMatrix(), maximumValues, minimumValues);
            return r2.calculate(normalizedFront);
        }
        return 0D;
    }
}
