#bash!
generation=0
alpha=10
beta=1
runHH=100

./algtest_command_to_run_4obj.sh  TwoPointsCrossover SimpleInsertionMutation OO_MyBatis 30 $runHH $generation $alpha $beta &
./algtest_command_to_run_4obj.sh  TwoPointsCrossover SimpleInsertionMutation OA_AJHsqldb 30 $runHH $generation $alpha $beta &
./algtest_command_to_run_4obj.sh  TwoPointsCrossover SimpleInsertionMutation OO_BCEL 30 $runHH $generation $alpha $beta &
./algtest_command_to_run_4obj.sh  TwoPointsCrossover SimpleInsertionMutation OA_AJHotDraw 30 $runHH $generation $alpha $beta &
./algtest_command_to_run_2obj.sh  TwoPointsCrossover SimpleInsertionMutation OO_JHotDraw 30 $runHH $generation $alpha $beta&

./algtest_command_to_run_4obj.sh  TwoPointsCrossover SimpleInsertionMutation OA_HealthWatcher 30 $runHH $generation $alpha $beta&
./algtest_command_to_run_4obj.sh  TwoPointsCrossover SimpleInsertionMutation OA_TollSystems 30 $runHH $generation $alpha $beta&
./algtest_command_to_run_4obj.sh  TwoPointsCrossover SimpleInsertionMutation OO_JBoss 30 $runHH $generation $alpha $beta&


#./run_algtest.sh 300 25 300