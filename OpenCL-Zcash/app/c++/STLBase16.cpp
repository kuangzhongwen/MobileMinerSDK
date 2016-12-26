//
// Created by Mr.Kuang on 11/2/16.
// 第十六章－标准模版库简介
//


/* 1.简单的说，标准模版库(STL）是一组模版类和函数，向程序员提供了：

   －存储信息的容器
   －访问容器存储信息的迭代器
   －操作容器内容的算法


   STL容器：
   容器是用于存储数据的STL类，STL提供了两种类型的容器－－

   顺序容器。
   关联容器。


   顺序容器就是按照顺序存储数据的容器，如数组和列表。
   顺序容器具有插入速度快但查找操作较慢的特征。

   STL顺序容器包括：
   std::vector   操作与动态数组一样，在最后插入
   std::deque    与std:vector类似，但是也允许在开头插入数据
   std::list     操作与链表一样


   STL vector类与数组类似，它允许随机访问元素，即可以使用下表运算符指定元素在向量中的位置，从而直接访问或操作元素。
   另外STL vector是动态数组，因此能够根据在运行阶段的需求自动调整长度。这通常会降低应用的性能。

   可以将STL list看作是普通链表的实现，虽然list中的元素不能像vector一样随机访问，但list可以使用不连续的内存块
   组织元素，因此它不能像vector那样需要给内部数组重新分配内存，今儿影响性能。s





   STL关联容器：
   关联容器按指定的顺序存储数据，像字典一样。这将降低插入数据的速度，但是在查询方面却有很大的优势。

   STL关联容器包括－－

   std::set   按排序排列的唯一值列表
   std::map   按键－值对形式
   std::multiset 与set类似，但允许存储多个值相同的项，即值不需要是唯一的
   std::multimap 与map类似，不要求键是唯一的
*/



/* 2. STL算法。

   查找，排序和反转都是标准的编程要求，不应让程序员重复实现这样的功能。因此STL以STL算法的方式提供了这些函数。

   最常见的STL算法包括：

   std::find 在集合中查找值
   std::findif 根据用户指定的谓词在集合中查找值
   std::reverse 反转集合中元素的排列顺序
   std::remove_if 根据用户定义的谓词将元素从集合中移除
   std::transform 使用用户定义的变换函数对集合中的元素进行变换


   #include <iostream>
   #include <vector>
   #include <algorithm>

   using namespace std;

   void testVector();

   int main() {
       testVector();

       return 0;
   }

   void testVector() {
       vector<int> vectorIntegerArray;

       vectorIntegerArray.push_back(50);
       vectorIntegerArray.push_back(2991);
       vectorIntegerArray.push_back(23);
       vectorIntegerArray.push_back(9999);

       cout << "The content of the vector are :" << endl;

       vector<int> :: iterator iArrayWalker = vectorIntegerArray.begin();
       while (iArrayWalker != vectorIntegerArray.end()) {
           cout << *iArrayWalker << endl;
           ++iArrayWalker;
       }

       vector<int> :: iterator iElement = find(vectorIntegerArray.begin(),
           vectorIntegerArray.end(), 2991);
       if (iElement != vectorIntegerArray.end()) {
           int position = distance(vectorIntegerArray.begin(), iElement);
           cout << "Value " << *iElement;
           cout << "find the value position " << position << endl;
       }
   }


   out:

   The content of the vector are :
   50
   2991
   23
   9999
   Value 2991find the value position 1
*/



#include <iostream>
#include <vector>
#include <algorithm>

using namespace std;

void testVector();

int main() {
    testVector();

    return 0;
}

void testVector() {
    vector<int> vectorIntegerArray;

    vectorIntegerArray.push_back(50);
    vectorIntegerArray.push_back(2991);
    vectorIntegerArray.push_back(23);
    vectorIntegerArray.push_back(9999);

    cout << "The content of the vector are :" << endl;

    vector<int> :: iterator iArrayWalker = vectorIntegerArray.begin();
    while (iArrayWalker != vectorIntegerArray.end()) {
        cout << *iArrayWalker << endl;
        ++iArrayWalker;
    }

    vector<int> :: iterator iElement = find(vectorIntegerArray.begin(),
        vectorIntegerArray.end(), 2991);
    if (iElement != vectorIntegerArray.end()) {
        int position = distance(vectorIntegerArray.begin(), iElement);
        cout << "Value " << *iElement;
        cout << "find the value position " << position << endl;
    }
}
