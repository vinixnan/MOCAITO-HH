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
public class UCB_TunnedSelector extends UCBSelector{

    private double delta;
    
    public UCB_TunnedSelector(ArrayList<AlgorithmHH> operators, double C, double delta) {
        super(operators, C);
        this.delta=delta;
    }

    protected HashMap<String, Double> VCalc(HashMap<String, Integer> nt, double sumNt) {
        HashMap<String, Double> V_values=new HashMap<>();
        for (int i = 0; i < this.operators.size(); i++) {
            AlgorithmHH op = this.operators.get(i);
            double temp1 = 2 * Math.log(sumNt);
            double temp2 = temp1 / (nt.get(op.getMethodName()) + 1);
            double temp3 = Math.sqrt(temp2);
            double value=Math.pow(this.delta, 2) + temp3;
            V_values.put(op.getMethodName(), value);
        }
        return V_values;
    }
    
    @Override
    protected double equation(AlgorithmHH op, HashMap<String, Double> frr, HashMap<String, Integer> nt, double sumNt) {
        HashMap<String, Double> V_values=this.VCalc(nt, sumNt);
        double vop=V_values.get(op.getMethodName());
        double numerator = Math.log(sumNt);
        double denominator=nt.get(op.getMethodName());
        double fraction=numerator/denominator;
        double sqrt = Math.sqrt(fraction * Math.min(1/4, vop));
        double frr_value=frr.get(op.getMethodName());
        return frr_value + this.C * sqrt;
    }
}
