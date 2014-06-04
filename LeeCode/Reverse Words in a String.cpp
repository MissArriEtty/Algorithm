#include <iostream>
#include <vector>
#include <string>
#include <stack>
#include <stdio.h>

using namespace std;


void reverseWords(string &s){

    string word;
    string reWords;
    char chr[1000];
    stack<string> st;
    int idx;

    //cout << "origin = " << s << endl;

    bool haveWord;
    haveWord = false;
    idx = 0;

    word.clear();
    
    for(int i = 0; i < s.length(); ++i){

        if(s[i] == ' '){
            if(haveWord){
               chr[idx] = '\0';
               word = chr;
               //cout << word << endl;
               st.push(word);
               idx = 0;
               haveWord = false;
            }
        }
        else{
            chr[idx++] = s[i];
            haveWord = true;
        }
    }
    
    if(haveWord){
    	chr[idx] = '\0';
    	word = chr;
    	st.push(word);
    }

    haveWord = false;

    while(!st.empty()){

        word = st.top();
        st.pop();

        if(!haveWord){
            reWords = word;
            haveWord = true;
        }
        else
            reWords += " " + word;

    }
    s = reWords;
    return;
}

int main(){

    freopen("in.txt", "r", stdin);
    freopen("out.txt", "w", stdout);
    string s;
    while(getline(cin, s)){
    	s = reverseWords(s);
    	cout << s;
    	//getchar();
    }
    return 0;
}
