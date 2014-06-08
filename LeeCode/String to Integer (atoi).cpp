#include <iostream>
#include <string>
#include <limits.h>
using namespace std;

class Solution {
public:
    int atoi(const char *str) {

        long long ans = -1, order;
        bool first;
        int i = 0, neg;
        order = 1;
        neg = 1;
        first = false;

        while(str[i] != '\0'){

            if(str[i] == ' ' && !first){
                i++;
                continue;
            }

            if(str[i] >= '0' && str[i] <= '9'){

                first = true;
                if(ans == -1 && str[i] != '0'){
                    ans = (str[i] - '0');
                    order *= 10;
                }else if(ans != -1){
                    ans = ans * order + (str[i] - '0');
                }

            }else if(str[i] == '-' && !first){
                neg = -1;
                first = true;
            }else if(str[i] == '+' && !first){
                first = true;
            }else if(first && ans != -1){
                break;
            }else
                return 0;

            if(ans * neg >= INT_MAX)
                return INT_MAX;

            if(ans * neg <= INT_MIN)
                return INT_MIN;
            i++;
        }


        if(ans == -1)
            return 0;
        if(neg == -1) return -ans;
        return ans;

    }
};

int main(){
    Solution solu;
    string s = "1";
    cout << solu.atoi(s.c_str()) << endl;
    cout << atoi(s.c_str()) << endl;
    return 0;
}
