/**
 * Definition for binary tree
 * struct TreeNode {
 *     int val;
 *     TreeNode *left;
 *     TreeNode *right;
 *     TreeNode(int x) : val(x), left(NULL), right(NULL) {}
 * };
 */
#include <iostream>
#include <vector>
#include <stack>

using namespace std;

struct TreeNode {
    int val;
    TreeNode *left;
    TreeNode *right;
    TreeNode(int x) : val(x), left(NULL), right(NULL) {}
};

class Solution {
public:
    vector<int> postorderTraversal(TreeNode *root) {

        TreeNode *cur, *pre;
        stack<TreeNode *> st;
        vector<int> ans;

        pre = NULL;
        st.push(root);
        while(!st.empty() && root != NULL){
            cur = st.top();

            if((cur->left == NULL && cur->right == NULL) ||
               pre != NULL && (pre == cur->left || pre == cur->right)){
                   ans.push_back(cur->val);
                   st.pop();
                   pre = cur;
               }
            else{
                if(cur->right != NULL)
                    st.push(cur->right);
                if(cur->left != NULL)
                    st.push(cur->left);
            }
        }

        return ans;
    }

};

