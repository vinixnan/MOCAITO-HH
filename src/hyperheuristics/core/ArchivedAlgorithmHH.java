/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.core;

import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.base.Problem;
import jmetal.base.SolutionSet;
import jmetal.base.operator.selection.RankingAndCrowdingSelection;
import jmetal.base.operator.selection.SelectionFactory;
import jmetal.problems.CITO_CAITO;
import jmetal.util.JMException;

/**
 *
 * @author vinicius
 */
public abstract class ArchivedAlgorithmHH extends AlgorithmHH {

    protected SolutionSet archive;
    protected int archiveSize;
    
    public ArchivedAlgorithmHH(Problem problem) {
        super(problem);
    }

    public SolutionSet getArchive() {
        return this.archive;
    }

    public void setArchive(SolutionSet archive) {
        this.archive = this.clonePopulation(archive);
    }
    /**
     * Configure and init population.
     *
     * @throws jmetal.util.JMException
     */
    @Override
    public abstract void executeMethod() throws JMException;

    /**
     * Configure and init population.
     */
    @Override
    public abstract void initPopulation();
    
    public SolutionSet getJoinedCutPopulation() {
        try {
            //pegar nao dominadas do arquivo
            RankingAndCrowdingSelection rank=(RankingAndCrowdingSelection) SelectionFactory.getSelectionOperator("RankingAndCrowdingSelection");
            rank.setParameter("problem", this.problem_);
            rank.setParameter("populationSize", this.populationSize);
            SolutionSet offSpringSolutionSet = (SolutionSet) rank.execute(population.union(archive), ((CITO_CAITO)this.problem_));
            return offSpringSolutionSet;
        } catch (JMException ex) {
            Logger.getLogger(ArchivedAlgorithmHH.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.archive;
    }
    
    @Override
    public SolutionSet getNonDominatedPopulation() {
        SolutionSet pop = this.archive.union(population);
        pop=PopulationWorks.removeDominadas(pop);
        pop=PopulationWorks.removeRepetidas(pop);
        pop.setCapacity(this.populationSize);
        return pop;
    }
}
