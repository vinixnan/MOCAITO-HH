#!/usr/bin/gnuplot
dataNsga=system("echo $dataNsga")
dataSpea=system("echo $dataSpea")
dataIBEA=system("echo $dataIBEA")
dataRandom=system("echo $dataRandom")
problem=system("echo $problem")

dataHH1=system("echo $dataHH1")
dataHH5=system("echo $dataHH5")
dataHH10=system("echo $dataHH10")
dataHH20=system("echo $dataHH20")

unset print;
set terminal png size 2048,1152;
set output "resultado/".problem."_hyp.png";
set key left top;
set title "Hypervolume ".problem
set grid; 
plot dataNsga u 1:2 with lines title "NSGAII" lt rgb "red", dataSpea u 1:2 with lines title "SPEA2" lt rgb "green", dataIBEA u 1:2 with lines title "IBEA" lt rgb "blue", dataRandom u 1:2 with lines title "Random" lt rgb "violet", dataHH1 u 1:2 with lines title "HH1", dataHH5 u 1:2 with lines title "HH5", dataHH10 u 1:2 with lines title "HH10", dataHH20 u 1:2 with lines title "HH20" lt rgb "black";
set xlabel "Generations";
set ylabel "Hypervolume";
replot;

set terminal png size 2048,1152;
set output "resultado/".problem."_r2.png";
set key right top;
set title "R2 ".problem
plot dataNsga u 1:3 with lines title "NSGAII" lt rgb "red", dataSpea u 1:3 with lines title "SPEA2" lt rgb "green", dataIBEA u 1:3 with lines title "IBEA" lt rgb "blue", dataRandom u 1:3 with lines title "Random" lt rgb "violet", dataHH1 u 1:3 with lines title "HH1", dataHH5 u 1:3 with lines title "HH5", dataHH10 u 1:3 with lines title "HH10", dataHH20 u 1:3 with lines title "HH20" lt rgb "black";
set xlabel "Generations";
set ylabel "R2";
set grid; 
replot;

set terminal png size 2048,1152;
set output "resultado/".problem."_rni.png";
set key left top;
set title "Non Dominated ".problem
plot dataNsga u 1:4 with lines title "NSGAII" lt rgb "red", dataSpea u 1:4 with lines title "SPEA2" lt rgb "green", dataIBEA u 1:4 with lines title "IBEA" lt rgb "blue", dataRandom u 1:4 with lines title "Random" lt rgb "violet", dataHH1 u 1:4 with lines title "HH1", dataHH5 u 1:4 with lines title "HH5", dataHH10 u 1:4 with lines title "HH10", dataHH20 u 1:4 with lines title "HH20" lt rgb "black";
set xlabel "Generations";
set ylabel "Non Dominated";
set grid; 
replot;
