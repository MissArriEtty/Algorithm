#include <iostream>
#include <string>
using namespace std;

class Solution {
public:
    string convert(string s, int nRows) {
        if(nRows == s.length() || nRows == 1)
            return s;

        string ans;
        for(int i = 0; i < nRows; ++i){
            for(int j = i; j < s.length(); j += (2 * nRows - 2)){

                ans.push_back(s[j]);

                if(i != 0 && i != nRows - 1 && (j + (2 * nRows - i * 2 - 2)) < s.length())
                    ans.push_back(s[j + (2 * nRows - i * 2 - 2)]);

            }
        }
        return ans;
    }
};

int main(){
    Solution solu;
    string s = "PAYPALISHIRING";
    cout << solu.convert(s, 3) << endl;
    return 0;
}
