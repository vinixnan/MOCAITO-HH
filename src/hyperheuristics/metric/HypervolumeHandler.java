package hyperheuristics.metric;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import jmetal.base.SolutionSet;
import jmetal.qualityIndicator.Hypervolume;

/**
 *
 * @author giovaniguizzo
 */
public class HypervolumeHandler extends MetricHandler {

    private final Hypervolume hypervolume;

    public HypervolumeHandler(int numObj) {
        super(numObj);
        this.hypervolume = new Hypervolume();
    }

    

    public double calculate(SolutionSet a, SolutionSet b) {
        double value = 0;
        if (a != null && a.size() != 0) {
            double[][] referencePoint = new double[numObj][numObj];
            referencePoint[0][0] = 2;
            referencePoint[1][1] = 4;

            SolutionSet a_b = a.union(b);

            double[][] objectivesA = a.writeObjectivesToMatrix();
            double[][] objectivesB = b.writeObjectivesToMatrix();
            double[][] objectivesAB = a_b.writeObjectivesToMatrix();

            double valueA = hypervolume.hypervolume(objectivesA, referencePoint, this.numObj);
            double valueB = hypervolume.hypervolume(objectivesB, referencePoint, this.numObj);
            double valueAB = hypervolume.hypervolume(objectivesAB, referencePoint, this.numObj);

            double aba = valueAB - valueA;
            double abb = valueAB - valueB;

            if (aba > abb) {
                return aba;
            } else {
                return abb;
            }
        }
        return value;
    }

    public void WFGHypervolume(double value, SolutionSet newfront, SolutionSet oldfront) {
        System.out.println(value);
        newfront.printObjectivesToFile("resultado/front.txt");
        System.exit(0);
    }

    public double calculateDMetric(SolutionSet a, SolutionSet b) {
        double value = 0;
        this.clear();
        if (a != null && a.size() != 0) {
            double[][] referencePoint = this.getReferencePoint(numObj);
            SolutionSet a_b = a.union(b);
            double valueB = 0;
            double[][] objectivesB;
            if (b.size() != 0) {
                objectivesB = b.writeObjectivesToMatrix();
                normalizeObjecties(objectivesB, this.numObj, a_b);
                valueB = hypervolume.hypervolume(objectivesB, referencePoint, this.numObj);
            }
            double[][] objectivesAB = a_b.writeObjectivesToMatrix();
            normalizeObjecties(objectivesAB, this.numObj, a_b);
            //double valueA = hypervolume.hypervolume(objectivesA, referencePoint, this.numObj);

            double valueAB = hypervolume.hypervolume(objectivesAB, referencePoint, this.numObj);

            value = valueAB - valueB;

        }
        return value;
    }

    public double calculate(SolutionSet front, double[] minimumValues, double[] maximumValues) {
        double[][] normalizedFront = metricUtil.getNormalizedFront(front.writeObjectivesToMatrix(), maximumValues, minimumValues);
        double[][] truePareto = new double[numObj][numObj];
        double[][] referencePoint = getReferencePoint(numObj);
        double[][] objectives = metricUtil.getNormalizedFront(front.writeObjectivesToMatrix(), maximumValues, minimumValues);
        return hypervolume.hypervolume(objectives, referencePoint, numObj);
    }
    
    @Override
    public double calculate(SolutionSet front) {
        if (population.size() != 0) {
            double[][] referencePoint = getReferencePoint(numObj);
            double[] maximumValues = metricUtil.getMaximumValues(population.writeObjectivesToMatrix(), numObj);
            double[] minimumValues = metricUtil.getMinimumValues(population.writeObjectivesToMatrix(), numObj);
            double[][] objectives = metricUtil.getNormalizedFront(front.writeObjectivesToMatrix(), maximumValues, minimumValues);
            return hypervolume.hypervolume(objectives, referencePoint, numObj);
        }
        return 0D;
    }
}
