//
// Created by Mr.Kuang on 11/3/16.
// 第二十章－STL set 和 multiset
//

/* 1.容器set 和 multiset 让程序员能够快速查找键，键是一唯容器中的值。

   set 和 multiset的区别是，multiset可以有重复的值，而set不能。

   #include <set>

   实例化set :

   set<int> setIntegers;
   multiset<int> msetIntegers;
*/


/* 2. 在set 和 multiset插入元素。

   #include <iostream>
   #include <set>

   using namespace std;

   void printSet(const set<int> & st);
   void printMSet(const multiset<int> & st);

   void testSetBase() {
       set<int> setIntegers;
       multiset<int> msetIntegers;

       // 插入元素
       setIntegers.insert(50);
       setIntegers.insert(-1);
       setIntegers.insert(6000);

       printSet(setIntegers);

       cout << endl << endl;

       msetIntegers.insert(setIntegers.begin(), setIntegers.end());
       msetIntegers.insert(3000);
       printMSet(msetIntegers);
   }

   void printSet(const set<int> & st) {
       set<int> :: const_iterator iElements = st.begin();
       while (iElements != st.end()) {
           cout << *iElements << endl;
           ++iElements;
       }
   }

   void printMSet(const multiset<int> & st) {
       multiset<int> :: const_iterator iElements = st.begin();
       while (iElements != st.end()) {
           cout << *iElements << endl;
           ++iElements;
       }
   }

*/


/* 3. 在set 和 multiset 中查找元素。

   typedef set<int> SETINT;


   void testSetFind() {
       SETINT setIntegers;

       setIntegers.insert(43);
       setIntegers.insert(78);
       setIntegers.insert(-1);
       setIntegers.insert(124);

       SETINT :: const_iterator iElements = setIntegers.begin();
       for (; iElements != setIntegers.end(); ++iElements) {
           cout << *iElements << endl;
       }

       cout << endl << endl;

       SETINT :: iElementsFound = setIntegers.find(-1);
       if (iElementsFound != setIntegers.end()) {
           cout << "find -1" << endl;
       } else {
           cout << "un find" << endl;
       }
   }
*/


#include <iostream>
#include <set>

using namespace std;

typedef set<int> SETINT;

void printSet(const set<int> & st);
void printMSet(const multiset<int> & st);

void testSetBase() {
    set<int> setIntegers;
    multiset<int> msetIntegers;

    // 插入元素
    setIntegers.insert(50);
    setIntegers.insert(-1);
    setIntegers.insert(6000);

    printSet(setIntegers);

    cout << endl << endl;

    msetIntegers.insert(setIntegers.begin(), setIntegers.end());
    msetIntegers.insert(3000);
    printMSet(msetIntegers);
}

void testSetFind() {
    SETINT setIntegers;

    setIntegers.insert(43);
    setIntegers.insert(78);
    setIntegers.insert(-1);
    setIntegers.insert(124);

    SETINT :: const_iterator iElements = setIntegers.begin();
    for (; iElements != setIntegers.end(); ++iElements) {
        cout << *iElements << endl;
    }

    cout << endl << endl;

    SETINT :: iElementsFound = setIntegers.find(-1);
    if (iElementsFound != setIntegers.end()) {
        cout << "find -1" << endl;
    } else {
        cout << "un find" << endl;
    }
}

void printSet(const set<int> & st) {
    set<int> :: const_iterator iElements = st.begin();
    while (iElements != st.end()) {
        cout << *iElements << endl;
        ++iElements;
    }
}

void printMSet(const multiset<int> & st) {
    multiset<int> :: const_iterator iElements = st.begin();
    while (iElements != st.end()) {
        cout << *iElements << endl;
        ++iElements;
    }
}

int main() {
    testSetFind();

    return 0;
}

