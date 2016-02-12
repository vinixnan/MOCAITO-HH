/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.core;

import hyperheuristics.metric.AlgorithmEffort;
import hyperheuristics.metric.HypervolumeHandler;
import hyperheuristics.metric.MetricHandler;
import hyperheuristics.metric.RNI;
import hyperheuristics.metric.SpreadHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jmetal.base.Solution;
import jmetal.base.SolutionSet;

/**
 *
 * @author vinicius
 */
public abstract class HyperHeuristicSelector {

    protected int numObj;
    protected ArrayList<AlgorithmHH> algs;
    protected AlgorithmHH current;

    protected HashMap<AlgorithmHH, ArrayList<AlgorithmRanking>> hash;
    protected HashMap<AlgorithmHH, SolutionSet> olderPopulation;

    protected HypervolumeHandler hypervolume;
    protected RNI rni;
    protected SpreadHandler ud;
    protected AlgorithmEffort ae;

    private long initialTime;
    private long finalTime;

    protected SolutionSet borders;

    public HyperHeuristicSelector(ArrayList<AlgorithmHH> algs, int numObj, int evaluationsPerTime, int populationsize) {
        this.numObj = numObj;
        this.algs = algs;
        this.current = null;
        this.hash = new HashMap<>();
        this.olderPopulation = new HashMap<>();
        this.hypervolume = new HypervolumeHandler(this.numObj);
        this.rni = new RNI(this.numObj, populationsize);
        this.ae = new AlgorithmEffort(this.numObj);
        this.ae.setEvaluations(evaluationsPerTime);
        this.ud = new SpreadHandler(this.numObj);
        this.borders = null;
        this.initHHSelector();
    }

    public HyperHeuristicSelector(ArrayList<AlgorithmHH> algs, int numObj, int evaluationsPerTime, int populationsize, double[] minimumValues, double[] maximumValues) {
        this.numObj = numObj;
        this.algs = algs;
        this.current = null;
        this.hash = new HashMap<>();
        this.olderPopulation = new HashMap<>();
        this.hypervolume = new HypervolumeHandler(this.numObj);
        this.rni = new RNI(this.numObj, populationsize);
        this.ae = new AlgorithmEffort(this.numObj);
        this.ae.setEvaluations(evaluationsPerTime);
        this.ud = new SpreadHandler(this.numObj);
        this.borders = new SolutionSet(2);
        Solution min = new Solution(numObj);
        Solution max = new Solution(numObj);
        for (int i = 0; i < this.numObj; i++) {
            min.setObjective(i, minimumValues[i]);
            max.setObjective(i, maximumValues[i]);
        }
        this.borders.add(min);
        this.borders.add(max);
        this.initHHSelector();
    }

    public void startTime() {
        this.initialTime = System.currentTimeMillis();
        this.finalTime = 0;
    }

    public void finishTime() {
        this.finalTime = System.currentTimeMillis();
    }

    protected void trocaContexto(SolutionSet newpop, SolutionSet newarchive, ArchivedAlgorithmHH destino) {
        this.olderPopulation.put(destino, destino.getNonDominatedPopulation());
        destino.setPopulation(newpop);
        destino.setArchive(newarchive);
        destino.setEvaluations(0);
    }

    protected void trocaContexto(SolutionSet newpop, AlgorithmHH destino) {
        this.olderPopulation.put(destino, destino.getNonDominatedPopulation());
        destino.setPopulation(newpop);
        destino.setEvaluations(0);
    }

    protected void trocaContexto(AlgorithmHH origem, AlgorithmHH destino) {
        if (origem != null) {
            if (origem instanceof ArchivedAlgorithmHH && destino instanceof ArchivedAlgorithmHH) {
                //SPEA TO IBEA or IBEA TO SPEA OR IBEA TO IBEA OR SPEA TO SPEA
                this.trocaContexto(origem.getPopulation(), ((ArchivedAlgorithmHH) origem).getArchive(), (ArchivedAlgorithmHH) destino);
            } else if (origem instanceof AlgorithmHH && destino instanceof ArchivedAlgorithmHH) {
                //EX NSGAII TO IBEA OR NSGAII TO SPEA
                this.trocaContexto(origem.getPopulation(), origem.getNonDominatedPopulation(), (ArchivedAlgorithmHH) destino);
            } else if (origem instanceof ArchivedAlgorithmHH && destino instanceof AlgorithmHH) {
                //ex: IBEA TO NSGAII OR SPEA TO NSGAII
                this.trocaContexto(((ArchivedAlgorithmHH) origem).getJoinedCutPopulation(), destino);//tecnica 1
            } else {
                //NSGAII TO NSGAII
                this.trocaContexto(origem.getPopulation(), destino);
            }
            origem.restartEstimatedTime();
        } else {
            for (AlgorithmHH alg : this.algs) {
                if (alg instanceof ArchivedAlgorithmHH && destino instanceof ArchivedAlgorithmHH) {
                    //SPEA TO IBEA or IBEA TO SPEA OR IBEA TO IBEA OR SPEA TO SPEA
                    alg.setPopulation(destino.getPopulation());
                    ((ArchivedAlgorithmHH) alg).setArchive(((ArchivedAlgorithmHH) destino).getArchive());
                } else if (alg instanceof AlgorithmHH && destino instanceof ArchivedAlgorithmHH) {
                    //ex: SPEA OR IBEA TO NSGAII
                    alg.setPopulation(((ArchivedAlgorithmHH) destino).getJoinedCutPopulation());//tecnica 1
                } else if (alg instanceof ArchivedAlgorithmHH && destino instanceof AlgorithmHH) {
                    //EX NSGAII TO IBEA OR NSGAII TO SPEA
                    alg.setPopulation(destino.getPopulation());
                    ((ArchivedAlgorithmHH) alg).setArchive(destino.getNonDominatedPopulation());
                } else {
                    //NSGAII TO NSGAII
                    alg.setPopulation(destino.getPopulation());
                }
                this.olderPopulation.put(alg, destino.getNonDominatedPopulation()); //VER
                alg.restartEstimatedTime();
            }
        }
        destino.setEvaluations(0);
        destino.noTimeCount();
    }

    public void incrementTimebutNotThis(AlgorithmHH algNoincrement) {
        for (AlgorithmHH alg : this.algs) {
            if (alg != algNoincrement) {
                alg.endTimeCount();
                alg.incrementRound();
            } else {
                alg.noTimeCount();
            }
        }
    }

    protected void hashInit() {
        for (AlgorithmHH alg : algs) {
            SolutionSet fullPop = alg.getNonDominatedPopulation();
            this.olderPopulation.put(alg, fullPop);
            ArrayList<AlgorithmRanking> ranking = new ArrayList<>();
            AlgorithmRanking algranking;

            algranking = new AlgorithmRanking(new Improvement(this.hypervolume), alg, this.numObj);//Hypervolume
            ranking.add(algranking);//Hypervolume

            algranking = new AlgorithmRanking(new Improvement(rni), alg, this.numObj);//RNI
            ranking.add(algranking);//RNI

            algranking = new AlgorithmRanking(new Improvement(ud), alg, this.numObj);//UD
            ranking.add(algranking);//UD

            algranking = new AlgorithmRanking(new Improvement(ae), alg, this.numObj);//AE
            ranking.add(algranking);//AE

            this.hash.put(alg, ranking);
        }
    }

    protected SolutionSet joinAllPopulation() {
        SolutionSet allpopulation = new SolutionSet();
        for (SolutionSet population : this.olderPopulation.values()) {
            allpopulation = allpopulation.union(population);
        }
        return allpopulation;
    }

    public SolutionSet joinNewAllPopulation() {
        SolutionSet allpopulation = new SolutionSet();
        for (AlgorithmHH alg : this.algs) {
            SolutionSet population = alg.getNonDominatedPopulation();
            allpopulation = allpopulation.union(population);
        }
        return allpopulation;
    }

    public void initHHSelector() {
        this.hashInit();

    }

    public AlgorithmHH selectAlg(int id) {
        if (id > this.algs.size()) {
            id = 0;
        }
        AlgorithmHH alg = this.algs.get(id);
        this.trocaContexto(this.current, alg);
        this.current = alg;
        return alg;
    }

    public AlgorithmHH selectAlg(AlgorithmHH alg) {
        this.trocaContexto(this.current, alg);
        this.current = alg;
        return alg;
    }

    public void updateRankingAlg(AlgorithmHH alg) {
        ArrayList<AlgorithmRanking> ranking = this.hash.get(alg);
        for (AlgorithmRanking algRanking : ranking) {
            algRanking.calcMetric();
        }
    }

    private ArrayList<AlgorithmRanking> getRankingByMetric(MetricHandler metric) {
        ArrayList<AlgorithmRanking> ranking = new ArrayList<>();
        for (AlgorithmHH alg : algs) {
            ArrayList<AlgorithmRanking> allranking = this.hash.get(alg);
            AlgorithmRanking algranking = null;
            for (AlgorithmRanking aux : allranking) {
                if (aux.getImp().getMetric() == metric) {
                    algranking = aux;
                    algranking.calcMetric();
                }
            }
            ranking.add(algranking);
        }
        Collections.sort(ranking);
        return ranking;
    }

    protected void calcRankingMetric(MetricHandler metric) {
        ArrayList<AlgorithmRanking> ranking = this.getRankingByMetric(metric);
        int rank = 0;
        double previus = Double.MIN_VALUE;
        for (AlgorithmRanking algranking : ranking) {
            if (algranking.getValue() != previus) {
                rank++;
                previus = algranking.getValue();
            }
            algranking.setRanking(rank);
        }
    }

    public void updateAEImprovement(AlgorithmHH alg, long initialtime, long finaltime) {
        this.ae.setTimeExpend(finaltime - initialtime);
        double improveAE = this.calcImprovement(alg, ae, null);//AE
        Improvement imp = this.getImprovementObj(alg, ae);//AE
        imp.addImprovement(improveAE);//AE
    }

    public void updateImprovement(AlgorithmHH alg, SolutionSet allpopulation) {
       // MetricsUtil metricUtil = new MetricsUtil();
        // allpopulation=metricUtil.readNonDominatedSolutionSet("OO_MyBatis-alljoin.txt");
        double improveHyp = this.calcImprovement(alg, hypervolume, allpopulation);//Hypervolume
        Improvement imp = this.getImprovementObj(alg, hypervolume);//Hypervolume
        imp.addImprovement(improveHyp);//Hypervolume

        double improveRNI = this.calcImprovement(alg, rni, allpopulation);//RNI
        imp = this.getImprovementObj(alg, rni);//RNI
        imp.addImprovement(improveRNI);//RNI

        double improveUD = this.calcImprovement(alg, ud, allpopulation);//UD
        imp = this.getImprovementObj(alg, ud);//UD
        imp.addImprovement(improveUD);//UD

        if (this.finalTime - this.initialTime > 0 && alg == this.current) {
            this.updateAEImprovement(alg, initialTime, finalTime);
        }
    }

    public void calcRanking() {
        SolutionSet allPop;
        if (this.borders == null) {
            allPop = this.joinAllPopulation();
        } else {
            allPop = this.borders;
        }

        for (AlgorithmHH alg : this.algs) {
            this.updateImprovement(alg, allPop);
        }
        this.calcRankingMetric(hypervolume);//Hypervolume
        this.calcRankingMetric(rni); //RNI
        this.calcRankingMetric(ud); //UD
        this.calcRankingMetric(ae); //AE
    }

    private int calcFrequencyFirst(AlgorithmHH alg) {
        ArrayList<AlgorithmRanking> ranking = this.hash.get(alg);
        int qtdfirst = 0;
        if (ranking != null) {
            for (AlgorithmRanking algrank : ranking) {
                if (algrank.getRanking() == 1) {
                    qtdfirst++;
                }
            }
        }
        return qtdfirst;
    }

    protected int[] calcAllFrequency() {
        HashMap<AlgorithmHH, Integer> frequencies = new HashMap<AlgorithmHH, Integer>();
        int biggerFrequency = Integer.MIN_VALUE;
        for (int i = 0; i < this.algs.size(); i++) {
            AlgorithmHH alg = this.algs.get(i);
            int aux = this.calcFrequencyFirst(alg);
            frequencies.put(alg, aux);
            if (aux > biggerFrequency) {
                biggerFrequency = aux;
            }
        }
        LinkedHashMap orderedfrequencies = this.sortByComparator(frequencies);
        int[] frequenciesArray = new int[this.algs.size()];
        for (int i = 0; i < this.algs.size(); i++) {
            AlgorithmHH alg = this.algs.get(i);
            frequenciesArray[i] = this.returnrRankPosition(alg, orderedfrequencies);
        }
        return frequenciesArray;
    }

    private int returnrRankPosition(AlgorithmHH alg, LinkedHashMap<AlgorithmHH, Integer> frequencies) {
        Iterator it = frequencies.entrySet().iterator();
        int counter = 0;
        int lastFrequency = Integer.MIN_VALUE;
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            counter++;
            if (pairs.getKey() == alg) {
                if (lastFrequency == ((Integer) pairs.getValue())) {
                    return counter - 1;
                } else {
                    return counter;
                }
            }
            lastFrequency = ((Integer) pairs.getValue());
        }
        return counter;
    }

    private LinkedHashMap<AlgorithmHH, Integer> sortByComparator(Map<AlgorithmHH, Integer> unsortMap) {

        // Convert Map to List
        List<Map.Entry<AlgorithmHH, Integer>> list
                = new LinkedList<Map.Entry<AlgorithmHH, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<AlgorithmHH, Integer>>() {
            public int compare(Map.Entry<AlgorithmHH, Integer> o1,
                    Map.Entry<AlgorithmHH, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // Convert sorted map back to a Map
        LinkedHashMap<AlgorithmHH, Integer> sortedMap = new LinkedHashMap<AlgorithmHH, Integer>();
        for (Iterator<Map.Entry<AlgorithmHH, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<AlgorithmHH, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    protected Improvement getImprovementObj(AlgorithmHH alg, MetricHandler metric) {
        ArrayList<AlgorithmRanking> rankings = this.hash.get(alg);
        for (AlgorithmRanking rank : rankings) {
            if (rank.getImp().getMetric() == metric) {
                return rank.getImp();
            }
        }
        return null;
    }

    protected AlgorithmRanking getMetric(AlgorithmHH alg, MetricHandler metric) {
        ArrayList<AlgorithmRanking> rankings = this.hash.get(alg);
        for (AlgorithmRanking rank : rankings) {
            if (rank.getImp().getMetric() == metric) {
                return rank;
            }
        }
        return null;
    }

    public void updateOldPopulation(AlgorithmHH alg, SolutionSet solution) {
        this.olderPopulation.put(alg, solution);
    }

    protected double calcAtualMetricValue(AlgorithmHH alg, MetricHandler metric, SolutionSet allPop) {
        metric.clear();
        metric.addParetoFront(allPop);
        metric.addParetoFront(alg.getNonDominatedPopulation());//VER
        double newH = metric.calculate(alg.getNonDominatedPopulation());//VER
        return newH;/// - oldH;
    }

    protected double calcOldMetricValue(AlgorithmHH alg, MetricHandler metric, SolutionSet allPop) {
        metric.clear();
        metric.addParetoFront(allPop);
        metric.addParetoFront(alg.getNonDominatedPopulation());//VER
        double oldH = metric.calculate(this.olderPopulation.get(alg));
        return oldH;/// - oldH;
    }

    protected double calcImprovement(AlgorithmHH alg, MetricHandler metric, SolutionSet allPop) {
        return this.calcAtualMetricValue(alg, metric, allPop);// - this.calcOldMetricValue(alg, metric, allPop);
    }

    public AlgorithmHH chooseAlg() {
        return null;
    }

    public AlgorithmHH chooseAlg(int firstIteration, Double[] qOp, Double[] nOptrial) {
        return null;
    }

    public ArrayList<AlgorithmHH> getAlgs() {
        return algs;
    }
}
