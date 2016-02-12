/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.core;

import hyperheuristics.algs.Genetic_MOEAD;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.base.Operator;
import jmetal.base.Problem;
import hyperheuristics.algs.IBEA;
import hyperheuristics.algs.NSGAII;
import hyperheuristics.algs.SPEA2;
import jmetal.base.operator.crossover.CrossoverFactory;
import jmetal.base.operator.mutation.MutationFactory;
import jmetal.base.operator.selection.SelectionFactory;

import jmetal.util.JMException;

/**
 *
 * @author vinicius
 */
public class HeuristicBuilder {

    protected Problem problem;
    protected String crossoverName;
    protected double crossoverProbability;
    protected String mutationName;
    protected double mutationProbability;
    protected int populationSize;
    protected int maxEvaluations;
    protected int archiveSize;

    protected ArrayList<AlgorithmHH> algs;
    protected ArrayList<double[]> probabilities;

    public HeuristicBuilder(Problem problem, String crossoverName, double crossoverProbability, String mutationName, double mutationProbability, int populationSize, int maxEvaluations, int archiveSize) {
        this.problem = problem;
        this.crossoverProbability = crossoverProbability;
        this.mutationProbability = mutationProbability;
        this.populationSize = populationSize;
        this.maxEvaluations = maxEvaluations;
        this.archiveSize = archiveSize;
        this.algs = new ArrayList<>();
        this.crossoverName = crossoverName;
        this.mutationName = mutationName;
        this.probabilities = new ArrayList<>();
        double[] values=new double[2];
        values[0]=crossoverProbability;
        values[1]=mutationProbability;
        this.probabilities.add(values);
    }

    public void addProbability(double crossoverProbability, double mutationProbability){
        double[] values=new double[2];
        values[0]=crossoverProbability;
        values[1]=mutationProbability;
        this.probabilities.add(values);
    }

    private void setParametersAlg(AlgorithmHH algorithm, double crossoverProbability, double mutationProbability) throws JMException {

        Operator crossover;
        Operator mutation;
        Operator selection;
        HashMap parameters = new HashMap();

        // Crossover
        crossover = CrossoverFactory.getCrossoverOperator(crossoverName); //PMXCrossover TwoPointsCrossover
        crossover.setParameter("probability", crossoverProbability);
        // Mutation
        mutation = MutationFactory.getMutationOperator(mutationName);//SwapMutation SimpleInsertionMutation
        mutation.setParameter("probability", mutationProbability);
        // Selection
        selection = SelectionFactory.getSelectionOperator("BinaryTournament");

        // Algorithm params
        algorithm.setInputParameter("populationSize", populationSize);
        algorithm.setInputParameter("maxEvaluations", maxEvaluations);
        algorithm.setInputParameter("archiveSize", archiveSize);
        //algorithm.setInputParameter("indicators", new QualityIndicator(problem, "./WFG1.2D.pf"));
        algorithm.addOperator("crossover", crossover);
        algorithm.addOperator("mutation", mutation);
        algorithm.addOperator("selection", selection);
    }

    public AlgorithmHH createIBEA(double crossoverprob, double mutationprob) throws JMException {
        AlgorithmHH algorithm = new IBEA(problem);
        algorithm.setMethodName(algorithm.getMethodName()+"_C"+crossoverprob+"_M"+mutationprob);
        this.setParametersAlg(algorithm, crossoverprob, mutationprob);
        algorithm.initPopulation();
        return algorithm;
    }

    public AlgorithmHH createNSGAII(double crossoverprob, double mutationprob) throws JMException {
        AlgorithmHH algorithm = new NSGAII(problem);
        algorithm.setMethodName(algorithm.getMethodName()+"_C"+crossoverprob+"_M"+mutationprob);
        this.setParametersAlg(algorithm, crossoverprob, mutationprob);
        algorithm.initPopulation();
        return algorithm;
    }

    public AlgorithmHH createSPEA2(double crossoverprob, double mutationprob) throws JMException {
        AlgorithmHH algorithm = new SPEA2(problem);
        algorithm.setMethodName(algorithm.getMethodName()+"_C"+crossoverprob+"_M"+mutationprob);
        this.setParametersAlg(algorithm, crossoverprob, mutationprob);
        algorithm.initPopulation();
        return algorithm;
    }
    
    private AlgorithmHH createMOEAD(String fn, double crossoverprob, double mutationprob) throws JMException {
        Genetic_MOEAD algorithm = new Genetic_MOEAD(problem);
        algorithm.setMethodName(algorithm.getMethodName()+"_C"+crossoverprob+"_M"+mutationprob);
        algorithm.setFunctionType_(fn);
        this.setParametersAlg(algorithm, crossoverprob, mutationprob);
        algorithm.setInputParameter("finalSize", populationSize); // used by MOEAD_DRA
        algorithm.setInputParameter("T", 30);//30
        algorithm.setInputParameter("delta", 0.9);
        algorithm.setInputParameter("nr", 3);//30
        algorithm.setInputParameter("dataDirectory", "/home/vrcarvalho/Oficina/mecba-hh/moead_weight");
        algorithm.initPopulation();
        return algorithm;
    }

    public void initAlgs() {
        this.algs = new ArrayList<>();
        try {
            for(double[] values : this.probabilities){
                double crossoverprob=values[0];
                double mutationprob=values[1];
                this.algs.add(this.createNSGAII(crossoverprob, mutationprob));
                this.algs.add(this.createSPEA2(crossoverprob, mutationprob));
                this.algs.add(this.createIBEA(crossoverprob, mutationprob));
                //this.algs.add(this.createMOEAD("_TCHE1",crossoverprob, mutationprob));
            }
        } catch (JMException ex) {
            Logger.getLogger(HeuristicBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.setSamePopulation();
    }

    private void setSamePopulation() {
        if (this.algs.size() > 0) {
            Random gerador = new Random();
            int pos = gerador.nextInt(this.algs.size());
            AlgorithmHH chosen = this.algs.get(pos);
            for (AlgorithmHH alg : this.algs) {
                alg.setPopulation(chosen.getPopulation());
            }
        }
    }

    public ArrayList<AlgorithmHH> getAlgs() {
        return algs;
    }
}
