/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package hyperheuristics.core;

import hyperheuristics.metric.MetricHandler;
import java.util.ArrayList;

/**
 *
 * @author vinicius
 */
public class Improvement {
    private ArrayList<Double> improvements;
    
    private MetricHandler metric;

    public Improvement(MetricHandler metric) {
        this.metric = metric;
        this.improvements=new ArrayList<>();
    }
    
    public void addImprovement(double improvement){
        
            this.improvements.add(improvement);
    }
    
    public double getLastValue(){
        if(this.improvements.size() > 0)
            return this.improvements.get(this.improvements.size()-1);
        return Double.MIN_VALUE;
    }

    public double getAverage() {
        double sum=0;
        if(this.improvements.size() > 0){
            for(Double val : this.improvements){
                sum+=val;
            }
            sum=sum/this.improvements.size();
        }
        return sum;
    }

    public ArrayList<Double> getImprovements() {
        return improvements;
    }

    

    public MetricHandler getMetric() {
        return metric;
    }

   

    
    
    
    
}
