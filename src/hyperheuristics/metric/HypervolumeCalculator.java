package hyperheuristics.metric;

import jmetal.base.Solution;
import jmetal.base.SolutionSet;
import jmetal.qualityIndicator.Hypervolume;

/**
 * This class was designed to help the calculation of Hypervolume. The idea is
 * to add all available Pareto fronts into an <b>internal population</b>, in
 * order to perform an exact normalization. This internal population is used for
 * assessing the max and min values for the normalization. It is useful when you
 * don't know what are these values and must use the max known and min known
 * values.
 * <br/>
 * <br/>
 * If the intention is to compare the fronts A, B and C, then the
 * addParetoFront() method must be invoked three times giving A, B and then C as
 * input.
 */
public class HypervolumeCalculator extends Calculator {

    private final Hypervolume hypervolume;

    private final int numberOfObjectives;

    private final Solution referencePoint;

    /**
     * Constructor for the hypervolume calculator.
     *
     * @param numberOfObjectives the number of objectives for the problem.
     */
    public HypervolumeCalculator(int numberOfObjectives) {
        this(numberOfObjectives, 0.01);
    }

    /**
     * Constructor for the calculator.
     *
     * @param numberOfObjectives the number of objectives for the problem.
     * @param offset offset used for the reference point.
     */
    public HypervolumeCalculator(int numberOfObjectives, double offset) {
        this.numberOfObjectives = numberOfObjectives;
        this.hypervolume = new Hypervolume();

        referencePoint = new Solution(numberOfObjectives);
        for (int i = 0; i < numberOfObjectives; i++) {
            referencePoint.setObjective(i, 1D + offset);
        }
    }

    /**
     * Calculates the hypervolume for the given front. This method uses the
     * internal population as basis for assessing the min and max values in the
     * normalization.
     *
     * @param frontPath path of the Pareto front to be evaluated.
     * @return the hypervolume value for the given Pareto front.
     */
    @Override
    public double execute(String frontPath) {
        return execute(metricsUtil.readNonDominatedSolutionSet(frontPath));
    }

    /**
     * Calculates the hypervolume for the given front. This method uses the
     * internal population as basis for assessing the min and max values in the
     * normalization.
     *
     * @param front Pareto front to be evaluated.
     * @return the hypervolume value for the given Pareto front.
     */
    @Override
    public double execute(SolutionSet front) {
        if (internalPopulation.size() != 0) {
            double[][] referencePoint = getReferencePoint(numberOfObjectives);
            double[] maximumValues = metricsUtil.getMaximumValues(internalPopulation.writeObjectivesToMatrix(), numberOfObjectives);
            double[] minimumValues = metricsUtil.getMinimumValues(internalPopulation.writeObjectivesToMatrix(), numberOfObjectives);
            double[][] objectives = metricsUtil.getNormalizedFront(front.writeObjectivesToMatrix(), maximumValues, minimumValues);
            return hypervolume.hypervolume(objectives, referencePoint, numberOfObjectives);
        }
        return 0D;
    }

    public double executeWithDefinedMaxValues(SolutionSet front, double[] minimumValues, double[] maximumValues) {
        double[][] referencePoint = getReferencePoint(numberOfObjectives);
        double[][] objectives = metricsUtil.getNormalizedFront(front.writeObjectivesToMatrix(), maximumValues, minimumValues);
        return hypervolume.hypervolume(objectives, referencePoint, numberOfObjectives);
    }
    
    private double[][] getReferencePoint(int numberOfObjectives) {
        double[][] referencePoint = new double[numberOfObjectives][numberOfObjectives];
        for (int i = 0; i < referencePoint.length; i++) {
            double[] objective = referencePoint[i];
            objective[i] = 1.01;
            for (int j = 0; j < objective.length; j++) {
                if (i != j) {
                    objective[j] = 0;
                }
            }
        }
        return referencePoint;
    }
}
