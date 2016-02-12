#bash!
generation=0
alpha=10
beta=1
runHH=100
mTax=0.02
nr=$1



function mk(){
	alg=$1
	mTax=$2
	nr=$3
	dir="resultado/"$alg"_"$nr"_"$mTax"/"
	mkdir $dir
	wait
	cd $dir
	mkdir OA_AJHotDraw_Comb_2obj
	mkdir OA_AJHsqldb_Comb_2obj
	mkdir OA_HealthWatcher_Comb_2obj
	mkdir OA_TollSystems_Comb_2obj
	mkdir OO_BCEL_Comb_2obj
	mkdir OO_JBoss_Comb_2obj
	mkdir OO_JHotDraw_Comb_2obj
	mkdir OO_MyBatis_Comb_2obj
	wait
	cd ../../
	wait
}


mk 'moead' $mTax $nr
./algtest_command_to_run_2obj.sh  TwoPointsCrossover SwapMutation OO_MyBatis 30 $runHH $generation $alpha $beta $nr $mTax &
./algtest_command_to_run_2obj.sh  TwoPointsCrossover SwapMutation OA_AJHsqldb 30 $runHH $generation $alpha $beta $nr $mTax &
./algtest_command_to_run_2obj.sh  TwoPointsCrossover SwapMutation OO_BCEL 30 $runHH $generation $alpha $beta $nr $mTax &
./algtest_command_to_run_2obj.sh  TwoPointsCrossover SwapMutation OA_AJHotDraw 30 $runHH $generation $alpha $beta $nr $mTax &
./algtest_command_to_run_2obj.sh  TwoPointsCrossover SwapMutation OO_JHotDraw 30 $runHH $generation $alpha $beta $nr $mTax &
./algtest_command_to_run_2obj.sh  TwoPointsCrossover SwapMutation OA_HealthWatcher 30 $runHH $generation $alpha $beta $nr $mTax &
./algtest_command_to_run_2obj.sh  TwoPointsCrossover SwapMutation OO_JBoss 30 $runHH $generation $alpha $beta $nr $mTax &


#./algtest_command_to_run_2obj.sh  TwoPointsCrossover SwapMutation OA_TollSystems 30 $runHH $generation $alpha $beta&
#./run_algtest.sh 300 25 300
