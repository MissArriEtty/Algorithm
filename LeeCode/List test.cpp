#include <iostream>
#include <list>
#include <string>
using namespace std;

struct Node{
    string name;
    int age;
    Node(int n, int a): name(n), age(a) {}
};

void printList(const list<int> &myList){
    for(auto i = myList.begin(); i != myList.end(); ++i)
        cout << *i << " ";
    cout << endl;
}

int main(){
    list<int> myList, yourList;
    for(int i = 1; i <= 5; ++i)  //myList: 1 2 3 4 5
        myList.push_back(i);

    for(int i = 1; i <= 5; ++i)
        yourList.push_back(i * 10); //yourList: 10 20 30 40 50

    auto iter = myList.begin();
    ++iter;                          //*iter = 2

    //myList.splice(myList.end(), myList, iter);  //myList: 1 3 4 5 2
    //myList.splice(myList.begin(), myList, iter);  //myList: 2 1 3 4 5
    //myList.splice(myList.begin(), yourList);        //myList: 10 20 30 40 50 1 2 3 4 5
    myList.splice(myList.end(), yourList);            //myList: 1 2 3 4 5 10 20 30 40 50

    list<Node> nodeList;

    printList(myList);



    return 0;
}
