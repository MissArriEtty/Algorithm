#define _GNU_SOURCE

#include <iostream>
#include <stdio.h>
#include <pthread.h>
#include <unistd.h>
#include <string.h>
#include <sched.h>

using namespace std;

void *myfun(void *arg){

    cpu_set_t mask;
    cpu_set_t get;
    char buf[256];
    int i, j;
    int  num = sysconf(_SC_NPROCESSORS_CONF);
    cout << "System has " << num << " processor(s)." << endl;
    
    for(i = 0; i < num; i++){
        CPU_ZERO(&mask);
        CPU_SET(i, &mask);

        //pthread_self() 返回值是pthread_t, pthread_t在linux中是无符号长整型，即unsigned long
        //gettid是系统调用，返回值是pid_t,  在linux上是一个无符号整型。
        if(pthread_setaffinity_np(pthread_self(), sizeof(mask), &mask) < 0){
            cout << "set thread affinity failed." << endl;
	}

	CPU_ZERO(&get);
	if(pthread_getaffinity_np(pthread_self(), sizeof(get), &get) < 0){
	    cout << "get thread affinity failed." << endl;
	}
	for(j = 0; j < num; j++){
	    if(CPU_ISSET(j, &get))
	        cout << "thread " << (unsigned long)pthread_self() << "is running in processor " << j << endl;
	}
	j = 0;
        while(j++ < 10000)
            memset(buf, '\0', sizeof(buf));
    }
    pthread_exit(NULL);
}

int main(int argc, char *argv[]){

    pthread_t tid;
    
    if(pthread_create(&tid, NULL, myfun, NULL) != 0){
        printf("thread create failed.\n");
        return -1;
    }
    
    pthread_join(tid, NULL);
    return 0;
}
