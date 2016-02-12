#!/usr/bin/gnuplot
dataNsga=system("echo $dataNsga")
dataSpea=system("echo $dataSpea")
dataIBEA=system("echo $dataIBEA")
dataRandom=system("echo $dataRandom")
problem=system("echo $problem")

dataHHAVG1=system("echo $dataHHAVG1")
dataHHAVG5=system("echo $dataHHAVG5")
dataHHAVG10=system("echo $dataHHAVG10")
dataHHAVG20=system("echo $dataHHAVG20")

unset print;
set terminal png size 2048,1152;
set output "resultado/".problem."AVG_hyp.png";
set key left top;
set title "Hypervolume ".problem
set grid; 
plot dataNsga u 1:2 with lines title "NSGAII" lt rgb "red", dataSpea u 1:2 with lines title "SPEA2" lt rgb "green", dataIBEA u 1:2 with lines title "IBEA" lt rgb "blue", dataRandom u 1:2 with lines title "Random" lt rgb "violet", dataHHAVG1 u 1:2 with lines title "AVG1", dataHHAVG5 u 1:2 with lines title "AVG5", dataHHAVG10 u 1:2 with lines title "AVG10", dataHHAVG20 u 1:2 with lines title "AVG20" lt rgb "black";
set xlabel "Generations";
set ylabel "Hypervolume";
replot;

set terminal png size 2048,1152;
set output "resultado/".problem."AVG_r2.png";
set key right top;
set title "R2 ".problem
plot dataNsga u 1:3 with lines title "NSGAII" lt rgb "red", dataSpea u 1:3 with lines title "SPEA2" lt rgb "green", dataIBEA u 1:3 with lines title "IBEA" lt rgb "blue", dataRandom u 1:3 with lines title "Random" lt rgb "violet", dataHHAVG1 u 1:3 with lines title "AVG1", dataHHAVG5 u 1:3 with lines title "AVG5", dataHHAVG10 u 1:3 with lines title "AVG10", dataHHAVG20 u 1:3 with lines title "AVG20" lt rgb "black";
set xlabel "Generations";
set ylabel "R2";
set grid; 
replot;

set terminal png size 2048,1152;
set output "resultado/".problem."AVG_rni.png";
set key left top;
set title "Non Dominated ".problem
plot dataNsga u 1:4 with lines title "NSGAII" lt rgb "red", dataSpea u 1:4 with lines title "SPEA2" lt rgb "green", dataIBEA u 1:4 with lines title "IBEA" lt rgb "blue", dataRandom u 1:4 with lines title "Random" lt rgb "violet", dataHHAVG1 u 1:4 with lines title "AVG1", dataHHAVG5 u 1:4 with lines title "AVG5", dataHHAVG10 u 1:4 with lines title "AVG10", dataHHAVG20 u 1:4 with lines title "AVG20" lt rgb "black";
set xlabel "Generations";
set ylabel "Non Dominated";
set grid; 
replot;
