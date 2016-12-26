//
// Created by Mr.Kuang on 11/3/16.
// 第十八章－STL动态数组类
//


/* 1. 动态数组让程序员能够灵活地存储数据，std :: vector。


   在本章中，将学习：

   － std :: vector的特点
   － 典型地vector操作
   － 理解大小与容量的概念
   － STL deque类


   std :: vector的特点:
   * 在数组末尾添加，删除元素的时间是固定的，不随数组大小而异。
   * 在数组中间添加或删除元素所需的时间与该元素后面的元素个数成正比。
   * 存储的元素数是动态的，而vector类负责管理内存。

   要使用vector，需要导入:
   #include <vector>
*/


/* 2.典型的vector操作。

   初始化：
       vector<int> vecArray;
       // 初始化动态数组大小，后面需要可以增大
       vector<int> vecArrayWithTenElements(10);
       // 初始化动态数组大小，给定每个的初始化
       vector<int> vecArrayWithTenInitElements(10, 90);
       // 根据一个vector生成一个新的vector
       vector<int> vecArrayCopy(vecArrayWithTenInitElements);
       // 从一个vector中取出n个元素生成一个新的vector
       vector<int> vecArrayCopySomeElements(vecArrayWithTenInitElements.begin(),
           vecArrayWithTenInitElements.begin() + 5);
*/


/* 3.在vector中插入元素。

       vector<int> vecIntegerDynamicArray;
       vecIntegerDynamicArray.push_back(10);
       vecIntegerDynamicArray.push_back(1);
       vecIntegerDynamicArray.push_back(987);
       vecIntegerDynamicArray.push_back(1001);

       cout << "vecIntegerDynamicArray size : " << vecIntegerDynamicArray.size() << endl;

       out: 4


     // 或者用数组方式赋值，但是得初始化大小
     vector<int> vecIntegerDynamicArray1(3);
     vecIntegerDynamicArray1[0] = 1;
     vecIntegerDynamicArray1[1] = 2;
     vecIntegerDynamicArray1[2] = 3;
     cout << "vecIntegerDynamicArray1 size : " << vecIntegerDynamicArray1.size() << endl;

     out : 3



    // 使用insert在中间位置插入
    vector<int> vecIntegers(4, 90);
    cout << "The init contents of the vector are : ";
    vector<int> :: iterator iElements;
    for (iElements = vecIntegers.begin(); iElements != vecIntegers.end(); ++iElements) {
        cout << *iElements << ' ';
    }
    cout << endl;

    vecIntegers.insert(vecIntegers.end(), 2, 45);

    vector<int> anotherVec(2, 30);
    vecIntegers.insert(vecIntegers.begin() + 1, anotherVec.begin(), anotherVec.end());
    cout << "The after insert contents of the vector are : ";
    vector<int> :: iterator iElements1;
    for (iElements1 = vecIntegers.begin(); iElements1 != vecIntegers.end(); ++iElements1) {
        cout << *iElements1 << ' ';
    }
    cout << endl;

    out:
    vecIntegerDynamicArray size : 4
    vecIntegerDynamicArray1 size : 3
    The init contents of the vector are : 90 90 90 90
    The after insert contents of the vector are : 90 30 30 90 90 90 45 45
*/



/* 4. 访问vector中的元素。

    vector<int> vecIntegerDynamicArray;
    vecIntegerDynamicArray.push_back(10);
    vecIntegerDynamicArray.push_back(1);
    vecIntegerDynamicArray.push_back(987);
    vecIntegerDynamicArray.push_back(1001);

    unsigned int elementsIndex = 0;
    while (elementsIndex < vecIntegerDynamicArray.size()) {
        cout << "Elements at position : " << elementsIndex
            << ", value : " << vecIntegerDynamicArray[elementsIndex] << endl;
        elementsIndex ++;
    }

    out:
    Elements at position : 0, value : 10
    Elements at position : 1, value : 1
    Elements at position : 2, value : 987
    Elements at position : 3, value : 1001




    用这种方式也可以：
       vector<int> :: iterator iElements1;
        for (iElements1 = vecIntegers.begin(); iElements1 != vecIntegers.end(); ++iElements1) {
            cout << *iElements1 << ' ';
        }
*/


/* 5. 删除vector中的元素。

    只支持在末尾删除元素


    // 删除最后一个元素
    vecIntegerDynamicArray.pop_back();
*/


/* 6. 理解size()和capacity()。

   一个是大小，一个是容量。
*/


/* 7. STL deque类。

   deque也是一个动态数组类，和vector类似，但是支持在数组开头和末尾插入或删除元素。

   得包含deque库    #include <deque>



    deque<int> dqIntegers;
    dqIntegers.push_back(100);
    dqIntegers.push_back(200);
    dqIntegers.push_back(300);

    // 在头部插入元素
    dqIntegers.push_front(2);
    dqIntegers.push_front(1);
    dqIntegers.push_front(0);

    // 删除头部元素
    dqIntegers.pop_front();

    // 删除尾部元素
    dqIntegers.pop_back();
*/


#include <iostream>
#include <vector>
#include <deque>
#include <algorithm>

using namespace std;

void testVectorInit();
void testVectorInsert();
void testVectorVisitElements();
void testVectorDeleteElements();
void testDeque();

int main() {
    testDeque();

    return 0;
}

void testVectorInit() {
    vector<int> vecArray;
    // 初始化动态数组大小，后面需要可以增大
    vector<int> vecArrayWithTenElements(10);
    // 初始化动态数组大小，给定每个的初始化
    vector<int> vecArrayWithTenInitElements(10, 90);
    // 根据一个vector生成一个新的vector
    vector<int> vecArrayCopy(vecArrayWithTenInitElements);
    // 从一个vector中取出n个元素生成一个新的vector
    vector<int> vecArrayCopySomeElements(vecArrayWithTenInitElements.begin(),
        vecArrayWithTenInitElements.begin() + 5);
}

void testVectorInsert() {
    vector<int> vecIntegerDynamicArray;
    vecIntegerDynamicArray.push_back(10);
    vecIntegerDynamicArray.push_back(1);
    vecIntegerDynamicArray.push_back(987);
    vecIntegerDynamicArray.push_back(1001);

    cout << "vecIntegerDynamicArray size : " << vecIntegerDynamicArray.size() << endl;

    // 或者用数组方式赋值，但是得初始化大小
    vector<int> vecIntegerDynamicArray1(3);
    vecIntegerDynamicArray1[0] = 1;
    vecIntegerDynamicArray1[1] = 2;
    vecIntegerDynamicArray1[2] = 3;
    cout << "vecIntegerDynamicArray1 size : " << vecIntegerDynamicArray1.size() << endl;

    // 使用insert在中间位置插入
    vector<int> vecIntegers(4, 90);
    cout << "The init contents of the vector are : ";
    vector<int> :: iterator iElements;
    for (iElements = vecIntegers.begin(); iElements != vecIntegers.end(); ++iElements) {
        cout << *iElements << ' ';
    }
    cout << endl;

    vecIntegers.insert(vecIntegers.end(), 2, 45);

    vector<int> anotherVec(2, 30);
    vecIntegers.insert(vecIntegers.begin() + 1, anotherVec.begin(), anotherVec.end());
    cout << "The after insert contents of the vector are : ";
    vector<int> :: iterator iElements1;
    for (iElements1 = vecIntegers.begin(); iElements1 != vecIntegers.end(); ++iElements1) {
        cout << *iElements1 << ' ';
    }
    cout << endl;
}

void testVectorVisitElements() {
    vector<int> vecIntegerDynamicArray;
    vecIntegerDynamicArray.push_back(10);
    vecIntegerDynamicArray.push_back(1);
    vecIntegerDynamicArray.push_back(987);
    vecIntegerDynamicArray.push_back(1001);

    unsigned int elementsIndex = 0;
    while (elementsIndex < vecIntegerDynamicArray.size()) {
        cout << "Elements at position : " << elementsIndex
            << ", value : " << vecIntegerDynamicArray[elementsIndex] << endl;
        elementsIndex ++;
    }
}

void testVectorDeleteElements() {
    vector<int> vecIntegerDynamicArray;
    vecIntegerDynamicArray.push_back(10);
    vecIntegerDynamicArray.push_back(1);
    vecIntegerDynamicArray.push_back(987);
    vecIntegerDynamicArray.push_back(1001);

    // 删除最后一个元素
    vecIntegerDynamicArray.pop_back();
}

void testDeque() {
    deque<int> dqIntegers;
    dqIntegers.push_back(100);
    dqIntegers.push_back(200);
    dqIntegers.push_back(300);

    // 在头部插入元素
    dqIntegers.push_front(2);
    dqIntegers.push_front(1);
    dqIntegers.push_front(0);

    // 删除头部元素
    dqIntegers.pop_front();

    // 删除尾部元素
    dqIntegers.pop_back();
}

