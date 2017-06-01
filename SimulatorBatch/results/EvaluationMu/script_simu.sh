# SCRIPT FOR THE SCENARIO WITH ONE CPU
# Author: Guillaume Artero Gallardo - LIP/ENSL

# set script parameters
tsim=500000
n=4
k=128
m=1
modelTraceFile=1limitedmodel
simTraceFile=1limitedsim
scenario=scenarioA

directory=../../results/EvaluationMu

resl=traces
# Erase the trace files
cd ../../bin
if [ -d "$resl" ]; 
then echo "trace exsit" ;
else	mkdir "$resl" ;
fi

cd traces
rm -f $simTraceFile*
rm -f $modeltraceFile*
cd ..

# loop on the values of mu that are equal to the ones used in the simulation archive



for batch in 3

do

#for lamb in `LANG=en_US seq 6 0.1 12`;
for lamb in `LANG=en_US seq 0.5 0.1 13`;

do
	# run the simulation and trace the results => should be long enough to get accurate simulation results
	java scenario.ScenarioBatchMu -n $n -Lambda $lamb -tsim $tsim -k $k -m $m -traceSim $simTraceFile$batch -traceModel $modelTraceFile$batch -extralot $batch -scenario $scenario
	

done
	cd traces

	cp $simTraceFile* $directory
	cp $modelTraceFile* $directory
	
	cd ..


done


# copy the trace file in the right directory
cd traces


# plot the results using python
cd $directory
python paper.py
