#include <iostream>
#include <string>
#include <algorithm>

using namespace std;

class Solution {
public:
    bool isPalindrome(int x) {
        if(x < 0)
            return false;

        long long len, x1;
        len = 1;
        x1 = x;

        while(x){
            x = x / 10;
            len *= 10;
        }
        len = len / 10;

        while(x1){
            if(x1 % 10 != x1 / len)
                return false;

            x1 = x1 % len;
            x1 = x1 / 10;
            len = len/100 > 1? len/100:1;
        }

        return true;

    }
};

int main(){
    Solution solu;
    int a = 1000000001;
    cout << solu.isPalindrome(a) << endl;
    return 0;
}
