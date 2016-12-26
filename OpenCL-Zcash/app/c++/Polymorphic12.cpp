//
// Created by Mr.Kuang on 10/31/16.
// 第十二章－多态
//


/* 1.在十一章中，介绍了如何在派生类中，编写虚函数，这是多态的基石：在运行阶段将派生类对象绑定到
基类指针。

   在本章中，您将学习：
   - 什么是多重继承以及如何使用它们。
   - 什么是虚继承以及如何使用它们。
   - 什么是抽象类以及如何使用它们。
   - 什么是纯虚函数。
*/


/* 2. 单继承存在的问题。

   你使用动物类已经有一段时间了，分为鸟类和哺乳类。
   现在有一种动物，叫天马，它是哺乳动物，但是又有翅膀，显然，单继承已经无法满足了。

   提升：
   将所需的函数放到类层次较高的类中，是一种常用的解决这种问题的方法，其结果是很多函数被提升到基类当中。
   这样将严重的破坏c++的类型化，创造出庞大的基类。

   如果仅仅是为了能够在某些派生类中能够调用它，而把这个函数放进去，这是破坏基类的定义的。


   向下转换：
   采用单继承时，另一种办法是把fly()方法留在Pegasus中。这种方法也不推荐。
*/


/* 3.多重继承。

   可以从多个基类中派生出新类。这被称为多重继承。
   在类声明中，将每个基类用逗号隔开。

   class DerivedClass : public BaseClass1, public BaseClass2 {}

   #include <iostream>

   using namespace std;

   class Horse {
       public:
           Horse() {
              cout << "Horse constructor..." << endl;
           }

           virtual ~Horse() {
              cout << "Horse destructor..." << endl;
           }

           virtual void whinny() const {
               cout << "Whinny..." << endl;
           }

       private:
           int itsAge;
   };

   class Bird {
       public:
           Bird() {
               cout << "Bird constructor..." << endl;
           }

           virtual ~Bird() {
               cout << "Bird destructor..." << endl;
           }

           virtual void chirp() const {
               cout << "Chirp..." << endl;
           }

           virtual void fly() const {
               cout << "I can fly..." << endl;
           }

       private:
           int itsWeight;
   };

   // 派生类，多重继承
   class Pegasus : public Horse, public Bird {

       public:
           void chirp() const {
               whinny();
           }

           Pegasus() {
               cout << "Pegasus constructor..." << endl;
           }

           ~Pegasus() {
               cout << "Pegasus destructor..." << endl;
           }
   };


   test:

   void testPegasus() {
       Horse *ranch[MagicNumber];
       Bird *aviary[MagicNumber];

       Horse *pHorse;
       Bird *pBird;

       int choice, i;

       for (i = 0; i < MagicNumber; i++) {
           cout << "\n (1)Horse (2)Pegasus: ";
           cin >> choice;
           if (choice == 2) {
               pHorse = new Pegasus;
           } else {
               pHorse = new Horse;
           }
           ranch[i] = pHorse;
       }

       for (i = 0; i < MagicNumber; i++) {
           cout << "\n (1)Bird (2)Pegasus: ";
           cin >> choice;
           if (choice == 2) {
               pBird = new Pegasus;
           } else {
               pBird = new Bird;
           }
           aviary[i] = pBird;
       }

       cout << endl;

       for (i = 0; i < MagicNumber; i++) {
           cout << "\n ranch[" << i << "]:";
           ranch[i] -> whinny();
           delete ranch[i];
       }

       for (i = 0; i < MagicNumber; i++) {
           cout << "\n aviary[" << i << "]:";
           aviary[i] -> chirp();
           aviary[i] -> fly();
           delete aviary[i];
       }
   }


   out:

    (1)Horse (2)Pegasus: 2
   Horse constructor...
   Bird constructor...
   Pegasus constructor...

    (1)Horse (2)Pegasus: 1
   Horse constructor...

    (1)Bird (2)Pegasus:

*/


/* 4. 多重继承中的构造函数。

   如果Pegasus从Horse和Bird派生而来，且每个基类都有接收参数的构造函数，那么Pegasus将依次调用这些构造函数
*/


/* 5. 抽象。

   c++通过提供纯虚函数来支持创建抽象类。
   通过将虚函数初始化为0来声明其纯虚，如：

   // 就是实现为空
   virtual void draw = 0;

   那么对于抽象类：

   ＊ 不能创建这个抽象类的对象，而应从其派生。
   ＊ 务必覆盖从这个抽象类继承的纯虚函数。


   // 抽象类
   class Shape {

       public:
           Shape() {}
           ~Shape() {}

           virtual double getArea() = 0;
           virtual double getPerim() = 0;
           virtual void draw() = 0;
   };

   void Shape :: draw() {
           cout << "Abstract draw!" << endl;
   }

   class Circle : public Shape {
       public:
           Circle(int radius) : itsRadius(radius) {}
           virtual ~Circle() {}

           double getArea() {
               return 3.14 * itsRadius * itsRadius;
           }

           double getPerim() {
               return 2 * 3.14 * itsRadius;
           }

           void draw();

       private:
           int itsRadius;
   }

   void Circle :: draw() {
       cout << "Circle draw!" << endl;
       // 调用父类的函数
       Shape :: draw();
   }
*/


#include "Polymorphic.h"

void testPegasus();

const int MagicNumber = 2;

int main() {
    testPegasus();

    return 0;
}

void testPegasus() {
    Horse *ranch[MagicNumber];
    Bird *aviary[MagicNumber];

    Horse *pHorse;
    Bird *pBird;

    int choice, i;

    for (i = 0; i < MagicNumber; i++) {
        cout << "\n (1)Horse (2)Pegasus: ";
        cin >> choice;
        if (choice == 2) {
            pHorse = new Pegasus;
        } else {
            pHorse = new Horse;
        }
        ranch[i] = pHorse;
    }

    for (i = 0; i < MagicNumber; i++) {
        cout << "\n (1)Bird (2)Pegasus: ";
        cin >> choice;
        if (choice == 2) {
            pBird = new Pegasus;
        } else {
            pBird = new Bird;
        }
        aviary[i] = pBird;
    }

    cout << endl;

    for (i = 0; i < MagicNumber; i++) {
        cout << "\n ranch[" << i << "]:";
        ranch[i] -> whinny();
        delete ranch[i];
    }

    for (i = 0; i < MagicNumber; i++) {
        cout << "\n aviary[" << i << "]:";
        aviary[i] -> chirp();
        aviary[i] -> fly();
        delete aviary[i];
    }
}