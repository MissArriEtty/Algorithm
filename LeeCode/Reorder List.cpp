/**
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
    ListNode* reverseList(ListNode *head){
        if(head == NULL || head->next == NULL)
            return head;

        ListNode *curNext, *cur, *p;

        for(cur = head, curNext = head->next; curNext; cur = curNext, curNext = p){
            p = curNext->next;
            curNext->next = cur;
        }

        head->next = NULL;
        return cur;
    }

    void reorderList(ListNode *head) {

        if(head == NULL || head->next == NULL || head->next->next == NULL)
            return ;
        ListNode *tail, *media, *p1, *p2, *p;
        tail = media = head;

        while(tail && tail->next){
            media = media->next;
            tail = tail->next->next;
        }

        p2 = reverseList(media->next);
        media->next = NULL;
        p1 = head;

        //merge two linkedlist
        while(p1 && p2){
            p = p1->next;
            p1->next = p2;
            p1 = p;
            p = p2->next;
            p2->next = p1;
            p2 = p;

        }

    }
};



void PrintList(ListNode* head){
    if(head == NULL)
        return ;
    ListNode *cur;
    cur = head;
    while(cur != NULL){
        cout << cur->val << " ";
        cur = cur->next;
    }
    cout << endl;
    return ;
}

ListNode* BuildList(int n){
    ListNode *p, *head, *cur;
    head = NULL;
    for(int i = 1; i <= n; ++i){
        p = new ListNode(i);
        if(head == NULL){
            head = p;
            cur = p;
            continue;
        }
        cur->next = p;
        cur = p;
    }
    return head;
}



int main(){
    ListNode *head, *tail;
    head = BuildList(4);
    PrintList(head);

    return 0;
}
