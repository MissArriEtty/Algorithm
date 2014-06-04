#include <iostream>
#include <vector>
#include <unordered_map>

using namespace std;

class Solution {
public:
    vector<int> twoSum(vector<int> &numbers, int target) {
        unordered_map<int, int> rest;
        vector<int> ans;
        int t;
        for(int i = 0; i < numbers.size(); ++i){
            rest.insert(pair<int, int>(target - numbers[i], i));
        }

//        cout << "contains" << endl;
//        for(auto &iter : rest){
//            cout << iter.first << " " << iter.second << endl;
//        }

        for(int i = 0; i < numbers.size(); ++i){
            auto iter = rest.find(numbers[i]);
            if(iter != rest.end() && i != iter->second){
                //cout << "** " << iter->second << endl;
                t = i < iter->second? i: iter->second;
                ans.push_back(t+1);
                t = i > iter->second? i: iter->second;
                ans.push_back(t+1);
                break;
            }
        }
        return ans;
    }
};

int main(){
    vector<int> numbers, ans;
    int t, g, n;
    cin >> n >> g;
    while(n--){
        cin >> t;
        numbers.push_back(t);
    }
    Solution sol;
    ans = sol.twoSum(numbers, g);
    cout << ans[0] << " " << ans[1] << endl;
    return 0;
}
