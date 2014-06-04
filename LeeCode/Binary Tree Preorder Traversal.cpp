/**
 * Definition for binary tree
 * struct TreeNode {
 *     int val;
 *     TreeNode *left;
 *     TreeNode *right;
 *     TreeNode(int x) : val(x), left(NULL), right(NULL) {}
 * };
 */
class Solution {
public:
    vector<int> preorderTraversal(TreeNode *root) {
        TreeNode *cur;
        stack<TreeNode*> st;
        vector<int> ans;

        st.push(root);
        while(!st.empty() && root != NULL){
            cur = st.top();
            st.pop();
            ans.push_back(cur->val);

            if(cur->right != NULL)
                st.push(cur->right);
            if(cur->left != NULL)
                st.push(cur->left);

        }
        return ans;
    }
};
