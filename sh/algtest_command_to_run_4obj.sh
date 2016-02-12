#!/bin/bash  
echo "MECBA - Combined 4 objectives"

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

nohup java -Xms1024m -Xmx2048m -classpath dist/MECBA-HH.jar jmetal.experiments.Combined_HH_4obj $populationsize $archivesize $maxEvaluations $crossover $mutation $runs $runHH $alpha $beta $problem > resultado/analise_4obj-$problem.csv 2>resultado/out$problem-4obj.txt &
nohup java -Xms1024m -Xmx2048m -classpath dist/MECBA-HH.jar jmetal.experiments.Combined_NSGAII_4obj $populationsize $archivesize $maxEvaluations $crossover $mutation $runs $problem > resultado/time_Experiment_NSGAII_4obj-$problem.txt &
nohup java -Xms1024m -Xmx2048m -classpath dist/MECBA-HH.jar jmetal.experiments.Combined_IBEA_4obj $populationsize $archivesize $maxEvaluations $crossover $mutation $runs $problem > resultado/time_Experiment_IBEA_4obj-$problem.txt &
nohup java -Xms1024m -Xmx2048m -classpath dist/MECBA-HH.jar jmetal.experiments.Combined_SPEA2_4obj $populationsize $archivesize $maxEvaluations $crossover $mutation $runs $problem > resultado/time_Experiment_SPEA2_4obj-$problem.txt &
nohup java -Xms1024m -Xmx2048m -classpath dist/MECBA-HH.jar jmetal.experiments.Combined_MOEAD_4obj $populationsize $archivesize $maxEvaluations $crossover $mutation $runs $problem > resultado/time_Experiment_MOEAD_4obj-$problem.txt &
