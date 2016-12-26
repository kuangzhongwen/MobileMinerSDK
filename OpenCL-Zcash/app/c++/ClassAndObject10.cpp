//
// Created by Mr.Kuang on 10/28/16.
// 第十章－类和对象
//


/* 1.类将一组变量和一组函数组合在一起。

   声明类：

   class Cat {
       unsigned int itsAge;
       unsigned int itsWeight;

       void Meow();
   }

   上述的声明并没有为Cat分配内存，它只是告诉编译器Cat是什么，它包含哪些数据，有什么功能。



   定义一个对象：
   Cat mCat;



   访问类成员：
   mCat.itsAge = 50;
   调用函数：
   mCat.Meow();
*/


/* 2. 私有和公有。

   用public和private用于类的成员，成员变量和方法。

   默认情况下，类的成员：变量和方法都是私有的。


   int main() {
       Cat mCat;
       // 不能访问其私有成员
       mCat.itsAge = 50;
       return 0;
   }

   编译会报错。




   #include <iostream>

   using namespace std;

   class Cat {
       public:
       unsigned int itsAge;
       unsigned int itsWeight;
       void Meow();
   };

   int main() {
       Cat mCat;
       // 不能访问其私有成员
       mCat.itsAge = 50;

       cout << "Cat itsAge:" << mCat.itsAge << endl;
       return 0;
   }
*/


/* 3. 和java一样，尽量设计为封装的，private，提供get, set方法。


   class Dog {
       public:
           // public accessors
           unsigned int getAge();
           void setAge(unsigned int age);

           unsigned int getWeight();
           void setWeight(unsigned int weight);

           // public member function
           void meow();


       private:
           unsigned int age;
           unsigned int weight;
   }
*/


/* 4. 实现类方法。

   class Dog {
       public:
           // public accessors
           unsigned int getAge();
           void setAge(unsigned int age);

           unsigned int getWeight();
           void setWeight(unsigned int weight);

           // public member function
           void meow();


       private:
           unsigned int age;
           unsigned int weight;
   };

   // 实现类的方法
   unsigned int Dog :: getAge() {
       return age;
   }

   void Dog :: setAge(unsigned int ageValue) {
       age = ageValue;
   }

   unsigned int Dog :: getWeight() {
       return weight;
   }

   void Dog :: setWeight(unsigned int weightValue) {
       weight = weightValue;
   }

   void testDog() {
       Dog mDog;
       mDog.setAge(10);
       mDog.setWeight(20);
       cout << "Dog age:" << mDog.getAge() << ", weight:" << mDog.getWeight() << endl;
   }

   int main() {
       testDog();

       return 0;
   }



   out: Dog age:10, weight:20
*/


/* 5. 添加构函数和析构函数。

   如何初始化类的成员数据呢，用构造函数。与类同名，没有返回值。

   声明构造函数后，还应该声明析构函数，一个是初始化数据用的，一个是在对象被销毁后完成清理工作并释放分配的
   资源或内存。

   析构函数也是与类同名，但是在前面加上一个 ～。

   class Dog {
       public:
           // 构造函数
           Dog();
           Dog (unsigned int age, unsigned int weight);
           // 析构函数
           ~Dog();
           // public accessors
           unsigned int getAge();
           void setAge(unsigned int age);

           unsigned int getWeight();
           void setWeight(unsigned int weight);

           // public member function
           void meow();


       private:
           unsigned int age;
           unsigned int weight;
   };

   Dog :: Dog () {

   }

   Dog :: Dog (unsigned int ageValue, unsigned int weightValue) {
       age = ageValue;
       weight = weightValue;
   }

   Dog :: ~Dog () {

   }

   // 实现类的方法
   unsigned int Dog :: getAge() {
       return age;
   }

   void Dog :: setAge(unsigned int ageValue) {
       age = ageValue;
   }

   unsigned int Dog :: getWeight() {
       return weight;
   }

   void Dog :: setWeight(unsigned int weightValue) {
       weight = weightValue;
   }

   void testDog() {
       Dog mDog;
       mDog.setAge(10);
       mDog.setWeight(20);
       cout << "Dog age:" << mDog.getAge() << ", weight:" << mDog.getWeight() << endl;
   }

   void testDog1() {
       Dog mDog(2, 3);
       cout << "Dog age:" << mDog.getAge() << ", weight:" << mDog.getWeight() << endl;
   }

   int main() {
       testDog1();

       return 0;
   }


   函数声明了就一定得实现。
*/


/* 6. Const成员函数。

   前面，const可以声明不能修改的变量，也可以用于类的成员函数，如果将类的方法声明为const，必须保证该方法
   不会修改任何类成员的值。

   void someFunction() const;

   如果前面的类Dog， setAge等set方法不能声明为const，因为它里面修改了类成员的值。
   而getAge等get方法可以声明为const。

   如：

   class Dog {
       public:
           // 构造函数
           Dog();
           Dog (unsigned int age, unsigned int weight);
           // 析构函数
           ~Dog();
           // public accessors
           unsigned int getAge() const;
           // 不可以声明为const
           void setAge(unsigned int age);

           unsigned int getWeight() const;
           // 不可以声明为const
           void setWeight(unsigned int weight);

           // public member function
           void meow();


       private:
           unsigned int age;
           unsigned int weight;
   };


   const方法，如果该方法的实现改变了对象的值，那么编译会报错。
*/



/* 7. 将类的声明和方法放在什么地方。

   大多数程序员的约定是，将声明放在头文件中，该头文件的名称与实现文件相同，但扩展名为.h。

   例如，可以把Dog类的声明放在Dog.h中，而将类方法的定义放在Dog.cpp中。加入 #include "Dog.h" 进行关联。

   为什么要分成两个文件呢，一般情况下，类的客户并不关心实现细节，只要阅读头文件，就可以知道需要知道的所有信息。

   他们可以忽略实现文件。
*/



/* 8. 内联实现。

   就像可以请求编译器将常规函数作为内联一样，也可以请求将类方法作为内联。为此只需要在返回类型前面加上inline。
   例如:


   // public accessors
   inline unsigned int getAge() const;

   // 实现类的方法
   inline unsigned int Dog :: getAge() const {
       return age;
   }

   也可以将函数的定义放到函数的声明中，也可以自动成为内联函数。
   class Cat {
      public:
        int getWeight() const {
            return weight;
        }

        void setWeight(int weight);

      private:
        int weight;
   }
*/


/* 9. 类的包含。

   就是一个类包含其他的类。

   Rectangle.h:

   class Point {
       public:
           // 内联函数
           void setX(int x) {
               itsX = x;
           }

           void setY(int y) {
               itsY = y;
           }

           int getX() const {
               return itsX;
           }

           int getY() const {
               return itsY;
           }

       private:
           int itsX;
           int itsY;
   };

   class Rectangle {
       public:
           Rectangle(int top, int left, int bottom, int right);
           ~Rectangle() {}

           int getTop() const {
               return itsTop;
           }

           int getLeft() const {
               return itsLeft;
           }

           int getBottom() const {
               return itsBottom;
           }

           int getRight() const {
               return itsRight;
           }

           void setTop(int top) {
               itsTop = top;
           }

           void setLeft(int left) {
               itsLeft = left;
           }

           void setBottom(int bottom) {
               itsBottom = bottom;
           }

           void setRight(int right) {
               itsRight = right;
           }

           Point getUpperLeft() const {
               return itsUpperLeft;
           }

           Point getLowerLeft() const {
               return itsLowerLeft;
           }

           Point getUpperRight() const {
               return itsUpperRight;
           }

           Point getLowerRight() const {
               return itsLowerRight;
           }

           void setUpperLeft(Point location) {
               itsUpperLeft = location;
           }

           void setLowerLeft(Point location) {
               itsLowerLeft = location;
           }

           void setUpperRight(Point location) {
               itsUpperRight = location;
           }

           void setLowerRight(Point location) {
               itsLowerRight = location;
           }

           int getArea() const;


       private:
           Point itsUpperLeft;
           Point itsUpperRight;
           Point itsLowerLeft;
           Point itsLowerRight;

           int itsLeft;
           int itsRight;
           int itsTop;
           int itsBottom;
   };


   ClassAndObject10.cpp:

   Rectangle :: Rectangle(int top, int left, int bottom, int right) {
       itsTop = top;
       itsLeft = left;
       itsBottom = bottom;
       itsRight = right;
   }

   int Rectangle :: getArea() const {
       int width = itsRight - itsLeft;
       int height = itsTop - itsBottom;

       return (width * height);
   }

   void testRectangle() {
       Rectangle rectangle (100, 20, 50, 80);
       int area = rectangle.getArea();

       cout << "area:" << area << endl;
   }


   out : 3000
*/



/* 10. 探索结构。

   与关键字class相似的是struct，它用来声明结构，在c++中，结构与类类似，只是其成员默认为公有的，且默认采用
   公有继承。可以像类一样声明结构，并给它声明数据成员和函数。

*/





#include <iostream>
#include "Dog.h"
#include "Rectangle.h"

using namespace std;

class Cat {
    public:
    unsigned int itsAge;
    unsigned int itsWeight;
    void Meow();
};

void testCat() {
    Cat mCat;
    // 不能访问其私有成员
    mCat.itsAge = 50;

    cout << "Cat itsAge:" << mCat.itsAge << endl;
}

Dog :: Dog () {

}

Dog :: Dog (unsigned int ageValue, unsigned int weightValue) {
    age = ageValue;
    weight = weightValue;
}

Dog :: ~Dog () {

}

// 实现类的方法
inline unsigned int Dog :: getAge() const {
    return age;
}

void Dog :: setAge(unsigned int ageValue) {
    age = ageValue;
}

unsigned int Dog :: getWeight() const {
    return weight;
}

void Dog :: setWeight(unsigned int weightValue) {
    weight = weightValue;
}

void testDog() {
    Dog mDog;
    mDog.setAge(10);
    mDog.setWeight(20);
    cout << "Dog age:" << mDog.getAge() << ", weight:" << mDog.getWeight() << endl;
}

void testDog1() {
    Dog mDog(2, 3);
    cout << "Dog age:" << mDog.getAge() << ", weight:" << mDog.getWeight() << endl;
}

Rectangle :: Rectangle(int top, int left, int bottom, int right) {
    itsTop = top;
    itsLeft = left;
    itsBottom = bottom;
    itsRight = right;
}

int Rectangle :: getArea() const {
    int width = itsRight - itsLeft;
    int height = itsTop - itsBottom;

    return (width * height);
}

void testRectangle() {
    Rectangle rectangle (100, 20, 50, 80);
    int area = rectangle.getArea();

    cout << "area:" << area << endl;
}

int main() {
    testRectangle();

    return 0;
}

