//
// Created by Mr.Kuang on 11/1/16.
// 第十三章－运算符类型和运算符重载
//


/* 1.在这章中，你将学到：

   使用关键字operator
   单目运算符和双目运算符
   转换运算符
   不能重新定义的运算符
*/


/* 2. c++中的运算符。

   单目运算符。
   双目运算符。
   三目运算符。
*/


/* 3. operator()函数。


   operator()函数让对象更像函数，它们应用于STL标准模版库中。通常用于STL算法中。

   #include <iostream>
   #include <string>

   class CDisplay {

       public:
           void operator() (std :: string strIn) const {
               std :: cout << strIn << std :: endl;
           }
   };

   void testCDisplay() {
       CDisplay cdisplayObject;
       cdisplayObject("hello world!");
   }

   int main() {
       testCDisplay();

       return 0;
   }


   out :
   hello world!


   CDisplay 实现了operator()操作函数，之所以能cdisplayObject("hello world!");把CDisplay
   用作函数，这是因为编译器隐式地将它转换为对operator函数的调用。
*/

/* 4. 不能重新定义的运算符。

   虽然c++提供了很大的灵活性，让程序员能够自定义运算符的行为让类更易于使用。它不允许程序员改变有些运算符的行为，
   如：

   .  .*  ::  ?: sizeof
*/


#include <iostream>
#include <string>

class CDisplay {

    public:
        void operator() (std :: string strIn) const {
            std :: cout << strIn << std :: endl;
        }
};

void testCDisplay() {
    CDisplay cdisplayObject;
    cdisplayObject("hello world!");
}

int main() {
    testCDisplay();

    return 0;
}

