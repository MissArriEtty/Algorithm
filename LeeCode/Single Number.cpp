/*
 * Given an array of integers, every element appears twice except for one. Find that single one.
 * time complexity O(n), space complexity O(1)
 */
#include <iostream>
using namespace std;

class Solution {
public:
    int singleNumber(int A[], int n) {
		int x = 0;
        for(int i = 0; i < n; ++i)
			x ^= A[i];
	    return x;
    }
};

int main(){
	Solution sol;
	int A[6] = {2, 3, 2, 3, 4};
	cout << sol.singleNumber(A, 5) << endl;
    return 0;
}