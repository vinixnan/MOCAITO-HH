#!/bin/bash
#echo "MECBA - Combined 2 and 4 objectives"

populationsize=300
archivesize=300
maxEvaluations=60000
crossover=$1
mutation=$2
runs=$4
problem=$3
runHH=$5
generation=$6
alpha=$7
beta=$8
nr=$9
mTax=${10}
echo $nr"_"$mTax
#nohup java -Xms1024m -Xmx2048m -classpath dist/MECBA-HH.jar jmetal.experiments.Combined_HH_2obj $populationsize $archivesize $maxEvaluations $crossover $mutation $runs $runHH $alpha $beta $problem > resultado/analise_2obj-$problem.csv 2>resultado/out$problem-2obj.txt &
#nohup java -Xms1024m -Xmx2048m -classpath dist/MECBA-HH.jar jmetal.experiments.Combined_NSGAII_2obj $populationsize $archivesize $maxEvaluations $crossover $mutation $runs $problem > resultado/time_Experiment_NSGAII_2obj-$problem.txt &
#nohup java -Xms1024m -Xmx2048m -classpath dist/MECBA-HH.jar jmetal.experiments.Combined_IBEA_2obj $populationsize $archivesize $maxEvaluations $crossover $mutation $runs $problem > resultado/time_Experiment_IBEA_2obj-$problem.txt &
#nohup java -Xms1024m -Xmx2048m -classpath dist/MECBA-HH.jar jmetal.experiments.Combined_SPEA2_2obj $populationsize $archivesize $maxEvaluations $crossover $mutation $runs $problem > resultado/time_Experiment_SPEA2_2obj-$problem.txt &
nohup java -Xms1024m -Xmx1024m -classpath dist/MECBA-HH.jar jmetal.experiments.Combined_MOEAD_2obj $populationsize $archivesize $maxEvaluations $crossover $mutation $runs $problem $nr $mTax > resultado/time_Experiment_MOEAD_2obj-$problem-$nr-$mTax.txt &
#nohup java -Xms1024m -Xmx1024m -classpath dist/MECBA-HH.jar jmetal.experiments.Combined_MOEAD_2obj $populationsize $archivesize $maxEvaluations $crossover $mutation $runs $problem 3 0.04 > resultado/time_Experiment_MOEAD_2obj-$problem-$mTax.txt &
#nohup java -Xms1024m -Xmx1024m -classpath dist/MECBA-HH.jar jmetal.experiments.Combined_MOEAD_2obj $populationsize $archivesize $maxEvaluations $crossover $mutation $runs $problem 3 0.08 > resultado/time_Experiment_MOEAD_2obj-$problem-$mTax.txt &
