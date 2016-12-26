//
// Created by Mr.Kuang on 11/3/16.
// 第十九章－STL list
//


/* 1. list 是一种双向链表。

   在这章将学习：

   －std :: list的特点
   －基本的list操作


   链表是一系列节点，其中每个节点除了包含对象外还包含指向下一个节点。即每个节点都链接到下一个节点。
*/


/* 2. 基本的list操作。

    首先的包含 <list>

    list<int> listIntegers;


#include <iostream>
#include <list>

using namespace std;

void iteratorList(list<int>);

void testListBase() {
    list<int> listIntegers;
    // 双向链表，可以在头部插入
    listIntegers.push_front(10);
    listIntegers.push_front(2999);

    iteratorList(listIntegers);

    cout << endl << endl;

    // 在list尾部插入
    listIntegers.push_back(300);
    listIntegers.push_back(1);

    iteratorList(listIntegers);

    // 在list中间插入
    listIntegers.insert(listIntegers.begin(), 0);
    listIntegers.insert(listIntegers.end(), 1111);
    // 插入3个2
    listIntegers.insert(listIntegers.begin(), 3, 2);

    cout << endl << endl;
    iteratorList(listIntegers);

    // 删除list元素
    listIntegers.erase(listIntegers.begin(), 2);
}

void iteratorList(list<int> lt) {
    list<int> :: iterator iElement;
    for (iElement = lt.begin(); iElement != lt.end(); ++iElement) {
        cout << *iElement << endl;
    }
}
*/



/* 2. 对list中的元素反转和排序。

   反转：listIntegers.reverse();


   元素排序：
   list的成员函数sort有两个版本，一个没有参数，另一个接受一个二元谓词函数作为参数。
   将根据谓词指定的标准来进行排序。


    list<int> listIntegers;
    listIntegers.push_front(444);
    listIntegers.push_front(333);
    listIntegers.push_front(21111);
    listIntegers.push_front(-1);
    listIntegers.push_front(0);
    listIntegers.push_back(-5);

    iteratorList(listIntegers);

    cout << endl << endl;

    listIntegers.sort();
    iteratorList(listIntegers);


    out:
    -5
    -1
    0
    333
    444
    21111



    bool sortDescending(const int& lsh, const int& rsh);

    listIntegers.sort(sortDescending);
    iteratorList(listIntegers);

    bool sortDescending(const int& lsh, const int& rsh) {
        return rsh < lsh;
    }

    out :

    21111
    444
    333
    0
    -1
    -5
*/



#include <iostream>
#include <list>

using namespace std;

void iteratorList(list<int>);
bool sortDescending(const int& lsh, const int& rsh);

void testListBase() {
    list<int> listIntegers;
    // 双向链表，可以在头部插入
    listIntegers.push_front(10);
    listIntegers.push_front(2999);

    iteratorList(listIntegers);

    cout << endl << endl;

    // 在list尾部插入
    listIntegers.push_back(300);
    listIntegers.push_back(1);

    iteratorList(listIntegers);

    // 在list中间插入
    listIntegers.insert(listIntegers.begin(), 0);
    listIntegers.insert(listIntegers.end(), 1111);
    // 插入3个2
    listIntegers.insert(listIntegers.begin(), 3, 2);

    cout << endl << endl;
    iteratorList(listIntegers);

    // 删除list元素
    // listIntegers.erase(listIntegers.begin(), 2);
}

void testListRevert() {
    list<int> listIntegers;
    // 双向链表，可以在头部插入
    listIntegers.push_back(10);
    listIntegers.push_back(2999);

    iteratorList(listIntegers);

    listIntegers.reverse();

    cout << endl << endl;

    iteratorList(listIntegers);
}

void testListSort() {
    list<int> listIntegers;
    listIntegers.push_front(444);
    listIntegers.push_front(333);
    listIntegers.push_front(21111);
    listIntegers.push_front(-1);
    listIntegers.push_front(0);
    listIntegers.push_back(-5);

    iteratorList(listIntegers);

    cout << endl << endl;

    listIntegers.sort();
    iteratorList(listIntegers);

    cout << endl << endl;
    listIntegers.sort(sortDescending);
    iteratorList(listIntegers);
}

bool sortDescending(const int& lsh, const int& rsh) {
    return rsh < lsh;
}

void iteratorList(list<int> lt) {
    list<int> :: iterator iElement;
    for (iElement = lt.begin(); iElement != lt.end(); ++iElement) {
        cout << *iElement << endl;
    }
}

int main() {
    testListSort();

    return 0;
}

