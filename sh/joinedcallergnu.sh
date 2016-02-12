#bash!
problem=$1
objetives=$2
path='resultado/analise_'$objetives'obj-'

dataNsga=$path$problem-NSGAII'.csv'
dataSpea=$path$problem-SPEA2'.csv'
dataIBEA=$path$problem-IBEA'.csv'
dataRandom=$path$problem-Random'.csv'

dataHH1=$path$problem-HH1'-1.csv'
dataHH5=$path$problem-HH1'-5.csv'
dataHH10=$path$problem-HH1'-10.csv'
dataHH20=$path$problem-HH1'-20.csv'


dataHHAVG1=$path$problem-HHAVG'-1.csv'
dataHHAVG5=$path$problem-HHAVG'-5.csv'
dataHHAVG10=$path$problem-HHAVG'-10.csv'
dataHHAVG20=$path$problem-HHAVG'-20.csv'


echo $alg $problem $dataNsga $dataSpea $dataIBEA $dataRandom $dataHH1 $dataHHAVG1
export dataNsga
export dataSpea
export dataIBEA
export dataRandom

export dataHHAVG
export problem

export dataHH1
export dataHH5
export dataHH10
export dataHH20

export dataHHAVG1
export dataHHAVG5
export dataHHAVG10
export dataHHAVG20

./joinedgnuplotscript.sh
./joinedgnuplotscriptAVG.sh