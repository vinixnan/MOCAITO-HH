package jmetal.experiments;

import hyperheuristics.core.AlgorithmHH;
import hyperheuristics.core.HeuristicBuilder;
import hyperheuristics.core.HyperHeuristicSelector;
import hyperheuristics.core.PopulationWorks;
import hyperheuristics.selectors.ChoiceFunction;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import jmetal.base.SolutionSet;
import jmetal.problems.Combined4Objectives;
import jmetal.util.JMException;

public class Combined_HH_4obj {

    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
    public static void main(String[] args) throws FileNotFoundException, IOException, JMException, ClassNotFoundException {

        String[] softwares;
        int runsNumber, populationSize, archiveSize, totalEvaluations, runHH;
        String crossoverName, mutationName;
        double alpha, beta;
        
        if (args.length == 10) {
            populationSize = Integer.parseInt(args[0]);
            archiveSize = Integer.parseInt(args[1]);
            totalEvaluations = Integer.parseInt(args[2]);
            crossoverName=args[3];
            mutationName=args[4];
            runsNumber = Integer.parseInt(args[5]);
            runHH = Integer.parseInt(args[6]);
            alpha = Double.parseDouble(args[7]);
            beta=Double.parseDouble(args[8]);
            softwares = new String[1];
            softwares[0] = args[9];
        }else if (args.length == 9) {
            populationSize = Integer.parseInt(args[0]);
            archiveSize = Integer.parseInt(args[1]);
            totalEvaluations = Integer.parseInt(args[2]);
            crossoverName=args[3];
            mutationName=args[4];
            runsNumber = Integer.parseInt(args[5]);
            softwares = new String[5];
            softwares[0] = "OO_MyBatis";
            softwares[1] = "OA_AJHsqldb";
            softwares[2] = "OO_BCEL";
            softwares[3] = "OA_AJHotDraw";
            softwares[4] = "OA_HealthWatcher";
            runHH = Integer.parseInt(args[6]);
            alpha = Double.parseDouble(args[7]);
            beta=Double.parseDouble(args[8]);
        }
        else {
            softwares = new String[3];
            softwares[0] = "OO_MyBatis";
            softwares[1] = "OA_AJHsqldb";
            softwares[2] = "OO_BCEL";
            //softwares[3] = "OA_AJHotDraw";
            //softwares[4] = "OA_HealthWatcher";
            runsNumber = 5;
            populationSize = 300;
            archiveSize = 300;
            totalEvaluations = 60000;
            crossoverName="TwoPointsCrossover";
            mutationName="SimpleInsertionMutation";
            runHH = 100;
            alpha = 25;
            beta=1;
        }
        
        double crossoverProbability = 0.95;
        double mutationProbability = 0.02; //0.2;
        int maxEvaluations = totalEvaluations / runHH;//slideSize como no artigo
        
        runHH--;
        int numObj=4;

        for (String filename : softwares) {

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
            long initTime = System.currentTimeMillis();
            SolutionSet todasRuns = new SolutionSet();

            SolutionSet partial = new SolutionSet();

            System.out.println("==========================Executando o problema " + filename + "==========================");
            System.out.println("HH: Choice Function");
            System.out.println("Context: " + context);
            System.out.println("Params:");
            System.out.println("\tPop -> " + populationSize);
            System.out.println("\tArchiveSize -> " + archiveSize);
            System.out.println("\tMaxEva -> " + totalEvaluations);
            System.out.println("\tSlide Window -> " + maxEvaluations);
            System.out.println("\tCrossover: " + crossoverName);
            System.out.println("\tCross -> "+crossoverProbability);
            System.out.println("\tMutation: " + mutationName);
            System.out.println("\tMuta -> "+mutationProbability);
            System.out.println("\tNumber of elements: " + problem.numberOfElements_);
            System.out.println("\tChoices Number "+(runHH+1));
            System.out.println("\tAlpha "+alpha);
            System.out.println("\tBeta "+beta);

            HeuristicBuilder builder = new HeuristicBuilder(problem, crossoverName, crossoverProbability, mutationName, mutationProbability, populationSize, totalEvaluations / 3, archiveSize);

            //escolhe algoritimo
            for (int runs = 0; runs < runsNumber; runs++) {
                long initRunTime = System.currentTimeMillis();
                SolutionSet resultFront = new SolutionSet();
                builder.initAlgs();
                HyperHeuristicSelector selector = new ChoiceFunction(alpha,beta, builder.getAlgs(), numObj, maxEvaluations, populationSize, minimumValues, maximumValues);
                AlgorithmHH algorithm;
                for (AlgorithmHH alg : builder.getAlgs()) {
                    System.out.println("Inicializando " + alg.getMethodName());
                    int i = 0;
                    long initialtime = System.currentTimeMillis();
                    while (i < maxEvaluations) {
                        alg.executeMethod();
                        i = alg.getEvaluations();
                    }
                    selector.updateAEImprovement(alg, initialtime, System.currentTimeMillis());
                }
                selector.calcRanking();
                for (int eval = 0; eval < runHH; eval++) {
                    algorithm = selector.chooseAlg();
                    int i = 0;
                    selector.startTime();
                    while (i < maxEvaluations) {
                        algorithm.executeMethod();
                        i = algorithm.getEvaluations();
                    }
                    selector.finishTime();
                    selector.incrementTimebutNotThis(algorithm);
                    partial = algorithm.getNonDominatedPopulation();
                    //escolhe algoritimo
                }
                resultFront = partial;
                resultFront.printObjectivesToFile("resultado/all/" + filename + context + "/FUN_all" + "-" + filename + "-" + runs + ".NaoDominadas");
                resultFront.printVariablesToFile("resultado/all/" + filename + context + "/VAR_all" + "-" + filename + "-" + runs + ".NaoDominadas");
                //armazena as solucoes de todas runs
                todasRuns = todasRuns.union(resultFront);
                long estimatedTime = System.currentTimeMillis() - initRunTime;
                System.out.println("Iruns: " + runs + "\tTotal time: " + estimatedTime);
            }
            todasRuns = PopulationWorks.removeDominadas(todasRuns);
            todasRuns = PopulationWorks.removeRepetidas(todasRuns);
            todasRuns.printObjectivesToFile("resultado/all/" + filename + context + "/All_FUN_all" + "-" + filename);
            todasRuns.printVariablesToFile("resultado/all/" + filename + context + "/All_VAR_all" + "-" + filename);

            long estimatedTotalTime = System.currentTimeMillis() - initTime;
            System.out.println("\n===============REPORT===================");
            System.out.println("Software: " + problem.getName());

            System.out.println("Estimated Total Time: " + estimatedTotalTime);
            System.out.println("\n=======================================");
            //grava arquivo juntando funcoes e variaveis
            //gravaCompleto(todasRuns, "TodasRuns-Completo_ibea");
        }
    }
    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
}
