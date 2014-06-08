#include <iostream>
#include <string>
#include <algorithm>
#include <string.h>

using namespace std;

class Solution {
public:
    string longestPalindrome(string s) {

        if(s.length() == 0 || s.length() == 1)
            return s;

        int p[2004], i, j;
        string newStr;
        //memset(p, 1, sizeof(p));

        newStr.push_back('$');
        for(i = 0; i < s.length(); ++i){

            //newStr[j] = '#';
            //newStr[j + 1] = s[i];
            newStr.push_back('#');
            newStr.push_back(s[i]);
        }
        newStr.push_back('#');

//        for(i = 1; i < newStr.length(); ++i)
//            cout << newStr[i] << " ";
//        cout << endl;

        int idx, mx;
        idx = 0;
        mx = 0;
        for(i = 1; i < newStr.length(); ++i){
            if(mx > i)
                p[i] = min(p[idx * 2 - i], mx - i);
            else
                p[i] = 1;
            for(; i - p[i] >= 0 && i + p[i] < newStr.length() &&
                newStr[i + p[i]] == newStr[i - p[i]]; p[i]++);
            if(p[i] > mx){
                mx = p[i] + i;
                idx = i;
            }
        }


//        for(i = 1; i < newStr.length(); ++i)
//            cout << p[i] << " ";
//        cout << endl;

        mx = 0;
        for(i = 1; i < newStr.length(); ++i)
            if(p[i] > mx){
                mx = p[i];
                idx = i;
            }

        //cout << "idx = " << idx << " max = " << mx << endl;

        string ans;
        for(i = idx + p[idx] - 1; i > idx - p[idx]; --i){
            if(newStr[i] != '#')
                ans.push_back(newStr[i]);
        }
        return ans;

    }
};

int main(){
    Solution solu;
    string str = "aaaa";
    cout << solu.longestPalindrome(str) << endl;
    return 0;
}
