/**
 * Given n points on a 2D plane, find the maximum number of points that lie on the same straight line.
 * Definition for a point.
 * struct Point {
 *     int x;
 *     int y;
 *     Point() : x(0), y(0) {}
 *     Point(int a, int b) : x(a), y(b) {}
 * };
 */
#include <iostream>
#include <string>
#include <vector>
#include <map>
#include <limits>

using namespace std;


struct Point {
    int x;
    int y;
    Point() : x(0), y(0) {}
    Point(int a, int b) : x(a), y(b) {}
};

class Solution {

public:
    int maxPoints(vector<Point> &points) {

    	if(points.empty())
    	    return 0;

        map<double, int> slopeCount;
        map<double, int>::iterator iter;

        double slop;
        int maxNum, sameCount, sameSlope;

        maxNum = 1;
        for(vector<Point>::size_type i = 0; i < points.size(); ++i){

        	slopeCount.clear();
        	sameCount = 0;
        	sameSlope = 0;

            for(vector<Point>::size_type j = i + 1; j < points.size(); ++j){

                if(points[i].x != points[j].x)
                    slop = (points[i].y - points[j].y) * 1.0 / (points[i].x - points[j].x);
                else{

                	//相同的点：0 0; 0 0; 1 1
                	if(points[i].y == points[j].y){
                	    sameCount++;
                	    continue;
                	}
                    slop = numeric_limits<double>::max();
                }

                //当两个点不相等才算斜率
                iter = slopeCount.find(slop);
                if(iter == slopeCount.end()){

                	slopeCount.insert(pair<double, int>(slop,1));
                	if(sameSlope == 0)
                	    sameSlope = 1;

                }
                else{

                	iter->second++;
                	if(iter->second > sameSlope)
                	    sameSlope = iter->second;
                }

            }

            if(maxNum < sameSlope + sameCount + 1)
                maxNum = sameSlope + sameCount + 1;

        }

        return maxNum;
    }
};

int main(){
    freopen("in.txt", "r", stdin);
    Solution sol;
    Point p;
    vector<Point> points;

    while(cin>>p.x>>p.y){
        points.push_back(p);
    }
    cout << sol.maxPoints(points) << endl;

    return 0;
}
