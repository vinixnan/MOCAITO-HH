/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.selectors;

import hyperheuristics.core.AlgorithmRanking;
import hyperheuristics.core.AlgorithmHH;
import hyperheuristics.core.Improvement;
import hyperheuristics.core.HyperHeuristicSelector;
import java.util.ArrayList;
import java.util.Random;
import jmetal.base.SolutionSet;

/**
 *
 * @author vinicius
 */
public class Roulette extends HyperHeuristicSelector {
    
    
    
    public Roulette(ArrayList<AlgorithmHH> algs, int numObj, int evaluationsPerTime, int populationsize) {
        super(algs, numObj, evaluationsPerTime, populationsize);
        
    }

    @Override
    public AlgorithmHH chooseAlg() {
        this.calcRanking();
        double sum=0;
        double[] valuesR2=new double[this.algs.size()];
        int[] allpos=new int[100];
        
        for (int i = 0; i < algs.size(); i++) {
            AlgorithmHH alg = this.algs.get(i);
            AlgorithmRanking hypRanking = this.getMetric(alg, this.hypervolume); //r2
            sum+=hypRanking.getValue()*100.00;//r2
            valuesR2[i]=hypRanking.getValue()*100.00;
            System.out.print("Algoritimo " + alg.getMethodName());
            System.out.print("\tEstimated Time: " + alg.getEstimatedTime());
            System.out.println("\tR2 Last Value: " + this.getImprovementObj(alg, hypervolume).getLastValue());
        }
        int j=0;
        for (int i = 0; i < valuesR2.length; i++) {
            valuesR2[i]=Math.floor((valuesR2[i]/sum)*100.00);
            for(int walk=0; walk < valuesR2[i]; walk++){
                allpos[j++]=i;
            }
        }
        Random gerador = new Random();
        int pos = gerador.nextInt(allpos.length-1);//por conta da quebra de decimais
        AlgorithmHH alg = this.algs.get(allpos[pos]);
        System.out.print("Rodando " + alg.getMethodName());
        System.out.println("\n=================================================");
        return this.selectAlg(allpos[pos]);
    }
    
    @Override
    public void updateImprovement(AlgorithmHH alg, SolutionSet allpopulation) {
        double improveR2= this.calcAtualMetricValue(alg, hypervolume, allpopulation);//R2
        Improvement imp = this.getImprovementObj(alg, hypervolume);//R2
        imp.addImprovement(improveR2);//R2
    }

    @Override
    public void calcRanking() {
        SolutionSet allPop = this.joinAllPopulation();
        if (this.current != null) {
            this.updateImprovement(current, allPop);
        } else {
            for (AlgorithmHH alg : this.algs) {
                this.updateImprovement(alg, allPop);
            }
        }
        this.calcRankingMetric(hypervolume); //R2
    }
}
