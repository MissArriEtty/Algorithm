#include <iostream>
#include <string>

using namespace std;

const int M = 140779;

int ELFhash(string str)
{
    unsigned long h = 0;
	char *key = (char*)str.c_str();
	while(*key){
	    h = (h << 4) + *key++;
		unsigned long g = h & 0xf0000000L;
		if(g) h ^= g >> 24;
		h &= ~g;
	}
	
    return h%M;
}

int main()
{
    string str;
	cin >> str;
	cout << ELFhash(str) << endl;
    return 0;
}