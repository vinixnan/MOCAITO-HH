#!/usr/bin/gnuplot
data=system("echo $data")
alg=system("echo $alg")
problem=system("echo $problem")

unset print;
set terminal png size 480,320;
set output data."_".problem."_hyp.png";
set key left top;
plot data u 1:2 with lines title "Hypervolume ".alg." ".problem;
set xlabel "Generations";
set ylabel "Hypervolume";
set grid; 
replot;

set terminal png size 480,320;
set output data."_".problem."_r2.png";
set key left top;
plot data u 1:3 with lines title "R2 ".alg." ".problem;
set xlabel "Generations";
set ylabel "R2";
set grid; 
replot;

set terminal png size 480,320;
set output data."_".problem."_rni.png";
set key left top;
plot data u 1:4 with lines title "Non Dominated ".alg." ".problem;
set xlabel "Generations";
set ylabel "Non Dominated";
set grid; 
replot;
