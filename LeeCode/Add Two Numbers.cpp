/**
 * Definition for singly-linked list.
 * struct ListNode {
 *     int val;
 *     ListNode *next;
 *     ListNode(int x) : val(x), next(NULL) {}
 * };
 */
#include <iostream>
#include <vector>
using namespace std;

struct ListNode {
    int val;
    ListNode *next;
    ListNode(int x) : val(x), next(NULL) {}
};
class Solution {
public:
    ListNode *addTwoNumbers(ListNode *l1, ListNode *l2) {
        if(l1 == NULL && l2 == NULL)
            return NULL;
        if(l1 == NULL)
            return l2;
        if(l2 == NULL)
            return l1;

        vector<int> sum;
        int carry, a, i;
        ListNode *p, *head;

        for(p = l1; p->next != NULL; p = p->next)
            sum.push_back(p->val);

        carry = 0;
        for(i = 0, p = l2; p->next != NULL; p = p->next, ++i){
            if(i < sum.size())
                a = sum[i];
            else
                a = 0;
            a += p->val + carry;
            carry = a / 10;
            if(i < sum.size())
                sum[i] = a % 10;
            else
                sum.push_back(a % 10);

        }
        while(carry){
            if(i < sum.size()){
                a = sum[i] + carry;
                sum[i] = a % 10;
                carry = a / 10;
            }else{
                sum.push_back(carry);
                carry = 0;
            }
            i++;
        }

        head = NULL;
        for(i = 0; i < sum.size(); ++i){

            if(head == NULL){
                head = new ListNode(sum[i]);
                p = head;
            }else{
                p->next = new ListNode(sum[i]);
                p = p->next;
            }
        }
        return head;

    }
};
void BuildList(ListNode *&l1, ListNode *&l2){
    ListNode *p;
    for(int i = 0; i < 1; i++){
        if(l1 == NULL){
                l1 = new ListNode(i+1);
                p = l1;
            }else{
                p->next = new ListNode(i+1);
                p = p->next;
            }
    }

    for(int i = 0; i < 1; i++){
        if(l2 == NULL){
                l2 = new ListNode(i+1);
                p = l2;
            }else{
                p->next = new ListNode(i+1);
                p = p->next;
            }
    }
}
int main(){
    Solution solu;
    ListNode *l1, *l2;
    l1 = NULL;
    l2 = NULL;
    BuildList(l1, l2);
    solu.addTwoNumbers(l1, l2);
    return 0;
}
