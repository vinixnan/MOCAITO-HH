package jmetal.experiments;


import hyperheuristics.algs.Genetic_MOEAD;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import jmetal.base.Algorithm;
import jmetal.base.Operator;
import jmetal.base.SolutionSet;
import jmetal.base.operator.crossover.CrossoverFactory;
import jmetal.base.operator.mutation.MutationFactory;
import jmetal.base.operator.selection.SelectionFactory;
import jmetal.problems.Combined2Objetives;
import jmetal.util.JMException;

//jar populationsize archivesize maxEvaluations crossover mutation runs (software name)
public class Combined_MOEAD_2obj {

    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
    public static void main(String[] args) throws FileNotFoundException, IOException, JMException, ClassNotFoundException {

        String[] softwares = {"OO_MyBatis", "OA_AJHsqldb", "OA_AJHotDraw", "OO_BCEL", "OO_JHotDraw", "OA_HealthWatcher", "OO_JBoss"};
        int runsNumber, populationSize, archiveSize, maxEvaluations;
        String crossoverName, mutationName;
        int T=30, nr=1;
        double delta=0.9;
        String moeadFn="TCHE1";
        double mutationProbability = 0.02; //0.2;
        double crossoverProbability = 1.0;
        if (args.length == 9) {
            populationSize = Integer.parseInt(args[0]);
            archiveSize = Integer.parseInt(args[1]);
            maxEvaluations = Integer.parseInt(args[2]);
            crossoverName=args[3];
            mutationName=args[4];
            runsNumber = Integer.parseInt(args[5]);
            softwares = new String[1];
            softwares[0] = args[6];
            nr= Integer.parseInt(args[7]);
            mutationProbability= Double.parseDouble(args[8]);
        }else if (args.length == 8) {
            populationSize = Integer.parseInt(args[0]);
            archiveSize = Integer.parseInt(args[1]);
            maxEvaluations = Integer.parseInt(args[2]);
            crossoverName=args[3];
            mutationName=args[4];
            runsNumber = Integer.parseInt(args[5]);
            nr= Integer.parseInt(args[6]);
            mutationProbability= Double.parseDouble(args[7]);
            softwares = new String[5];
            softwares[0] = "OO_MyBatis";
            softwares[1] = "OA_AJHsqldb";
            softwares[2] = "OO_BCEL";
            softwares[3] = "OA_AJHotDraw";
            softwares[4] = "OA_HealthWatcher";
        }
        else {
            runsNumber = 30;
            populationSize = 300;
            archiveSize = 300;
            maxEvaluations = 60000;
            crossoverName="TwoPointsCrossover";
            mutationName="SwapMutation";
        }
        
        for (String filename : softwares) {
            
            String context = "_Comb_2obj";

            File directory = new File("resultado/moead/" + filename + context);
            if (!directory.exists()) {
                if (!directory.mkdir()) {
                    System.exit(0);
                }
            }

            Combined2Objetives problem = new Combined2Objetives("problemas/" + filename + ".txt");
            Algorithm algorithm = new Genetic_MOEAD(problem);
            SolutionSet todasRuns = new SolutionSet();
            Operator crossover;
            Operator mutation;
            Operator selection;
            
            //double mutaProb=(1/(double)problem.numberOfElements_)+mutationProbability;
            double mutaProb=mutationProbability;
            // Crossover
            crossover = CrossoverFactory.getCrossoverOperator(crossoverName);
            crossover.setParameter("probability", crossoverProbability);
            // Mutation
            mutation = MutationFactory.getMutationOperator(mutationName);// SwapMutation
            mutation.setParameter("probability", mutaProb);
            // Selection
            selection = SelectionFactory.getSelectionOperator("BinaryTournament");
            // Algorithm params
            algorithm.setInputParameter("populationSize", populationSize);
            algorithm.setInputParameter("maxEvaluations", maxEvaluations);
            algorithm.setInputParameter("archiveSize", archiveSize);
            algorithm.addOperator("crossover", crossover);
            algorithm.addOperator("mutation", mutation);
            algorithm.addOperator("selection", selection);
            
            algorithm.setInputParameter("finalSize", populationSize); // used by MOEAD_DRA
            algorithm.setInputParameter("T", T);//30
            algorithm.setInputParameter("delta", delta);
            algorithm.setInputParameter("nr", nr);//30
            algorithm.setInputParameter("dataDirectory", "/home/vrcarvalho/Oficina/mecba-hh/moead_weight");

            System.out.println("\n================ MOEAD ================");
            System.out.println("Software: " + filename);
            System.out.println("Context: " + context);
            System.out.println("Params:");
            System.out.println("\tPop -> " + populationSize);
            System.out.println("\tArq -> " + archiveSize);
            System.out.println("\tMaxEva -> "+maxEvaluations);
            System.out.println("\tCrossover: " + crossoverName);
            System.out.println("\tCross -> "+crossoverProbability);
            System.out.println("\tMutation: " + mutationName);
            System.out.println("\tMuta -> "+mutaProb);
            System.out.println("Number of elements: " + problem.numberOfElements_);

            long heapSize = Runtime.getRuntime().totalMemory();
            heapSize = (heapSize / 1024) / 1024;
            System.out.println("Heap Size: " + heapSize + "Mb\n");

            for (int runs = 0; runs < runsNumber; runs++) {
                // Execute the Algorithm
                long initTime = System.currentTimeMillis();
                SolutionSet resultFront = algorithm.execute();
                long estimatedTime = System.currentTimeMillis() - initTime;
                System.out.println("Iruns: " + runs + "\tTotal time: " + estimatedTime);

                resultFront = problem.removeDominadas(resultFront);
                resultFront = problem.removeRepetidas(resultFront);

                resultFront.printObjectivesToFile("resultado/moead_"+nr+"_"+mutationProbability+"/" + filename + context + "/FUN_moead" + "-" + filename + "-" + runs + ".NaoDominadas");
                resultFront.printVariablesToFile("resultado/moead/" + filename + context + "/VAR_moead" + "-" + filename + "-" + runs + ".NaoDominadas");

                //armazena as solucoes de todas runs
                todasRuns = todasRuns.union(resultFront);
            }

            todasRuns = problem.removeDominadas(todasRuns);
            todasRuns = problem.removeRepetidas(todasRuns);
            todasRuns.printObjectivesToFile("resultado/moead_"+nr+"_"+mutationProbability+"/" + filename + context + "/All_FUN_moead" + "-" + filename);
            todasRuns.printVariablesToFile("resultado/moead/" + filename + context + "/All_VAR_moead" + "-" + filename);

            //grava arquivo juntando funcoes e variaveis
            //gravaCompleto(todasRuns, "TodasRuns-Completo_moead");
        }
    }
    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
}
