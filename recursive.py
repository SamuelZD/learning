#!python 2.7
#recursive join + 1 exit - 1

#client from 1 to M

M = 6
N = 8 # nb of port exsting
ports = [0 for i in range(N)]
numerator = [0 for i in range(N+1)]
seq = ""

import math

def dfs(client, ports, no):
    #the condition of stop : 
    if client == M :
        for port in range(N):
            ports[port]+=1
            numerator[computeNbPortNotEmpty(ports)]+=1
            
            if computeNbPortNotEmpty(ports) == 2:
                print(ports  + [(no + str(port+1))] )
            
            ports[port]-=1

            #print sequence
            #print(no+str(port+1))
            
        return

    for p in range(0,N):
        ports[p]+=1
        dfs(client + 1,ports, no + str(p+1))
        ports[p]-=1
        
def computeNbPortNotEmpty(ps):
    nb = 0
    for p in ps:
        if p !=0 :
            nb +=1
    return nb

def helper():
    dfs(1,ports, seq)

#the Formule for p2
def formulaTwo():
    sum = 0
    for i in range(M-1):
        sum += N*(N-1)*math.pow(2,M-2-i)
    print("P2: " + str(sum))

#Formule for P3
def formulaThree():
    nb = combination(N,3)*(math.pow(3,M) - combination(3,2)*math.pow(2,M) )
    print("p3 " + str(nb) )




#the formula for p3
#def formulaThree():
    
def combination(n,k):
    num = 1
    mom = 1
    
    #the bord
    if k == 1:
        return n

    for i in range(1,k+1):
        mom *= i
    for i in range(k):
        num *= (N-i)

    return num/mom

helper()
print(numerator)
print("sum " + str(sum(numerator)))
print(math.pow(N,M))
formulaTwo()
formulaThree()
