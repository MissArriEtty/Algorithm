#include <iostream>
#include <conio.h>
#include <vector>
using namespace std;
int A[100], B[100], m, n;

double findM(){
	vector<int> num;
	int i, j;
	double ans = 0.0;
	i = 0;
	j = 0;
	while(i < m || j < n){
		if(i < m && j < n && A[i] < B[j]){
			num.push_back(A[i]);
			i++;
		}
		else if(i < m && j < n && A[i] >= B[j]){
			num.push_back(B[j]);
			j++;
		}
		else if(i < m){
			num.push_back(A[i]);
			i++;
		}
		else if(j < n){
			num.push_back(B[j]);
			j++;
		}
	}
//	vector<int>::iterator iter;
//	
//	for(iter = num.begin(); iter != num.end(); iter++){
//		cout << iter.num
//	}
//    for(int i = 0; i < m+n; i++)
//        cout << num[i] << " ";
//    cout << endl;
    if((m+n)%2 == 0){
    	ans = num[(m+n)/2-1] + num[(m+n)/2];
    	ans = ans * 0.5;
    }
    else{
    	ans = num[(m+n)/2];
    }
	return ans;
}

int main(){
    double ans;
    freopen("in.txt","r",stdin);
    cin >> m;
    for(int i = 0; i < m; i++)
        cin >> A[i];
    cin >> n;
    for(int i = 0; i < n; i++)
        cin >> B[i];
    ans = findM();
    cout << ans << endl;
	return 0;
}
