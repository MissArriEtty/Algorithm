/*
 * Design and implement a data structure for Least Recently Used (LRU) cache.
 * It should support the following operations: get and set.
 *
 * attention: set(2,1), set(2,2), get(2) ----> 2
 *
 * addr: https://oj.leetcode.com/problems/lru-cache/
 */

#include <iostream>
#include <unordered_map>
#include <list>
using namespace std;

class LRUCache{
private:
    struct cacheNode{
        int key;
        int val;
        cacheNode(int k, int v): key(k), val(v) {}
    };


public:
    LRUCache(int capacity) {
        this->capacity = capacity;
        this->cacheMap.clear();
        this->cacheList.clear();
    }

    void print(){
        for(auto iter = cacheList.begin(); iter != cacheList.end(); ++iter)
            cout << iter->key << " " << iter->val << endl;

    }
    //访问节点，如果节点存在就更新其访问为最近访问的节点
    int get(int key) {

        auto iterMap = cacheMap.find(key);
        if(iterMap == cacheMap.end())
            return -1;

        cacheList.splice(cacheList.begin(), cacheList, iterMap->second);
        iterMap->second = cacheList.begin();

        return (iterMap->second)->val;

    }

    //增加节点，如果存在就更新为最近访问的节点，如果不存在就插入同时检查capacity是否达到上限
    void set(int key, int value) {

        cacheNode node(key, value);
        auto iterMap = cacheMap.find(key);

        //不存在
        if(iterMap == cacheMap.end()){

            if(capacity == cacheList.size()){
                cacheMap.erase(cacheList.back().key);
                cacheList.pop_back();
            }

            cacheList.push_front(node);
            //更新map，增加新key
            cacheMap[key] = cacheList.begin();

        }
        //存在
        else{
            auto iterList = cacheMap[key];
            iterList->val = value;
            cacheList.splice(cacheList.begin(), cacheList, iterList);
            cacheMap[key] = cacheList.begin();
        }

    }
private:
    int capacity;
    unordered_map<int, list<cacheNode>::iterator > cacheMap;
    list<cacheNode> cacheList;
};

int main(){

    LRUCache ins(2); //capacity == 3
    //cout << ins.get(2) << endl;
    ins.set(2, 1);
    ins.set(2, 2);
    cout << ins.get(2) << endl;
    //ins.set(3, 2);
    //cout << ins.get(2) << endl;
    //cout << ins.get(3) << endl;
    //ins.set(3, 30);
    //ins.print();
    //cout << ins.get(2) << endl;
    ins.print();

    return 0;
}
