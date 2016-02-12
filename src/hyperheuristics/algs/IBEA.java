//  IBEA.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
package hyperheuristics.algs;

import jmetal.base.*;
import jmetal.util.JMException;
import jmetal.util.Ranking;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import hyperheuristics.core.ArchivedAlgorithmHH;
import jmetal.base.operator.comparator.DominanceComparator;
import jmetal.problems.CITO_CAITO;

/**
 * This class implementing the IBEA algorithm
 */
public class IBEA extends ArchivedAlgorithmHH {

    /**
     * Defines the number of tournaments for creating the mating pool
     */
    public static final int TOURNAMENTS_ROUNDS = 1;

    /**
     * Stores the value of the indicator between each pair of solutions into the
     * solution set
     */
    private List<List<Double>> indicatorValues_;

    /**
     *
     */
    private double maxIndicatorValue_;

    /**
     * Constructor. Create a new IBEA instance
     *
     * @param problem Problem to solve
     */
    public IBEA(Problem problem) {
        super(problem);
        this.methodName="IBEA";
    } // Spea2

    /**
     * calculates the hypervolume of that portion of the objective space that is
     * dominated by individual a but not by individual b
     */
    double calcHypervolumeIndicator(Solution p_ind_a,
            Solution p_ind_b,
            int d,
            double maximumValues[],
            double minimumValues[]) {
        double a, b, r, max;
        double volume = 0;
        double rho = 2.0;

        r = rho * (maximumValues[d - 1] - minimumValues[d - 1]);
        max = minimumValues[d - 1] + r;

        a = p_ind_a.getObjective(d - 1);
        if (p_ind_b == null) {
            b = max;
        } else {
            b = p_ind_b.getObjective(d - 1);
        }

        if (d == 1) {
            if (a < b) {
                volume = (b - a) / r;
            } else {
                volume = 0;
            }
        } else {
            if (a < b) {
                volume = calcHypervolumeIndicator(p_ind_a, null, d - 1, maximumValues, minimumValues)
                        * (b - a) / r;
                volume += calcHypervolumeIndicator(p_ind_a, p_ind_b, d - 1, maximumValues, minimumValues)
                        * (max - b) / r;
            } else {
                volume = calcHypervolumeIndicator(p_ind_a, p_ind_b, d - 1, maximumValues, minimumValues)
                        * (max - a) / r;
            }
        }

        return (volume);
    }

    /**
     * This structure store the indicator values of each pair of elements
     */
    public void computeIndicatorValuesHD(SolutionSet population,
            double[] maximumValues,
            double[] minimumValues) {
        SolutionSet A, B;
        // Initialize the structures
        indicatorValues_ = new ArrayList<List<Double>>();
        maxIndicatorValue_ = -Double.MAX_VALUE;

        for (int j = 0; j < population.size(); j++) {
            A = new SolutionSet(1);
            A.add(population.get(j));

            List<Double> aux = new ArrayList<Double>();
            for (int i = 0; i < population.size(); i++) {
                B = new SolutionSet(1);
                B.add(population.get(i));

                int flag = (new DominanceComparator()).compare(A.get(0), B.get(0));

                double value = 0.0;
                if (flag == -1) {
                    value = -calcHypervolumeIndicator(A.get(0), B.get(0), problem_.getNumberOfObjectives(), maximumValues, minimumValues);
                } else {
                    value = calcHypervolumeIndicator(B.get(0), A.get(0), problem_.getNumberOfObjectives(), maximumValues, minimumValues);
                }
        //double value = epsilon.epsilon(matrixA,matrixB,problem_.getNumberOfObjectives());

                //Update the max value of the indicator
                if (Math.abs(value) > maxIndicatorValue_) {
                    maxIndicatorValue_ = Math.abs(value);
                }
                aux.add(value);
            }
            indicatorValues_.add(aux);
        }
    } // computeIndicatorValues

    /**
     * Calculate the fitness for the individual at position pos
     */
    public void fitness(SolutionSet population, int pos) {
        double fitness = 0.0;
        double kappa = 0.05;

        for (int i = 0; i < population.size(); i++) {
            if (i != pos) {
                fitness += Math.exp((-1 * indicatorValues_.get(i).get(pos) / maxIndicatorValue_) / kappa);
            }
        }
        population.get(pos).setFitness(fitness);
    }

    /**
     * Calculate the fitness for the entire pop.
     *
     */
    public void calculateFitness(SolutionSet population) {
        // Obtains the lower and upper bounds of the pop
        double[] maximumValues = new double[problem_.getNumberOfObjectives()];
        double[] minimumValues = new double[problem_.getNumberOfObjectives()];

        for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
            maximumValues[i] = -Double.MAX_VALUE; // i.e., the minus maxium value
            minimumValues[i] = Double.MAX_VALUE; // i.e., the maximum value
        }

        for (int pos = 0; pos < population.size(); pos++) {
            for (int obj = 0; obj < problem_.getNumberOfObjectives(); obj++) {
                double value = population.get(pos).getObjective(obj);
                if (value > maximumValues[obj]) {
                    maximumValues[obj] = value;
                }
                if (value < minimumValues[obj]) {
                    minimumValues[obj] = value;
                }
            }
        }

        computeIndicatorValuesHD(population, maximumValues, minimumValues);
        for (int pos = 0; pos < population.size(); pos++) {
            fitness(population, pos);
        }
    }

    /**
     * Update the fitness before removing an individual
     */
    public void removeWorst(SolutionSet population) {

        // Find the worst;
        double worst = population.get(0).getFitness();
        int worstIndex = 0;
        double kappa = 0.05;

        for (int i = 1; i < population.size(); i++) {
            if (population.get(i).getFitness() > worst) {
                worst = population.get(i).getFitness();
                worstIndex = i;
            }
        }

        //if (worstIndex == -1) {
        //    System.out.println("Yes " + worst);
        //}
        //System.out.println("Solution Size "+population.size());
        //System.out.println(worstIndex);
        // Update the pop
        for (int i = 0; i < population.size(); i++) {
            if (i != worstIndex) {
                double fitness = population.get(i).getFitness();
                fitness -= Math.exp((-indicatorValues_.get(worstIndex).get(i) / maxIndicatorValue_) / kappa);
                population.get(i).setFitness(fitness);
            }
        }

        // remove worst from the indicatorValues list
        indicatorValues_.remove(worstIndex); // Remove its own list
        Iterator<List<Double>> it = indicatorValues_.iterator();
        while (it.hasNext()) {
            it.next().remove(worstIndex);
        }

        // remove the worst individual from the pop
        population.remove(worstIndex);
    } // removeWorst

    /**
     * Runs of the IBEA algorithm.
     *
     * @return a <code>SolutionSet</code> that is a set of non dominated
     * solutions as a result of the algorithm execution
     * @throws JMException
     */
    @Override
    public SolutionSet execute() throws JMException {
        this.initPopulation();
        while (evaluations < maxEvaluations) {
            this.executeMethod();
        } // while

        Ranking ranking = new Ranking(archive);
        return ranking.getSubfront(0);
    } // execute

    @Override
    public void executeMethod() throws JMException {
        // Create a new offspringPopulation
        SolutionSet union = ((SolutionSet) population).union(archive);
        calculateFitness(union);
        archive = union;
        while (archive.size() > populationSize) {
            removeWorst(archive);
        }
        SolutionSet offSpringSolutionSet = new SolutionSet(populationSize);
        Solution[] parents = new Solution[2];
        while (offSpringSolutionSet.size() < populationSize) {
            int j = 0;
            do {
                j++;
                parents[0] = (Solution) selectionOperator.execute(archive, (CITO_CAITO) problem_);
            } while (j < IBEA.TOURNAMENTS_ROUNDS); // do-while
            int k = 0;
            do {
                k++;
                parents[1] = (Solution) selectionOperator.execute(archive,(CITO_CAITO) problem_);
            } while (k < IBEA.TOURNAMENTS_ROUNDS); // do-while

            //make the crossover
            Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents,(CITO_CAITO) problem_);
            mutationOperator.execute(offSpring[0],(CITO_CAITO) problem_);
            problem_.evaluate(offSpring[0]);
            problem_.evaluateConstraints(offSpring[0]);
            offSpringSolutionSet.add(offSpring[0]);
            evaluations++;
        } // while
        // End Create a offSpring population
        population = offSpringSolutionSet;
    }

    @Override
    public void initPopulation() {

        //Read the params
        this.populationSize = ((Integer) getInputParameter("populationSize")).intValue();
        archiveSize = ((Integer) getInputParameter("archiveSize")).intValue();
        maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();

        //Read the operators
        crossoverOperator = operators_.get("crossover");
        mutationOperator = operators_.get("mutation");
        selectionOperator = operators_.get("selection");

        //Initialize the variables
        population = new SolutionSet(populationSize);
        archive = new SolutionSet(archiveSize);
        evaluations = 0;

        //-> Create the initial population
        Solution newSolution;
        for (int i = 0; i < populationSize; i++) {
            try {
                newSolution = new Solution((CITO_CAITO) problem_);
                problem_.evaluate(newSolution);
                problem_.evaluateConstraints(newSolution);
                evaluations++;
                population.add(newSolution);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(IBEA.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JMException ex) {
                Logger.getLogger(IBEA.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        SolutionSet union = ((SolutionSet) population).union(archive);
        calculateFitness(union);
        archive = union;
        while (archive.size() > populationSize) {
            removeWorst(archive);
        }
    }
} // IBEA
