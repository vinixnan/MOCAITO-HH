/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.selectors;


import hyperheuristics.core.AlgorithmHH;
import hyperheuristics.core.HyperHeuristicSelectorImprovement;
import hyperheuristics.selectors.moeadfrrmab.CreditAssignment;
import hyperheuristics.selectors.moeadfrrmab.SlidingWindow;
import hyperheuristics.selectors.moeadfrrmab.UCBSelector;
import java.util.ArrayList;
import java.util.HashMap;
import jmetal.base.SolutionSet;

/**
 *
 * @author vinicius
 */
public class FRRMAB extends HyperHeuristicSelectorImprovement {

    private UCBSelector mabselector;
    private SlidingWindow slidewindow;
    private CreditAssignment creditAssignment;
    private HashMap<String, Integer> nt;
    private HashMap<String, Double> FIR;
    

    public FRRMAB(ArrayList<AlgorithmHH> algs, int numObj, int evaluationsPerTime, int populationsize, double C, double D, int W) {
        super(algs, numObj, evaluationsPerTime, populationsize);
        this.nt=new HashMap<>();
        this.FIR=new HashMap<>();
        this.slidewindow=new SlidingWindow(W);
        this.mabselector=new UCBSelector(this.algs, C);
        this.creditAssignment=new CreditAssignment(D, FIR);
        for(AlgorithmHH alg :this.algs){
            this.nt.put(alg.getMethodName(), 0);
            this.FIR.put(alg.getMethodName(), 0.0);
        }
    }
    
    private void addIteminSlide(AlgorithmHH alg){
        SolutionSet allPop;
        if(this.borders==null)
            allPop = this.joinAllPopulation();
        else
            allPop=this.borders;
        double value=this.calcImprovement(alg, this.hypervolume, allPop);
        this.FIR.put(alg.getMethodName(), value + this.FIR.get(alg.getMethodName()));
        this.slidewindow.addItem(alg.getMethodName(), this.FIR.get(alg.getMethodName()), nt);
    }
    
    private void printMethod(HashMap<String, Double> frr){
        for(AlgorithmHH alg : this.algs){
            System.out.println(alg.getMethodName()+" FRR "+frr.get(alg.getMethodName()).toString()+" FIR "+FIR.get(alg.getMethodName()).toString()+" NT "+nt.get(alg.getMethodName()));
        }
    }
    
    @Override
    public AlgorithmHH chooseAlg() {
        if(this.current!=null){
            this.addIteminSlide(this.current);
        }
        HashMap<String, Double> frr= creditAssignment.calcFRR(slidewindow);
        AlgorithmHH alg=this.mabselector.selectOperator(frr, nt);
        this.printMethod(frr);
        System.out.println("Select "+alg.getMethodName());
        return this.selectAlg(alg);
    }
}
