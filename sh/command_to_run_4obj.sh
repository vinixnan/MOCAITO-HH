#!/bin/bash  
echo "MECBA - Combined 4 objectives"

populationsize=300
archivesize=300
maxEvaluations=60000
crossover=$1
mutation=$2
runs=$4
problem=$3
runHH=100
alpha=25 
beta=1

#java -Xms1024m -Xmx2048m -classpath dist/MECBA-HH-GDA.jar jmetal.experiments.Combined_NSGAII_4obj $populationsize $archivesize $maxEvaluations $crossover $mutation $runs $problem > resultado/time_Experiment_NSGAII_4obj-$problem.txt &
#java -Xms1024m -Xmx2048m -classpath dist/MECBA-HH-GDA.jar jmetal.experiments.Combined_IBEA_4obj $populationsize $archivesize $maxEvaluations $crossover $mutation $runs $problem > resultado/time_Experiment_IBEA_4obj-$problem.txt &
#java -Xms1024m -Xmx2048m -classpath dist/MECBA-HH-GDA.jar jmetal.experiments.Combined_SPEA2_4obj $populationsize $archivesize $maxEvaluations $crossover $mutation $runs $problem > resultado/time_Experiment_SPEA2_4obj-$problem.txt &
java -Xms1024m -Xmx2048m -classpath dist/MECBA-HH.jar jmetal.experiments.Combined_HH_4obj $populationsize $archivesize $maxEvaluations $crossover $mutation $runs  $runHH $alpha $beta $problem > resultado/time_Experiment_HH_4obj-$problem.txt &

