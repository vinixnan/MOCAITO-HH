#!/bin/bash  
echo "MECBA - Combined 2 objectives"

populationsize=300
archivesize=300
maxEvaluations=60000
crossover=$1
mutation=$2
runs=$3
problem=$4
runHH=100
alpha=6
beta=1

#java -Xms1024m -Xmx2048m -classpath dist/MECBA-HH-GDA.jar jmetal.experiments.Combined_NSGAII_2obj $populationsize $archivesize $maxEvaluations $crossover $mutation $runs $problem > resultado/time_Experiment_NSGAII_2obj-$problem.txt &
#java -Xms1024m -Xmx2048m -classpath dist/MECBA-HH-GDA.jar jmetal.experiments.Combined_IBEA_2obj $populationsize $archivesize $maxEvaluations $crossover $mutation $runs $problem > resultado/time_Experiment_IBEA_2obj-$problem.txt &
#java -Xms1024m -Xmx2048m -classpath dist/MECBA-HH-GDA.jar jmetal.experiments.Combined_SPEA2_2obj $populationsize $archivesize $maxEvaluations $crossover $mutation $runs $problem > resultado/time_Experiment_SPEA2_2obj-$problem.txt &
java -Xms1024m -Xmx2048m -classpath dist/MECBA-HH.jar jmetal.experiments.Combined_HH_2obj $populationsize $archivesize $maxEvaluations $crossover $mutation $runs $problem > resultado/time_Experiment_HH_2obj-$problem.txt &
