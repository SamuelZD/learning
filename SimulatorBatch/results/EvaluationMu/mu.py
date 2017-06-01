#!/usr/bin/env python
# SCRIPT TO PLOT RESULTS FOR THE SCENARIO WITH ONE CPU
# Author: Guillaume Artero Gallardo - LIP/ENSL

# IMPORT FUNCTIONS
from pylab import * 
import pylab as pl
from numpy import linspace, loadtxt, ones, convolve
import numpy as np
import matplotlib.pyplot as plt
import sys
import matplotlib


# LOAD DATA FILES
#filename = ["1limitedmodel15inf","1limitedmodel15sup","1limitedsim15inf","1limitedmodel15pon"]
filename = ["1limitedmodel15sup"]

typeModel = len(filename)
input_data = []
for i in range(0,typeModel):
	input_data.append(loadtxt(filename[i],float))


# COLLECT DATA
N = int(input_data[0][0,0])
mu = input_data[0][0,2]
per=[0.2,0.3,0.5]

# LINE TO CHANGE FOR EVERY PARTICULAR SCENARIO
min_coeff = per[0]
lamb = input_data[0][:,1]/min_coeff

q = []
b = []
s = [] #standard
t = [] #throught
mu = []

for i in range(0,typeModel):
	q_type = []
	b_type = []
	s_type = []
	t_type = []
	mu_type = []
	
	if i != 2 :
		for j in range(0,N):
			q_type.append(input_data[i][:,j*4+3])
			b_type.append(input_data[i][:,j*4+4])
			mu_type.append(input_data[i][:,j*4+2])
	else:		
		for j in range(0,N):
			q_type.append(input_data[i][:,j*5+0])
			b_type.append(input_data[i][:,j*5+1])
			s_type.append(input_data[i][:,j*5+2])
			t_type.append(input_data[i][:,j*5+3])
			mu_type.append(input_data[i][:,j*5+4])
		
	q.append(q_type)
	b.append(b_type)
	s.append(s_type)
	t.append(t_type)
	mu.append(mu_type)



print(lamb)
print(len(lamb))
print(len(q))
print(typeModel)
print(len(q[0][0]))
print(len(q[0][1]))
print(len(q[0]))
print(N)
#print(len(q[2][1]))
#print(t)

print("hello")



#colors = ['r','b','g','m','k','y']
colors = ['r','b','g','m','k']
#types = ["-+" , "-" , "." , "--" , ","]
types = ["+--" , ".--" , "-" , ","]
#labels = ["inferior: accurate","inferior: approx","inferior: piecewise", "superior : accurate", "superior : approx", "superior : piecewise","sim: 16","ponderation"]
labels = ["inferior: approx", "superior : accurate","sim: 16","Weidthing"]
#labels = ["model: 8","model: 16","model: 32","sim : 8","sim : 16","sim : 32"]
#q = [q_sim,q_model,q_modelOld]


#fgsize=(8, 6.7)
	
for i in range(0,N):

	fig = pl.figure()
	for j in range(0, typeModel):
		plot(lamb, 1/mu[j][i], colors[j] , markersize = 8 , linewidth = 1, label = "port "+str(i+1) + " " + labels[j])
	

	xlabel("Load (Mpps))", fontsize=25)
        ylabel("Average Processing Time (One Miss)", fontsize=20)
        
	grid(True)
	rc('text', usetex=True)
	plt.rc('font', **{'family': 'serif', 'serif': ['Computer Modern']})
	plt.rc("font",size=20)
	tight_layout()
	plt.legend(ncol=1,loc=4, fontsize = 10	)


	savefig("average_time_mu_UnMiss"+ str(i) +".pdf")
	
	
fig_total_mu = pl.figure()
for i in range(N):
	plot(lamb, 1/mu[0][i], colors[i] , markersize = 8 , linewidth = 1, label = "port "+str(i+1))

xlabel("Load (Mpps))", fontsize=25)
ylabel("Average Processing Time (One Miss)", fontsize=20)

grid(True)
rc('text', usetex=True)
plt.rc('font', **{'family': 'serif', 'serif': ['Computer Modern']})
plt.rc("font",size=20)
tight_layout()
plt.legend(ncol=1,loc=4, fontsize = 10	)

savefig("3ports_mu".pdf")
	
"""
for i in range(0,N):

	fig = pl.figure()
	for j in range(0, typeModel):
		plot(lamb, mu[j][i], colors[j%(typeModel)] + types[j/(typeModel)] , markersize = 8 , linewidth = 1, label = "port "+str(i+1) + " " + labels[j])
	

	xlabel("Load (Mpps))", fontsize=25)
        ylabel("Average $\mu$ (One Miss)", fontsize=20)
	#xlim(5, 10)
	#ylim(0.3,0.4)

	grid(True)
	rc('text', usetex=True)
	plt.rc('font', **{'family': 'serif', 'serif': ['Computer Modern']})
	plt.rc("font",size=20)
	tight_layout()
	plt.legend(ncol=1,loc=4, fontsize = 10	)


	savefig("average_mu"+ str(i) +".pdf")
"""
