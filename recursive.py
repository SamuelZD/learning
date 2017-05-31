#!python 2.7
#recursive join + 1 exit - 1

#client from 1 to M

M = 6
N = 7 # nb of port exsting
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
            """
            if computeNbPortNotEmpty(ports) == 2:
                print(ports)
            """
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
def formula():
    sum = 0
    for i in range(M-1):
        sum += N*(N-1)*math.pow(2,M-2-i)
    print("P2: " + str(sum))


helper()
print(numerator)
print("sum " + str(sum(numerator)))
print(math.pow(N,M))
formula()
