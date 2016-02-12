/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.selectors.moeadfrrmab;

import hyperheuristics.core.AlgorithmHH;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author vinicius
 */
public class UCB_VSelector extends UCBSelector{

    private double delta;
    
    public UCB_VSelector(ArrayList<AlgorithmHH> operators, double C, double delta) {
        super(operators, C);
        this.delta=delta;
    }
    
    @Override
    protected double sumNt(HashMap<String, Integer> nt) {
        double qtd = 0;
        for (AlgorithmHH op : this.operators) {
            qtd += (nt.get(op.getMethodName()) * this.delta);
        }
        return qtd;
    }

    @Override
    protected double equation(AlgorithmHH op, HashMap<String, Double> frr, HashMap<String, Integer> nt, double sumNt) {
        double numerator = 2 * Math.log(sumNt);
        double denominator=nt.get(op.getMethodName());
        double fraction=numerator/denominator;
        double sqrt = Math.sqrt(fraction);
        double frr_value=frr.get(op.getMethodName());
        double numerator2=super.sumNt(nt);
        double denominator2=nt.get(op.getMethodName());
        double fraction2=numerator2/denominator2;
        return frr_value + this.C * sqrt + 3 * fraction2;
    }
}
