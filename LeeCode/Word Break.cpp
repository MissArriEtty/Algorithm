class Solution {
public:
    bool wordBreak(string s, unordered_set<string> &dict) {
        if(s == "")
            return false;
        if(dict.find(s) != dict.end())
            return true;

        vector<bool> f(s.length(), false);
        if(dict.find(s.substr(0, 1)) != dict.end())
            f[0] = true;

        for(int i = 1; i < s.length(); ++i){
            if(dict.find(s.substr(0,i + 1)) != dict.end()){
                f[i] = true;
                continue;
            }
            for(int j = 0; j < i; ++j){
                if(f[j] && dict.find(s.substr(j + 1, i - j)) != dict.end()){
                    f[i] = true;
                    break;
                }
            }
        }
        return f[s.length() - 1];

    }
};
