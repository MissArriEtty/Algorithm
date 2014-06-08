#include <iostream>
#include <string>
using namespace std;

int nextt[50];

void GetNext(string s){
    int len = s.length();
    int k = 0, j = 1;
    nextt[0] = -1;
    nextt[1] = 0;
    while(j < len - 1){
        if(s[k] == s[j]){
            nextt[j + 1] = k + 1;
            j++;
            k++;
        }else if(k == 0){
            nextt[j + 1] = 0;
            j++;
        }else
            k = nextt[k];
    }

}

class Solution {
public:
    int lengthOfLongestSubstring(string s) {
    	int alpha[27], maxLen[10000];
    	int ans;
    	memset(alpha, 255, sizeof(alpha));
    	memset(maxLen, 0, sizeof(maxLen));
    	
    	transform(s.begin(), s.end(), s.begin(), tolower);
    	maxLen[0] = 1;
    	ans = 1;
    	for(int i = 1; i < s.length(); ++i){
    		if(alpha[s[i] - 'a'] == -1){
    			alpha[s[i] - 'a'] = i;
    			maxLen[i] = maxLen[i-1] + 1;
    		}else{
    			
    			maxLen[i] = i - alpha[s[i] - 'a'];
    			alpha[s[i] - 'a'] = i;
			}
			if(maxLen[i] > ans)
			    ans = maxLen[i];
    	}
    	return ans;
    }
};

int main(){
    string s;
    cin >> s;
    GetNext(s);
    for(int i = 0; i < s.length(); ++i)
        cout << nextt[i] << " ";
    cout << endl;
    return 0;
}


