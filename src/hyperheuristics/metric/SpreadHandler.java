package hyperheuristics.metric;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import jmetal.base.SolutionSet;
import jmetal.qualityIndicator.GeneralizedSpread;
import jmetal.qualityIndicator.Spread;

/**
 *
 * @author vinicius
 */
public class SpreadHandler extends MetricHandler {

    private final Spread spread2objectives;
    private final GeneralizedSpread spread4objectives;

    public SpreadHandler(int numObj) {
        super(numObj);
        this.spread2objectives = new Spread();
        this.spread4objectives=new GeneralizedSpread();
    }
    
    @Override
    public double calculate(SolutionSet front) {
        if (population.size() != 0) {
            double[][] objectives = front.writeObjectivesToMatrix();
            double[][] paretoFront=this.population.writeObjectivesToMatrix();
            if(this.numObj==2)
                return this.spread2objectives.spread(objectives, paretoFront, this.numObj);
            else
                return this.spread4objectives.generalizedSpread(objectives, paretoFront, this.numObj);
        }
        return 0D;
    }
}
