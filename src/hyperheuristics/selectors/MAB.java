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
import java.util.Random;

/**
 *
 * @author vinicius
 */
public class MAB extends HyperHeuristicSelector{

    private int numberOfHeuristics = 3;
    private double scalingFactor;
    private Boolean opSelected[] = new Boolean[numberOfHeuristics]; 
    
    public MAB(double scalingFactor, ArrayList<AlgorithmHH> algs, int numObj, int evaluationsPerTime, int populationsize) {
        super(algs, numObj, evaluationsPerTime, populationsize);
        this.scalingFactor = scalingFactor;
        for(int i = 0; i < numberOfHeuristics; i++){
            opSelected[i] = false;
        }
    }

    @Override
    public AlgorithmHH chooseAlg(int time, Double qOp[], Double nOpTrials []) {
        int op = 0;
        if (time == 0) {
            return this.selectAlg(op);
        }
        Double calc[] = new Double[numberOfHeuristics];

        for (int j = 0; j < numberOfHeuristics; j++) {
            //System.out.println("Op " + j + ": qOp: " + qOp[j] + " C: " + scalingFactor * Math.sqrt(2 * Math.log10(sumOfTrials(nOpTrials)/ nOpTrials[j])));
            
            calc[j] = qOp[j] + (scalingFactor * Math.sqrt(2 * Math.log10(sumOfTrials(nOpTrials) / nOpTrials[j])));
            // System.out.println("Operador " + j + " :" + calc[j]);
        }
        op = argMax(calc);
				// System.out.println("Selecionou: " + op);

        return this.selectAlg(op);
    }
    
    private int argMax(Double[] calc) {
		int max = 0;
		double maxvalue = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < calc.length; i++) {
			if (calc[i] > maxvalue) {
				max = i;
				maxvalue = calc[i];
			}
		}
		return max;
    }
    
    
    
    private boolean thereIsUnusedOperator(Boolean[] v) {
		boolean result = false;
		for (int i = 0; i < v.length; i++) {
			if (v[i] == false) {
				result = true;
			}
		}
		return result;
   }
    
    private int getUnusedOperator(Boolean[] v) {
		int sum = 0;
		int chosen = 0;
		ArrayList<Integer> operators = new ArrayList<Integer>();
		for (int i = 0; i < v.length; i++) {
			if (v[i] == false) {
				sum++;
				operators.add(i);
			}
		}
		double prob[] = new double[sum];
		
		prob[0] = (double) 1 / sum;
		for (int i = 1; i < prob.length; i++) {
			prob[i] = prob[i - 1] + (double) 1 / sum;
		}
		
		Random random = new Random();
		double randomNumber = random.nextDouble();
		boolean x = true;
		for (int i = 0; i < prob.length; i++) {
			if (randomNumber <= prob[i]) {
				chosen = i;
				break;
			}
		}
		return operators.get(chosen);
	}
    
    private double sumOfTrials(Double v[]) {
		double sum = 0;
		for (int i = 0; i < v.length; i++) {
			sum += v[i];
		}
		return sum;
	}
    
    public double getReward(int op){
        this.calcRanking();
        int[] frequencies =this.calcAllFrequency();
        AlgorithmRanking rniRanking = this.getMetric(algs.get(op), hypervolume); //RNI
        frequencies[op] = frequencies[op] + rniRanking.getRanking();//RNI
        
        double min = 5.0;
        double max = 7.0;
        
        double value = 2.0 * (double) (numberOfHeuristics+1) - (double) (frequencies[op]);
        return (value-min)/(max-min);  
    }
    
    
 }

