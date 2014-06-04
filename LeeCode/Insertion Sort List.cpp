/**
 * Sort a linked list using insertion sort.
 *
 * Definition for singly-linked list.
 * struct ListNode {
 *     int val;
 *     ListNode *next;
 *     ListNode(int x) : val(x), next(NULL) {}
 * };
 */
#include <iostream>
using namespace std;

struct ListNode {
    int val;
    ListNode *next;
    ListNode(int x) : val(x), next(NULL) {}
};

class Solution {
public:
    ListNode *insertionSortList(ListNode *head) {

        if(head == NULL || head->next == NULL)
           return head;

        ListNode *pre_p, *pre_curr, *curr, *p;

        pre_curr = head;
        curr = head->next;

        while(curr != NULL){
            p = head;
            pre_p = NULL;

            while(p != curr){
                if(p->val > curr->val){

                    pre_curr->next = curr->next;
                    curr->next = p;
                    if(p == head)
                        head = curr;
                    else
                        pre_p->next = curr;
                    curr = pre_curr;
                    break;
                }
                pre_p = p;
                p = p->next;
            }
            pre_curr = curr;
            curr = curr->next;
        }
        return head;
    }
};

int main(){

    freopen("in.txt", "r", stdin);
    ListNode *head = NULL, *cur;
    Solution sol;
    int a;
    while(cin>>a){
        //cout << a << endl;
        if(!head){
            head = new ListNode(a);
            cur = head;
        }
        else{
            ListNode *p = new ListNode(a);
            cur->next = p;
            cur = p;
        }
    }



    head = sol.insertionSortList(head);

    while(head != NULL){
        cout << head->val << " ";
        cur = head;
        head = head->next;
        delete cur;
    }
    cout << endl;
    return 0;
}
