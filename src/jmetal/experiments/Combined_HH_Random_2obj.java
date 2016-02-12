package jmetal.experiments;

import hyperheuristics.core.AlgorithmHH;
import hyperheuristics.core.ArchivedAlgorithmHH;
import hyperheuristics.core.HeuristicBuilder;
import hyperheuristics.core.HyperHeuristicSelector;
import hyperheuristics.core.PopulationWorks;
import hyperheuristics.metric.HypervolumeCalculator;
import hyperheuristics.metric.R2Calculator;
import hyperheuristics.selectors.RandomicChoice;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import jmetal.base.SolutionSet;
import jmetal.problems.Combined2Objetives;
import jmetal.util.JMException;
import jmetal.util.Ranking;

public class Combined_HH_Random_2obj {

    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
    public static void main(String[] args) throws FileNotFoundException, IOException, JMException, ClassNotFoundException {

        String[] softwares;
        int runsNumber, populationSize, archiveSize, totalEvaluations, runHH;
        String crossoverName, mutationName;
        
int generationsPerRound;

        if (args.length == 9) {
            populationSize = Integer.parseInt(args[0]);
            archiveSize = Integer.parseInt(args[1]);
            totalEvaluations = Integer.parseInt(args[2]);
            crossoverName = args[3];
            mutationName = args[4];
            runsNumber = Integer.parseInt(args[5]);
            runHH = Integer.parseInt(args[6]);
            softwares = new String[1];
            softwares[0] = args[7];
            generationsPerRound = Integer.parseInt(args[8]);
        } else if (args.length == 8) {
            populationSize = Integer.parseInt(args[0]);
            archiveSize = Integer.parseInt(args[1]);
            totalEvaluations = Integer.parseInt(args[2]);
            crossoverName = args[3];
            mutationName = args[4];
            runsNumber = Integer.parseInt(args[5]);
            runHH = Integer.parseInt(args[6]);
            generationsPerRound = Integer.parseInt(args[7]);
            softwares = new String[5];
            softwares[0] = "OO_MyBatis";
            softwares[1] = "OA_AJHsqldb";
            softwares[2] = "OO_BCEL";
            softwares[3] = "OA_AJHotDraw";
            softwares[4] = "OA_HealthWatcher";
        } else {
            softwares = new String[1];
            softwares[0] = "OO_BCEL";
            //softwares[1] = "OA_AJHsqldb";
            //softwares[2] = "OO_BCEL";
            //softwares[3] = "OO_MyBatis";
            //softwares[4] = "OA_HealthWatcher";
            runsNumber = 1;
            populationSize = 300;
            archiveSize = 300;
            totalEvaluations = 60000;
            crossoverName = "TwoPointsCrossover";
            mutationName = "SimpleInsertionMutation";
            runHH = 200;
            generationsPerRound = 5;
        }
        
        double crossoverProbability = 0.95;
        double mutationProbability = 0.02; //0.2;

        int maxEvaluations = totalEvaluations / runHH;//slideSize como no artigo
        int numObj = 2;
      
       
        for (String filename : softwares) {
             System.err.println(runsNumber+" "+filename+" "+runHH);
            String context = "_Comb_2obj";
            File directory = new File("resultado/all/" + filename + context);
            if (!directory.exists()) {
                if (!directory.mkdir()) {
                    System.err.println("Dir error");
                    System.exit(0);
                }
            }

            Combined2Objetives problem = new Combined2Objetives("problemas/" + filename + ".txt");
            double[] maximumValues=problem.getMaxValues();
            double[] minimumValues=new double[numObj];
            double[][] iterationsValues=new double[runHH][3];//200 generations 3 metrics
            
            HypervolumeCalculator hypervolume=new HypervolumeCalculator(numObj);
            R2Calculator r2=new R2Calculator(numObj);
            
            SolutionSet todasRuns = new SolutionSet();
            SolutionSet partial = new SolutionSet();
            HeuristicBuilder builder = new HeuristicBuilder(problem, crossoverName, crossoverProbability, mutationName, mutationProbability, populationSize, totalEvaluations / 3, archiveSize);
            for (int runs = 0; runs < runsNumber; runs++) {
                System.err.println(runs);
                SolutionSet resultFront;
                builder.initAlgs();
                HyperHeuristicSelector selector = new RandomicChoice(builder.getAlgs(), numObj, maxEvaluations, populationSize);
                AlgorithmHH algorithm;
                int generation=0;
                algorithm = selector.chooseAlg();
                int momentToChange=0;
                for (int eval = 0; eval < runHH; eval++) {
                    if(momentToChange==generationsPerRound){
                        System.err.println("Troca");
                        algorithm = selector.chooseAlg();
                        momentToChange=0;
                    }
                    int i = 0;
                    while (i < maxEvaluations) {
                        System.err.println("Rodando " + algorithm.getMethodName());
                        algorithm.executeMethod();
                        i = algorithm.getEvaluations();
                        
                        SolutionSet sol;
                        if(algorithm instanceof ArchivedAlgorithmHH){
                            sol=((ArchivedAlgorithmHH)algorithm).getArchive();
                        }
                        else{
                            sol=algorithm.getPopulation();
                        }
                        Ranking ranking = new Ranking(sol);
                        sol=ranking.getSubfront(0);
                        sol=PopulationWorks.removeDominadas(sol);
                        sol=PopulationWorks.removeRepetidas(sol);
                        hypervolume.addParetoFront(sol);
                        r2.addParetoFront(sol);
                        double hyp=hypervolume.executeWithDefinedMaxValues(sol, minimumValues, maximumValues);
                        double r2val=r2.executeWithDefinedMaxValues(sol, minimumValues, maximumValues);
                        hypervolume.clear();
                        r2.clear();
                        iterationsValues[generation][0]+=hyp;
                        iterationsValues[generation][1]+=r2val;
                        iterationsValues[generation][2]+=sol.size();
                        generation++;
                        momentToChange++;
                    }
                    partial = algorithm.getNonDominatedPopulation();
                }
                resultFront = partial;
                resultFront.printObjectivesToFile("resultado/all/" + filename + context + "/FUN_all" + "-" + filename + "-" + runs + ".NaoDominadas_Random_" + generationsPerRound);
                resultFront.printVariablesToFile("resultado/all/" + filename + context + "/VAR_all" + "-" + filename + "-" + runs + ".NaoDominadas_Random_" + generationsPerRound);
                //armazena as solucoes de todas runs
                todasRuns = todasRuns.union(resultFront);

            }
            System.out.println("\n\n\n");
            for(int generation=0; generation < runHH; generation++){
                System.out.print((generation+1));
                for(int i=0; i < 3; i++){
                    iterationsValues[generation][i]=iterationsValues[generation][i]/runsNumber;
                    System.out.print(" "+iterationsValues[generation][i]);
                }
                System.out.println("");
            }
            System.out.println("\n\n\n");
            todasRuns = PopulationWorks.removeDominadas(todasRuns);
            todasRuns = PopulationWorks.removeRepetidas(todasRuns);
            todasRuns.printObjectivesToFile("resultado/all/" + filename + context + "/All_FUN_all" + "-" + filename + "_Random_" + generationsPerRound);
            todasRuns.printVariablesToFile("resultado/all/" + filename + context + "/All_VAR_all" + "-" + filename + "_Random_" + generationsPerRound);
        }
    }
    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
}
