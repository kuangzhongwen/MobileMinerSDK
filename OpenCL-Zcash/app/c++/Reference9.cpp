//
// Created by Mr.Kuang on 10/28/16.
// 第九章－引用
//

/* 1.引用是别名，创建引用时，您将其初始化为另一个对象的名称。然后引用即成为它的另一个名称，
   对引用执行任何操作实际上都是对它的操作。

   如 int a = 5;
      int &ra = a;

   引用和变量的区别是，声明引用的时候，必须对其进行初始化，否则会编译错误。

   int & ra = a；这样加上空格也可以


   int intOne;
   int &rOne = intOne;
   intOne = 5;
   cout << "intOne : " << intOne << ", rOne : " << rOne << endl;
   rOne = 10;
   cout << "intOne : " << intOne << ", rOne : " << rOne << endl;

   intOne : 5, rOne : 5
   intOne : 10, rOne : 10
*/



/* 2. 将地址运算符运用于引用

   cout << "intOne address: " << &intOne << ", rOne address: " << &rOne << endl;
   // intOne address: 0x7fff5f66ea3c, rOne address: 0x7fff5f66ea3c



   不能给引用重新赋值：
   int intTwo;
   // 这样是不行的
   rOne = intTwo;
*/


/* 3. 交换程序swap

    void swap1(int *x, int *y) {
        int temp = *x;
        *x = *y;
        *y = temp;
    }

    void swap2(int &x, int &y) {
        int temp = x;
        x = y;
        y = temp;
    }


    int x = 5, y = 3;
    swap1(&x, &y);
    cout << "x : " << x << ",y : " << y << endl;
    swap2(x, y);
    cout << "x : " << x << ",y : " << y << endl;

    x : 3,y : 5
    x : 5,y : 3
*/


/* 4. 何时使用引用，何时使用指针。

   经验丰富的c++程序员喜欢用引用，确实引用更清晰，但是引用不能被重新赋值，这个时候就得用指针。
*/

#include <iostream>

using namespace std;

void testRef1();
void swap1(int *x, int *y);
void swap2(int &x, int &y);

int main() {
    //testRef1();
    int x = 5, y = 3;
    swap1(&x, &y);
    cout << "x : " << x << ",y : " << y << endl;
    swap2(x, y);
    cout << "x : " << x << ",y : " << y << endl;


    return 0;
}

void testRef1() {
    int intOne;
    int &rOne = intOne;
    intOne = 5;
    cout << "intOne : " << intOne << ", rOne : " << rOne << endl;
    rOne = 10;
    cout << "intOne : " << intOne << ", rOne : " << rOne << endl;

    cout << "intOne address: " << &intOne << ", rOne address: " << &rOne << endl;
    // intOne address: 0x7fff5f66ea3c, rOne address: 0x7fff5f66ea3c
}

void swap1(int *x, int *y) {
    int temp = *x;
    *x = *y;
    *y = temp;
}

void swap2(int &x, int &y) {
    int temp = x;
    x = y;
    y = temp;
}