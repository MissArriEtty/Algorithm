/*
 * Sort a linked list in O(n log n) time using constant space complexity.
 * 归并排序 
 */
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
	ListNode *mergeSortList(ListNode *p1, ListNode *p2){
		
		if(p1 == NULL)
		    return p2;
		if(p2 == NULL)
		   return p1;
		
		ListNode *p;
		ListNode *head = NULL;
		
		while(p1 != NULL && p2 != NULL){
			if(p1->val <= p2->val){
				if(head == NULL){
					head = p1;
					p = p1;
				}else{
					p->next = p1;
					p = p1;
				}
				p1 = p1->next;
				    
			}
			else{
				if(head == NULL){
					head = p2;
					p = p2;
				}else{
					p->next = p2;
					p = p2;
				}
				p2 = p2->next;
			}
		}
		if(p1 != NULL)
		    p->next = p1;
		else if(p2 != NULL)
		    p->next = p2;
		return head;
	}
    ListNode *sortList(ListNode *head) {
    	
    	if(head == NULL || head->next == NULL)
    	    return head;
    	ListNode *slow, *fast;
    	slow = head;
    	fast = head;
    	
    	//快慢指针找到中间值 
    	while(fast->next != NULL && fast->next->next != NULL){
    		fast = fast->next->next;
    		slow = slow->next;
    	}
    	
    	//打断链表
		fast = slow->next;
		slow->next = NULL;
		
		ListNode *p1, *p2;
		p1 = sortList(head);
		p2 = sortList(fast);
		 
    	return mergeSortList(p1, p2);
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



    head = sol.sortList(head);

    while(head != NULL){
        cout << head->val << " ";
        cur = head;
        head = head->next;
        delete cur;
    }
    cout << endl;
    return 0;
}
