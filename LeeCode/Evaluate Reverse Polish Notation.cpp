/*
 *思想：栈的使用
 *注意判断负数的时候，符号和减号的区别
 *
*/

/*
  ["2", "1", "+", "3", "*"] -> ((2 + 1) * 3) -> 9
  ["4", "13", "5", "/", "+"] -> (4 + (13 / 5)) -> 6
*/

#include <iostream>
#include <stack>
#include <string>
#include <cctype>
#include <vector>
#include <stdlib.h>
#include <sstream>
#include <stdio.h>

using namespace std;


class Solution {

public:
    int getValue(int a, int b, string p){
        switch(p[0]){
            case '/': return b / a;
            case '*': return b * a;
            case '+': return b + a;
            case '-': return b - a;
            default: return -65535;
        }

    }

	bool isNum(const string &str){

        if(isdigit(str[0]) || (str[0] == '-' && str.length() != 1))
            return true;
        return false;
	}

    int evalRPN(vector<string> &tokens) {

        stack<int> st;
        string pTokens;
        int numA, numB, ans;

        ans = 0;
        for(vector<string>::size_type i = 0; i < tokens.size(); ++i){

        	pTokens = tokens.at(i);

        	if(isNum(pTokens)){

        		numA = atoi(pTokens.c_str());
        		st.push(numA);

        	}else{
        		numA = st.top();
        		st.pop();
        		numB = st.top() ;
        		st.pop();
        		ans = getValue(numA, numB, pTokens);
        		st.push(ans);
        	}
        }

        return st.top();
    }
};




int main(){
	freopen("Evaluate Reverse Polish Notation.txt", "r", stdin);
	Solution sol;
	string input, str;
	vector<string> tokens;
	while(getline(cin, input)){
	    istringstream conv(input);

        while(conv >> str)
            tokens.push_back(str);

		cout << sol.evalRPN(tokens) << endl;
	}


	return 0;
}
