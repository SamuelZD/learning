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
from matplotlib import pyplot


# LOAD DATA FILES
filename = ["1limitedmodelE3","1limitedsim3","1limitedmodel3"]

typeModel = len(filename)
input_data = []
for i in range(0,typeModel):
	input_data.append(loadtxt(filename[i],float))


# COLLECT DATA
N = int(input_data[0][0,0])
mu = input_data[0][0,2]
per=[0.15, 0.2, 0.25, 0.4]

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
	
	if i != 1: #model
		for j in range(0,N):
			mu_type.append(input_data[i][:,j*4+2])
			q_type.append(input_data[i][:,j*4+3])
			b_type.append(input_data[i][:,j*4+4])
	else: # simu
		for j in range(0,N):
			mu_type.append(input_data[i][:,j*5+4])
			q_type.append(input_data[i][:,j*5+0])
			b_type.append(input_data[i][:,j*5+1])
		
		
	q.append(q_type)
	b.append(b_type)
	s.append(s_type)
	t.append(t_type)
	mu.append(mu_type)
	
	"""
	mu_total = []
	for i in range(len(lamb)):
		mu_total.append(1/mu[0][0][i] +  1/mu[0][1][i] + 1/ mu[0][2][i])
	mu.append(mu_total)
	"""

print(lamb)
print(len(lamb))
#print(len(q[2][1]))
#print(t)

print("hello")



#colors = ['r','b','g','m','k','y']
colors = ['r','b','g','m','k']
#types = ["-+" , "-" , "." , "--" , ","]
types = ["-" , "+" ,"1--", "-" , ","]
#labels = ["inferior: 1","inferior: 4","inferior: 8","inferior: 16","inferior: 32", "sim : 1","sim : 4","sim : 8","sim : 16","sim : 32"]
label = ["Model Enum","Simulation","Model 1"]
#q = [q_sim,q_model,q_modelOld]

#fgsize=(8, 6.7)

#queue size
fig_total_mu = pl.figure()

for i in range(N):
	for t in range(typeModel):
		plot(lamb, q[t][i], colors[i] + types[t] , markersize = 8 , linewidth = 1)
		
xlabel("Load (Mpps)", fontsize=25)
ylabel("Average Queue Size", fontsize=25)

xlim(2,13)


grid(True)
rc('text', usetex=True)
plt.rc('font', **{'family': 'serif', 'serif': ['Computer Modern']})
plt.rc("font",size=20)
tight_layout()
#plt.legend(ncol=1,loc=4, fontsize = 14)


savefig("model_1_q.pdf")



# loss
fig_total_mu_2 = pl.figure()
for i in range(N):
	for t in range(typeModel):
		plot(lamb, b[t][i], colors[i] + types[t] , markersize = 8 , linewidth = 1)

xlabel("Load (Mpps)", fontsize=25)
ylabel("Loss Rate", fontsize=25)

plot_lines = []
for i in range(N):
        l, = plot(0,0,colors[i],markersize = 8, linewidth = 1, label = "port "+ str(i+1))
        plot_lines.append(l)

plot_m = []
for i in range(typeModel):
        l, =plot (0,0,"k"+ types[i],markersize = 8, linewidth = 1, label = label[i])
        plot_m.append(l)

xlim(2,13)

grid(True)
rc('text', usetex=True)
plt.rc('font', **{'family': 'serif', 'serif': ['Computer Modern']})
plt.rc("font",size=20)
tight_layout()
legend1 = plt.legend( plot_lines,["Port 1","Port 2","Port 3","Port 4"] ,ncol=1,bbox_to_anchor=(0,0.8),loc=2, title = "Port No." ,fontsize = 15)
plt.legend(plot_m,label ,ncol=1,loc=2 ,fontsize = 15)
pyplot.gca().add_artist(legend1)


savefig("model_1_b.pdf")


# PLOT DATA
fig3 = pl.figure()


for i in range(0,N):
	for t in range(0, typeModel):
		plot(lamb, q[t][i]/(lamb*(1-b[t][i])*per[i]), colors[i] + types[t] , markersize = 8 , linewidth = 1)

# SET FIGURE PARAMETERS
xlabel("Load (Mpps)", fontsize=25)
ylabel("Average Sojourn Time ($\mu{}s)$", fontsize=25)
#title("Asym. system with $N=$ "+str(N)+" and $\mu{}=$"+str(mu))

xlim(2,13)

grid(True)
rc('text', usetex=True)
plt.rc('font', **{'family': 'serif', 'serif': ['Computer Modern']})
plt.rc("font",size=20)
tight_layout()
plt.legend(ncol=1,loc=4, fontsize = 12	)

savefig("model_1_s.pdf")


fig_total_mu_3 = pl.figure()

for i in range(N):
	for t in range(typeModel):
		plot(lamb, 1/mu[t][i], colors[i] + types[t] , markersize = 8 , linewidth = 1, label = label[t]+ ": $\overline{T_p}_" + str(i+1) + "$")	
xlabel("Load (Mpps)", fontsize=25)
ylabel("Average Processing Time for a Packet")

grid(True)
rc('text', usetex=True)
plt.rc('font', **{'family': 'serif', 'serif': ['Computer Modern']})
plt.rc("font",size=20)
tight_layout()
plt.legend(ncol=1,loc=4, fontsize = 10	)

savefig("4ports_mu.pdf")



# throughput
"""
for i in range(0,N):
	fig = pl.figure()
	for j in range(5, typeModel):
		plot(lamb, t[j][i], colors[j%5] + types[j/5] , markersize = 8 , linewidth = 1, label = "port "+str(i+1) + " " + labels[j])
	
	xlabel("Load (Mpps)", fontsize=25)
	ylabel("$\mu(Mpps)$", fontsize=25)


	grid(True)
	rc('text', usetex=True)
	plt.rc('font', **{'family': 'serif', 'serif': ['Computer Modern']})
	plt.rc("font",size=20)
	tight_layout()
	plt.legend(ncol=1,loc=2, fontsize = 10	)


	savefig("throughput_"+ str(i) +".pdf")
"""


"""
# PLOT DATA
fig1 = pl.figure(figsize=(8, 6.7))



for i in range(0,N):
	for j in range(0, typeModel - 1):
		plot(lamb, q[j][i], colors[j] + types[i] , markersize = 8 , linewidth = 1, label = "port "+str(i+1) + " " + labels[j])

# SET FIGURE PARAMETERS
#xlabel("$\lambda{}_{in}$ (Mpps)", fontsize=25)
xlabel("Load (Mpps)", fontsize=25)
ylabel("Average Queue Size (pkt)", fontsize=25)
#title("Asym. system with $N=$ "+str(N)+" and $\mu{}=$"+str(mu))



#xlim(0.5,3)

grid(True)
rc('text', usetex=True)
plt.rc('font', **{'family': 'serif', 'serif': ['Computer Modern']})
plt.rc("font",size=20)
tight_layout()
plt.legend(ncol=1,loc=4, fontsize = 10	)


savefig("model_queuesize.pdf")



# PLOT DATA
fig2 = pl.figure(figsize=(8, 6.7))

for i in range(0,N):
	for j in range(0, typeModel - 1):
		plot(lamb, b[j][i], colors[j] + types[i] , markersize = 8 , linewidth = 1 , label = "port "+str(i+1)+ " " + labels[j])
		#plot(lamb, (lamb*per[i] - t[j][i])/(lamb*per[i]), colors[j] + "," , markersize = 8 , linewidth = 1 , label = "port "+str(i+1)+" - " + labels[j] + " validation by throughput")
		#print (lamb*per[i] - t[j][i])/(lamb*per[i])


# SET FIGURE PARAMETERS
xlabel("Load (Mpps)", fontsize=25)
ylabel("Loss Rate", fontsize=25)
#title("Asym. system with $N=$ "+str(N)+" and $\mu{}=$"+str(mu))

#xlim(0.5,3)

grid(True)
rc('text', usetex=True)
plt.rc('font', **{'family': 'serif', 'serif': ['Computer Modern']})
plt.rc("font",size=20)
tight_layout()
plt.legend(ncol=1,loc=2, fontsize = 9	)

savefig("model_blocking.pdf")






# PLOT DATA
fig4 = pl.figure(figsize=(8, 6.7))



for i in range(0,N):
	for j in range(0, typeModel-1):
		plot(lamb, s[j][i], colors[j] + types[i] , markersize = 8 , linewidth = 1, label = "port "+str(i+1)+ " " + labels[j])

# SET FIGURE PARAMETERS
#xlabel("$\lambda{}_{in}$ (Mpps)", fontsize=25)
xlabel("Load (Mpps)", fontsize=25)
ylabel("Standard Average Queue Size (pkt)", fontsize=25)
#title("Asym. system with $N=$ "+str(N)+" and $\mu{}=$"+str(mu))



#xlim(0.5,3)

grid(True)
rc('text', usetex=True)
plt.rc('font', **{'family': 'serif', 'serif': ['Computer Modern']})
plt.rc("font",size=20)
tight_layout()
plt.legend(ncol=1,loc=1, fontsize = 10)


savefig("model_queuesize_std.pdf")


# PLOT DATA
fig5 = pl.figure(figsize=(8, 6.7))



for i in range(0,N):
	for j in range(0, typeModel - 1):
		plot(lamb, t[j][i], colors[j] + types[i] , markersize = 8 , linewidth = 1, label = "port "+str(i+1)+ " " + labels[j])

# SET FIGURE PARAMETERS
#xlabel("$\lambda{}_{in}$ (Mpps)", fontsize=25)
xlabel("Load (Mpps)", fontsize=25)
ylabel("Throughput (Mpps)", fontsize=25)
#title("Asym. system with $N=$ "+str(N)+" and $\mu{}=$"+str(mu))



#xlim(0.5,3)

grid(True)
rc('text', usetex=True)
plt.rc('font', **{'family': 'serif', 'serif': ['Computer Modern']})
plt.rc("font",size=20)
tight_layout()
plt.legend(ncol=1,loc=4, fontsize = 14	)


savefig("model_throughput.pdf")
"""

