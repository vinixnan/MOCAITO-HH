//  MOEAD.java
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

import hyperheuristics.core.AlgorithmHH;
import jmetal.base.*;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.metaheuristics.moead.Utils;
import jmetal.problems.CITO_CAITO;

public class Genetic_MOEAD extends AlgorithmHH {

    /**
     * Z vector (ideal point)
     */
    double[] z_;
    /**
     * Lambda vectors
     */
    //Vector<Vector<Double>> lambda_ ; 
    double[][] lambda_;
    /**
     * T: neighbour size
     */
    int T_;
    /**
     * Neighborhood
     */
    int[][] neighborhood_;
    /**
     * delta: probability that parent solutions are selected from neighbourhood
     */
    double delta_;
    /**
     * nr: maximal number of solutions replaced by each child solution
     */
    int nr_;
    Solution[] indArray_;
    String functionType_;

    String dataDirectory_;

    /**
     * Constructor
     *
     * @param problem Problem to solve
     */
    public Genetic_MOEAD(Problem problem) {
        super(problem);
        functionType_ = "_TCHE1";
        this.methodName = "MOEA/D";
    }

    /**
     * initUniformWeight
     */
    public void initUniformWeight() {
        if ((problem_.getNumberOfObjectives() == 2) && (populationSize <= 300)) {
            for (int n = 0; n < populationSize; n++) {
                double a = 1.0 * n / (populationSize - 1);
                lambda_[n][0] = a;
                lambda_[n][1] = 1 - a;
            } // for
        } // if
        else {
            String dataFileName;
            dataFileName = "W" + problem_.getNumberOfObjectives() + "D_"
                    + populationSize + ".dat";

            try {
                // Open the file
                FileInputStream fis = new FileInputStream(dataDirectory_ + "/" + dataFileName);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);

                int numberOfObjectives = 0;
                int i = 0;
                int j = 0;
                String aux = br.readLine();
                while (aux != null) {
                    StringTokenizer st = new StringTokenizer(aux);
                    j = 0;
                    numberOfObjectives = st.countTokens();
                    while (st.hasMoreTokens()) {
                        double value = (new Double(st.nextToken())).doubleValue();
                        lambda_[i][j] = value;
                        //System.out.println("lambda["+i+","+j+"] = " + value) ;
                        j++;
                    }
                    aux = br.readLine();
                    i++;
                }
                br.close();
            } catch (Exception e) {
                System.out.println("initUniformWeight: failed when reading for file: " + dataDirectory_ + "/" + dataFileName);
                e.printStackTrace();
            }
        } // else

        //System.exit(0) ;
    } // initUniformWeight

    /**
     *
     */
    public void initNeighborhood() {
        double[] x = new double[populationSize];
        int[] idx = new int[populationSize];

        for (int i = 0; i < populationSize; i++) {
            // calculate the distances based on weight vectors
            for (int j = 0; j < populationSize; j++) {
                x[j] = Utils.distVector(lambda_[i], lambda_[j]);
                //x[j] = dist_vector(population[i].namda,population[j].namda);
                idx[j] = j;
                //System.out.println("x["+j+"]: "+x[j]+ ". idx["+j+"]: "+idx[j]) ;
            } // for

            // find 'niche' nearest neighboring subproblems
            Utils.minFastSort(x, idx, populationSize, T_);
            //minfastsort(x,idx,population.size(),niche);

            System.arraycopy(idx, 0, neighborhood_[i], 0, T_);
        } // for
    } // initNeighborhood

    /**
     *
     */
    public void initPopulationMOEAD() throws JMException, ClassNotFoundException {
        for (int i = 0; i < populationSize; i++) {
            Solution newSolution = new Solution((CITO_CAITO) problem_);

            problem_.evaluate(newSolution);
            evaluations++;
            population.add(newSolution);
        } // for
    } // initPopulationMOEAD

    /**
     *
     */
    void initIdealPoint() throws JMException, ClassNotFoundException {
        for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
            z_[i] = 1.0e+30;
            indArray_[i] = new Solution((CITO_CAITO) problem_);
            problem_.evaluate(indArray_[i]);
            evaluations++;
        } // for

        for (int i = 0; i < populationSize; i++) {
            updateReference(population.get(i));
        } // for
    } // initIdealPoint

    /**
     *
     */
    public void matingSelection(Vector<Integer> list, int cid, int size, int type) {
        // list : the set of the indexes of selected mating parents
        // cid  : the id of current subproblem
        // size : the number of selected mating parents
        // type : 1 - neighborhood; otherwise - whole population
        int ss;
        int r;
        int p;

        ss = neighborhood_[cid].length;
        while (list.size() < size) {
            if (type == 1) {
                r = PseudoRandom.randInt(0, ss - 1);
                p = neighborhood_[cid][r];
                //p = population[cid].table[r];
            } else {
                p = PseudoRandom.randInt(0, populationSize - 1);
            }
            boolean flag = true;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == p) // p is in the list
                {
                    flag = false;
                    break;
                }
            }

            //if (flag) list.push_back(p);
            if (flag) {
                list.addElement(p);
            }
        }
    } // matingSelection

    /**
     *
     * @param individual
     */
    void updateReference(Solution individual) {
        for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
            if (individual.getObjective(n) < z_[n]) {
                z_[n] = individual.getObjective(n);

                indArray_[n] = individual;
            }
        }
    } // updateReference

    /**
     * @param individual
     * @param id
     * @param type
     */
    void updateProblem(Solution indiv, int id, int type) {
        // indiv: child solution
        // id:   the id of current subproblem
        // type: update solutions in - neighborhood (1) or whole population (otherwise)
        int size;
        int time;

        time = 0;

        if (type == 1) {
            size = neighborhood_[id].length;
        } else {
            size = population.size();
        }
        int[] perm = new int[size];

        Utils.randomPermutation(perm, size);

        for (int i = 0; i < size; i++) {
            int k;
            if (type == 1) {
                k = neighborhood_[id][perm[i]];
            } else {
                k = perm[i];      // calculate the values of objective function regarding the current subproblem
            }
            double f1, f2;

            f1 = fitnessFunction(population.get(k), lambda_[k]);
            f2 = fitnessFunction(indiv, lambda_[k]);

            if (f2 < f1) {
                population.replace(k, new Solution(indiv));
                //population[k].indiv = indiv;
                time++;
            }
            // the maximal number of solutions updated is not allowed to exceed 'limit'
            if (time >= nr_) {
                return;
            }
        }
    } // updateProblem

    double fitnessFunction(Solution individual, double[] lambda) {
        double fitness;
        fitness = 0.0;

        if (functionType_.equals("_TCHE1")) {
            double maxFun = -1.0e+30;

            for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
                double diff = Math.abs(individual.getObjective(n) - z_[n]);

                double feval;
                if (lambda[n] == 0) {
                    feval = 0.0001 * diff;
                } else {
                    feval = diff * lambda[n];
                }
                if (feval > maxFun) {
                    maxFun = feval;
                }
            } // for

            fitness = maxFun;
        } // if
        else if (functionType_.equals("_AGG")) {
            double sum = 0.0;
            for (int n = 0; n < problem_.getNumberOfObjectives(); n++) {
                sum += (lambda[n]) * individual.getObjective(n);
            }

            fitness = sum;

        } else if (functionType_.equals("_PBI")) {
            double d1, d2, nl;
            double theta = 5.0;

            d1 = d2 = nl = 0.0;

            for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
                d1 += (individual.getObjective(i) - z_[i]) * lambda[i];
                nl += Math.pow(lambda[i], 2.0);
            }
            nl = Math.sqrt(nl);
            d1 = Math.abs(d1) / nl;

            for (int i = 0; i < problem_.getNumberOfObjectives(); i++) {
                d2 += Math.pow((individual.getObjective(i) - z_[i]) - d1 * (lambda[i] / nl), 2.0);
            }
            d2 = Math.sqrt(d2);

            fitness = (d1 + theta * d2);
        } else {
            System.out.println("MOEAD.fitnessFunction: unknown type " + functionType_);
            System.exit(-1);
        }
        return fitness;
    } // fitnessEvaluation

    public void setFunctionType_(String functionType_) {
        this.functionType_ = functionType_;
        this.methodName = this.methodName+functionType_;
    }
    
    @Override
    public void executeMethod() throws JMException {
        int[] permutation = new int[populationSize];
        Utils.randomPermutation(permutation, populationSize);

        for (int i = 0; i < populationSize; i++) {
            int n = permutation[i]; // or int n = i;
            //int n = i ; // or int n = i;
            int type;
            double rnd = PseudoRandom.randDouble();

            // STEP 2.1. Mating selection based on probability
            if (rnd < delta_) // if (rnd < realb)    
            {
                type = 1;   // neighborhood
            } else {
                type = 2;   // whole population
            }
            Vector<Integer> p = new Vector<Integer>();
            matingSelection(p, n, 1, type);

            // STEP 2.2. Reproduction
            Solution[] parents = new Solution[2];

            parents[0] = population.get(p.get(0));
            parents[1] = population.get(n);

            Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents, (CITO_CAITO) problem_);
            mutationOperator.execute(offSpring[0], (CITO_CAITO) problem_);
            mutationOperator.execute(offSpring[1], (CITO_CAITO) problem_);
            problem_.evaluate(offSpring[0]);
            problem_.evaluateConstraints(offSpring[0]);
            problem_.evaluate(offSpring[1]);
            problem_.evaluateConstraints(offSpring[1]);
            evaluations+=2;

                // STEP 2.3. Repair. Not necessary
            // STEP 2.4. Update z_
            updateReference(offSpring[0]);
            updateReference(offSpring[1]);

            // STEP 2.5. Update of solutions
            updateProblem(offSpring[0], n, type);
            updateProblem(offSpring[1], n, type);
        } // for 
    }

    @Override
    public void initPopulation() {

        evaluations = 0;
        maxEvaluations = ((Integer) this.getInputParameter("maxEvaluations")).intValue();
        populationSize = ((Integer) this.getInputParameter("populationSize")).intValue();
        dataDirectory_ = this.getInputParameter("dataDirectory").toString();
        System.out.println("POPSIZE: " + populationSize);

        population = new SolutionSet(populationSize);
        indArray_ = new Solution[problem_.getNumberOfObjectives()];

        T_ = ((Integer) this.getInputParameter("T")).intValue();
        nr_ = ((Integer) this.getInputParameter("nr")).intValue();
        delta_ = ((Double) this.getInputParameter("delta")).doubleValue();

        /*
         T_ = (int) (0.1 * populationSize);
         delta_ = 0.9;
         nr_ = (int) (0.01 * populationSize);
         */
        neighborhood_ = new int[populationSize][T_];

        z_ = new double[problem_.getNumberOfObjectives()];
        //lambda_ = new Vector(problem_.getNumberOfObjectives()) ;
        lambda_ = new double[populationSize][problem_.getNumberOfObjectives()];

        crossoverOperator = operators_.get("crossover"); // default: DE crossover
        mutationOperator = operators_.get("mutation");  // default: polynomial mutation
        try {
            // STEP 1. Initialization
            // STEP 1.1. Compute euclidean distances between weight vectors and find T
            initUniformWeight();
            //for (int i = 0; i < 300; i++)
            // 	System.out.println(lambda_[i][0] + " " + lambda_[i][1]) ;

            initNeighborhood();

            // STEP 1.2. Initialize population
            initPopulationMOEAD();

            // STEP 1.3. Initialize z_
            initIdealPoint();
        } catch (JMException ex) {
            Logger.getLogger(Genetic_MOEAD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Genetic_MOEAD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public SolutionSet execute() throws JMException, ClassNotFoundException {
        this.initPopulation();
        // STEP 2. Update
        do {
            this.executeMethod();
        } while (evaluations < maxEvaluations);
        return population;
    }

} // MOEAD

