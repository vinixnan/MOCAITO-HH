package jmetal.experiments;

import hyperheuristics.algs.*;
import hyperheuristics.core.AlgorithmHH;
import hyperheuristics.core.ArchivedAlgorithmHH;
import hyperheuristics.core.PopulationWorks;
import hyperheuristics.metric.HypervolumeCalculator;
import hyperheuristics.metric.R2Calculator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import jmetal.base.Operator;
import jmetal.base.SolutionSet;
import jmetal.base.operator.crossover.CrossoverFactory;
import jmetal.base.operator.mutation.MutationFactory;
import jmetal.base.operator.selection.SelectionFactory;
import jmetal.problems.Combined2Objetives;
import jmetal.util.JMException;
import jmetal.util.Ranking;
//java -Xms1024m -Xmx2048m -classpath dist/MECBA-HH.jar jmetal.experiments.Combined_Arbitrary_2obj > resultado/royalNSSPEA_MyBatis.txt &
public class Combined_Arbitrary_2obj {

    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
    public static void main(String[] args) throws FileNotFoundException, IOException, JMException, ClassNotFoundException {

        String[] softwares;
        int runsNumber, populationSize, archiveSize, totalEvaluations;
        String crossoverName, mutationName;
        String algName;
        
        if (args.length == 8) {
            populationSize = Integer.parseInt(args[0]);
            archiveSize = Integer.parseInt(args[1]);
            totalEvaluations = Integer.parseInt(args[2]);
            crossoverName=args[3];
            mutationName=args[4];
            runsNumber = Integer.parseInt(args[5]);
            algName=args[6];
            softwares = new String[1];
            softwares[0] = args[7];
        }else if (args.length == 7) {
            populationSize = Integer.parseInt(args[0]);
            archiveSize = Integer.parseInt(args[1]);
            totalEvaluations = Integer.parseInt(args[2]);
            crossoverName=args[3];
            mutationName=args[4];
            runsNumber = Integer.parseInt(args[5]);
            algName=args[6];
            softwares = new String[5];
            softwares[0] = "OO_MyBatis";
            softwares[1] = "OA_AJHsqldb";
            softwares[2] = "OO_BCEL";
            softwares[3] = "OA_AJHotDraw";
            softwares[4] = "OA_HealthWatcher";
        }
        else {
            softwares = new String[1];
            softwares[0] = "OO_MyBatis";
            //softwares[1] = "OA_AJHsqldb";
            //softwares[2] = "OO_MyBatis";
            //softwares[3] = "OO_MyBatis";
            //softwares[4] = "OA_HealthWatcher";
            runsNumber = 30;
            populationSize = 300;
            archiveSize = 300;
            totalEvaluations = 60000;
            crossoverName = "TwoPointsCrossover";
            mutationName = "SimpleInsertionMutation";
            algName="NSGAII";
            
        }
        String algDir=algName.toLowerCase();
        double crossoverProbability = 0.95;
        double mutationProbability = 0.02; //0.2;
        int numOfGenerations=200;
        int evaluationsPerGeneration=totalEvaluations/numOfGenerations;

        for (String filename : softwares) {
            
            String context = "_Comb_2obj";

            File directory = new File("resultado/"+algDir+"/" + filename + context);
            if (!directory.exists()) {
                if (!directory.mkdir()) {
                    System.exit(0);
                }
            }

            Combined2Objetives problem = new Combined2Objetives("problemas/" + filename + ".txt");
            int problemsize=2;
            
            double[] maximumValues=problem.getMaxValues();
            double[] minimumValues=new double[problemsize];
            double[][] iterationsValues=new double[numOfGenerations][3];//200 generations 3 metrics
            
            HypervolumeCalculator hypervolume=new HypervolumeCalculator(problemsize);
            R2Calculator r2=new R2Calculator(problemsize);
            
            AlgorithmHH algorithm;
            algorithm = new NSGAII(problem);
            
            ArchivedAlgorithmHH second=new IBEA(problem);
           
            SolutionSet todasRuns = new SolutionSet();
            Operator crossover;
            Operator mutation;
            Operator selection;

            // Crossover
            crossover = CrossoverFactory.getCrossoverOperator(crossoverName);
            crossover.setParameter("probability", crossoverProbability);
            // Mutation
            mutation = MutationFactory.getMutationOperator(mutationName);// SwapMutation
            mutation.setParameter("probability", mutationProbability);
            // Selection
            selection = SelectionFactory.getSelectionOperator("BinaryTournament");
            // Algorithm params
            algorithm.setInputParameter("populationSize", populationSize);
            algorithm.setInputParameter("maxEvaluations", evaluationsPerGeneration);
            algorithm.setInputParameter("archiveSize", archiveSize);
            algorithm.addOperator("crossover", crossover);
            algorithm.addOperator("mutation", mutation);
            algorithm.addOperator("selection", selection);
            
            // Algorithm params
            second.setInputParameter("populationSize", populationSize);
            second.setInputParameter("maxEvaluations", evaluationsPerGeneration);
            second.setInputParameter("archiveSize", archiveSize);
            second.addOperator("crossover", crossover);
            second.addOperator("mutation", mutation);
            second.addOperator("selection", selection);
            
            /*
            System.out.println("\n================ "+algName+" ================");
            System.out.println("Software: " + filename);
            System.out.println("Context: " + context);
            System.out.println("Params:");
            System.out.println("\tPop -> " + populationSize);
            System.out.println("\tArq -> " + archiveSize);
            System.out.println("\tMaxEva -> "+totalEvaluations);
            System.out.println("\tCrossover: " + crossoverName);
            System.out.println("\tCross -> "+crossoverProbability);
            System.out.println("\tMutation: " + mutationName);
            System.out.println("\tMuta -> "+mutationProbability);
            System.out.println("Number of elements: " + problem.numberOfElements_);
            System.out.println("\n=============================================\n\n");
*/
            long heapSize = Runtime.getRuntime().totalMemory();
            heapSize = (heapSize / 1024) / 1024;
            
            
            for (int runs = 0; runs < runsNumber; runs++) {
                // Execute the Algorithm
                SolutionSet resultFront=new SolutionSet();
                algorithm.initPopulation();
                second.initPopulation();
                int i=0;
                int generation=0;
                while(i < totalEvaluations){
                    if(generation==100){
                        second.setPopulation(algorithm.getPopulation());
                        second.setArchive(algorithm.getNonDominatedPopulation());
                        second.setEvaluations(0);
                        algorithm=second;
                    }
                    algorithm.setEvaluations(0);
                    algorithm.executeMethod();
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
                    sol.printObjectivesToFile("resultado/"+algDir+"/" + filename + context + "/FUN_" +algDir+ "-" + filename + "-" + runs + ".NaoDominadas"+i);
                    i+=algorithm.getEvaluations();
                    iterationsValues[generation][0]+=hyp;
                    iterationsValues[generation][1]+=r2val;
                    iterationsValues[generation][2]+=sol.size();
                    generation++;
                }
                resultFront=algorithm.getPopulation();
                Ranking ranking = new Ranking(resultFront);
                resultFront=ranking.getSubfront(0);
                resultFront = problem.removeDominadas(resultFront);
                resultFront = problem.removeRepetidas(resultFront);

                resultFront.printObjectivesToFile("resultado/"+algDir+"/" + filename + context + "/FUN_" +algDir+ "-" + filename + "-" + runs + ".NaoDominadas");
                resultFront.printVariablesToFile("resultado/"+algDir+"/" + filename + context + "/VAR_"+algDir + "-" + filename + "-" + runs + ".NaoDominadas");

                //armazena as solucoes de todas runs
                todasRuns = todasRuns.union(resultFront);
            }
            
            for(int generation=0; generation < numOfGenerations; generation++){
                System.out.print((generation+1));
                for(int i=0; i < 3; i++){
                    iterationsValues[generation][i]=iterationsValues[generation][i]/runsNumber;
                    System.out.print(" "+iterationsValues[generation][i]);
                }
                System.out.println("");
            }

            
            todasRuns = problem.removeDominadas(todasRuns);
            todasRuns = problem.removeRepetidas(todasRuns);
            todasRuns.printObjectivesToFile("resultado/"+algDir+"/" + filename + context + "/All_FUN_"+algDir + "-" + filename);
            todasRuns.printVariablesToFile("resultado/"+algDir+"/" + filename + context + "/All_VAR_"+algDir + "-" + filename);

            //grava arquivo juntando funcoes e variaveis
            //gravaCompleto(todasRuns, "TodasRuns-Completo_ibea");
            System.out.println("\n\n\n");
        }
    }
    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
}
