/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.selectors;

import hyperheuristics.core.AlgorithmRanking;
import hyperheuristics.core.AlgorithmHH;
import hyperheuristics.core.HyperHeuristicSelector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author vinicius
 */
public class ChoiceFunction extends HyperHeuristicSelector {

    protected double alpha;
    protected double beta;
    
    public ChoiceFunction(double alpha, double beta, ArrayList<AlgorithmHH> algs, int numObj, int evaluationsPerTime, int populationsize) {
        super(algs,numObj, evaluationsPerTime, populationsize);
        this.alpha=alpha;
        this.beta=beta;
    }
    
     public ChoiceFunction(double alpha, double beta, ArrayList<AlgorithmHH> algs, int numObj, int evaluationsPerTime, int populationsize, double[] minimumValues, double[] maximumValues) {
        super(algs,numObj, evaluationsPerTime, populationsize, minimumValues, maximumValues);
        this.alpha=alpha;
        this.beta=beta;
    }
    

    @Override
    public AlgorithmHH chooseAlg() {
        //selecao aleatoria
        this.calcRanking();
        int[] frequencies =this.calcAllFrequency();
        System.out.println(Arrays.toString(frequencies));
        double valueChosen=0;
        System.out.println("\n=================Choice Function=================");
        ArrayList<Integer> chosen=new ArrayList<>();
        for(int i=0; i < algs.size(); i++){
            AlgorithmHH alg=this.algs.get(i);
            AlgorithmRanking rniRanking = this.getMetric(alg, this.rni); //RNI
            double cFvalue=this.cFunction(alg, frequencies[i], rniRanking.getRanking());
            if(cFvalue > valueChosen){
                valueChosen=cFvalue;
                chosen.clear();
                chosen.add(i);
            }
            else if(cFvalue == valueChosen) {
                chosen.add(i);
            }
            System.out.print("Algoritimo " + alg.getMethodName());
            System.out.print("\tEstimated Time: "+alg.getEstimatedTime());
            System.out.print("\tHypervolume Last Value: " + this.getImprovementObj(alg, hypervolume).getLastValue());
            System.out.print("\tUD Last Value: " + this.getImprovementObj(alg, ud).getLastValue());
            System.out.print("\tAE Last Value: " + (this.getImprovementObj(alg, ae).getLastValue())*(-1));//para nao mudar o if, pois o menor tempo é melhor
            System.out.println("\tRNI Last Value: " + this.getImprovementObj(alg, rni).getLastValue());
            System.out.print("\tChoice Value: " + cFvalue);
            System.out.println("\t\t\tHypervolume Rank: " + this.getMetric(alg, hypervolume).getRanking());
            System.out.println("\t\t\tRNI Rank: " + this.getMetric(alg, rni).getRanking());
            System.out.print("\t\t\tUD Rank: " + this.getMetric(alg, ud).getRanking());
            System.out.println("\t\t\tAE " + this.getMetric(alg, ae).getRanking());
        }
        int pos;
        if(chosen.size()==1){
            pos=chosen.get(0);
        }
        else{
            Random gerador = new Random();
            pos=gerador.nextInt(chosen.size());
        }
        AlgorithmHH alg=this.algs.get(pos);
        System.out.print("Rodando " + alg.getMethodName());
        System.out.print("\tChoice Value: "+valueChosen);
        System.out.println("\n=================================================");
        return this.selectAlg(pos);
    }

    protected double cFunction(AlgorithmHH alg, int freqrank, int rnirank) {
        return this.f1(this.algs.size(), freqrank, rnirank)*this.alpha + (alg.getEstimatedTime()*this.beta);
    }

    protected double f1(int N, int freqrank, int rnirank) {
        return 2 * (N + 1) - (freqrank + rnirank);
    }
}
