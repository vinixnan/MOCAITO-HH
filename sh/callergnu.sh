#bash!
alg=$2
problem=$1
data='resultado/analise_2obj-'$problem-$alg'.csv'
echo $alg $problem $data
export data
export alg
export problem
./gnuplotscript.sh