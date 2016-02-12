package jmetal.experiments;

import hyperheuristics.core.AlgorithmHH;
import hyperheuristics.core.HeuristicBuilder;
import hyperheuristics.core.PopulationWorks;
import hyperheuristics.selectors.HVImpChoice;
import hyperheuristics.selectors.HVImpChoiceAVG;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.base.SolutionSet;
import jmetal.problems.Combined4Objectives;
import jmetal.util.JMException;

public class Combined_HH_OrientedAVG_Hyp_4obj {

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
        int numObj = 4;
        int iterationsPerChoice = populationSize * generationsPerRound;
        

        for (String filename : softwares) {
            System.err.println(runsNumber + " " + filename + " " + runHH);
            String context = "_Comb_4obj";
            File directory = new File("resultado/all/" + filename + context);
            if (!directory.exists()) {
                if (!directory.mkdir()) {
                    System.err.println("Dir error");
                    System.exit(0);
                }
            }

            Combined4Objectives problem = new Combined4Objectives("problemas/" + filename + ".txt");
            double[] maximumValues = problem.getMaxValues();
            double[] minimumValues = new double[numObj];
            double[][] iterationsValues = new double[runHH][3];//200 generations 3 metrics

            SolutionSet todasRuns = new SolutionSet();
            SolutionSet partial = new SolutionSet();
            HeuristicBuilder builder = new HeuristicBuilder(problem, crossoverName, crossoverProbability, mutationName, mutationProbability, populationSize, totalEvaluations / 3, archiveSize);
            for (int runs = 0; runs < runsNumber; runs++) {
                System.err.println(runs);
                SolutionSet resultFront;
                builder.initAlgs();
                HVImpChoice selector = new HVImpChoiceAVG(builder.getAlgs(), numObj, maxEvaluations, populationSize, maximumValues, minimumValues, runHH);
                AlgorithmHH algorithm;

                double oldvalues[][][] = new double[3][generationsPerRound][3];
                for (int i = 0; i < selector.getAlgs().size(); i++) {
                    algorithm = selector.getAlgs().get(i);
                    System.err.println("Inicializando " + algorithm.getMethodName());
                    algorithm.setEvaluations(0);
                    for (int exec = 0; exec < generationsPerRound; exec++) {
                        try {
                            oldvalues[i][exec] = selector.calcIndicators(algorithm);
                            algorithm.executeMethod();
                            double _old = oldvalues[i][exec][0];
                            selector.updateLastHypervolume(i, _old);//apenas o ultimo valor será usado
                        } catch (JMException ex) {
                            Logger.getLogger(HVImpChoice.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    selector.updateHypervolume(algorithm);
                    selector.incrementTimebutNotThis(algorithm);
                }
                algorithm = selector.chooseAlg();
                int posAlg = selector.getAlgs().indexOf(algorithm);
                int generation;
                for (generation = 0; generation < generationsPerRound; generation++) {

                    for (int j = 0; j < 3; j++) {
                        iterationsValues[generation][j] += oldvalues[posAlg][generation][j];
                    }

                }
                int totalEval = populationSize * generationsPerRound;
                while (totalEval < totalEvaluations) {
                    int partialEval = 0;
                    while (partialEval < iterationsPerChoice) {
                        selector.updateHypervolume(algorithm);
                        double[] values = selector.calcIndicators(algorithm);
                        for (int j = 0; j < iterationsValues[0].length; j++) {
                            iterationsValues[generation][j] += values[j];
                        }
                        generation++;
                        System.err.println("Rodando " + algorithm.getMethodName() + " " + generation + " " + totalEval);
                        algorithm.executeMethod();
                        partialEval = algorithm.getEvaluations();
                    }
                    totalEval += partialEval;
                    partial = algorithm.getNonDominatedPopulation();
                    algorithm = selector.chooseAlg();
                }
                resultFront = partial;
                resultFront.printObjectivesToFile("resultado/all/" + filename + context + "/FUN_all" + "-" + filename + "-" + runs + ".NaoDominadas_AVG_" + generationsPerRound);
                resultFront.printVariablesToFile("resultado/all/" + filename + context + "/VAR_all" + "-" + filename + "-" + runs + ".NaoDominadas_AVG_" + generationsPerRound);
                //armazena as solucoes de todas runs
                todasRuns = todasRuns.union(resultFront);

            }
            System.out.println("\n\n\n");
            for (int generation = 0; generation < runHH; generation++) {
                System.out.print((generation + 1));
                for (int i = 0; i < 3; i++) {
                    iterationsValues[generation][i] = iterationsValues[generation][i] / runsNumber;
                    System.out.print(" " + iterationsValues[generation][i]);
                }
                System.out.println("");
            }
            System.out.println("\n\n\n");
            todasRuns = PopulationWorks.removeDominadas(todasRuns);
            todasRuns = PopulationWorks.removeRepetidas(todasRuns);
            todasRuns.printObjectivesToFile("resultado/all/" + filename + context + "/All_FUN_all" + "-" + filename + "_AVG_" + generationsPerRound);
            todasRuns.printVariablesToFile("resultado/all/" + filename + context + "/All_VAR_all" + "-" + filename + "_AVG_" + generationsPerRound);
        }
    }
    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
}
