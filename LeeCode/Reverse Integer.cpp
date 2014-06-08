class Solution {
public:
    int reverse(int x) {
        int ans;
        bool neg;
        string str, rev;

        ostringstream conv;
        conv << x;
        str = conv.str();

        neg = false;
        for(int i = str.length() - 1; i >= 0; --i){

            if(str[i] == '-'){
                neg = true;
                break;
            }
            rev.push_back(str[i]);
        }

        ans = atoi(rev.c_str());
        if(neg) ans = -ans;
        return ans;

    }
};
