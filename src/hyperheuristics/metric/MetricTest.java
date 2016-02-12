/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.metric;

import jmetal.base.SolutionSet;
import jmetal.qualityIndicator.GenerationalDistance;
import jmetal.qualityIndicator.InvertedGenerationalDistance;
import jmetal.qualityIndicator.util.MetricsUtil;

/**
 *
 * @author vinicius
 */
public class MetricTest {

    public static void main(String args[]) {
        MetricsUtil metricUtil = new MetricsUtil();
        double ret=Double.NaN;
        /*
        args=new String[4];
        args[0]="hypervolume";
        args[1]="/home/vinicius/front.txt";
        args[2]="/home/vinicius/truefront.txt";
        args[3]="2";
                */
        
        if (args.length == 4) {
            String metricname = args[0].toLowerCase();
            String frontPath = args[1];
            String trueFrontPath = args[2];
            int numObj = Integer.parseInt(args[3]);

            SolutionSet front = metricUtil.readNonDominatedSolutionSet(frontPath);
            SolutionSet truefront = metricUtil.readNonDominatedSolutionSet(trueFrontPath);
            
            double[] maximumValues = metricUtil.getMaximumValues(truefront.writeObjectivesToMatrix(), numObj);
            double[] minimumValues = metricUtil.getMinimumValues(truefront.writeObjectivesToMatrix(), numObj);
            //truefront = PopulationWorks.removeDominadas(truefront, numObj);
            //truefront = PopulationWorks.removeRepetidas(truefront);
            
            if (metricname.equals("hypervolume")) {
                HypervolumeCalculator calc=new HypervolumeCalculator(numObj);
                ret=calc.executeWithDefinedMaxValues(front, minimumValues, maximumValues);
            } else if (metricname.equals("spread")) {
                MetricHandler metric = new SpreadHandler(numObj);
                metric.addParetoFront(truefront);
                ret=metric.calculate(front);
            } else if (metricname.equals("gd")) {
                GenerationalDistance gd = new GenerationalDistance();
                double[][] solutionFront = front.writeObjectivesToMatrix();
                double[][] trueSolutionFront = truefront.writeObjectivesToMatrix();
                ret=gd.generationalDistance(solutionFront, trueSolutionFront);
            } else if (metricname.equals("igd")) {
                InvertedGenerationalDistance igd = new InvertedGenerationalDistance();
                double[][] solutionFront = front.writeObjectivesToMatrix();
                double[][] trueSolutionFront = truefront.writeObjectivesToMatrix();
                ret=igd.invertedGenerationalDistance(solutionFront, trueSolutionFront);
            }

        }
        else{
            System.out.println("Wrong parameters");
        }
        System.out.println(ret);
    }
}
