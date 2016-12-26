//
// Created by Mr.Kuang on 10/31/16.
// 第十一章－实现继承
//

/* 1.继承是面向对象的重要特性之一。

   在本章中，您将学习：
   ＊ 什么是继承。
   ＊ 如何使用继承从一个类中派生出另一个类。
   ＊ 什么是保护访问权限（protected access)以及如何使用它。
   ＊ 什么是虚方法。
   ＊ 什么是私有继承。


   派生的语法：
   声明类时，可在类名后面加上冒号（：）＋ 派生类型（公有或私有）＋ 基类名来指出它是从哪个类派生而来。

   格式：
   class derivedClass : accessType baseClass

   如：
   class Dog : public Mammal

   enum BREED {GOLDEN, CAIRN, DANDIE, SHETLAND, DOBERMAN, LAB};

   class Mammal {
       public:
           Mammal();
           ~Mammal();

           int getAge() const;
           void setAge(int);
           int getWeight() const;
           void setWeight(int);

           void speak() const;
           void sleep() const;


       protected:
           int itsAge;
           int itsWeight;
   }

   class Dog : public Mammal {
       public:
           Dog();
           ~Dog();

           BREED getBreed() const;
           void setBreed(BREED);

           void wagTail();
           void begForFood();

       protected:
           BREED itsBreed;
   }


   每个Dog都将有三个成员变量，itsAge, itsWeight, itsBreed。
   Dog对象从Mammal类继承了这些变量以及除复制构造函数，构造函数，析构函数之外的所有方法。
*/



/* 2. 私有和保护。

   您想要的派生类型：使这些数据对当前类和派生类是可见的，这种派生类型为protected。
   proteced成员对于派生类来说是完全可见的，但是对于其他类来说是私有的。

   总共有三种访问修饰符：public, protected, private。

   即使在继承层次中，Mammal和Dog中还有其他类，Dog类仍然能访问Mammal的保护成员，前提是这些类都是采用公有继承。

   #include <iostream>

   using namespace std;

   enum BREED {GOLDEN, CAIRN, DANDIE, SHETLAND, DOBERMAN, LAB};

   class Mammal {
       public:
           Mammal() : itsAge(2), itsWeight(5){}
           ~Mammal(){}

           int getAge() const {
               return itsAge;
           }
           void setAge(int age) {
               itsAge = age;
           }
           int getWeight() const {
               return itsWeight;
           }
           void setWeight(int weight) {
               itsWeight = weight;
           }

           void speak() const {
               cout << "Mammal sound!" << endl;
           }
           void sleep() const {
               cout << "Mammal sleep!" << endl;
           }


       protected:
           int itsAge;
           int itsWeight;
   };

   class Dog : public Mammal {
       public:
           Dog() : itsBreed(GOLDEN) {}
           ~Dog() {}

           BREED getBreed() const {
               return itsBreed;
           }

           void setBreed(BREED breed) {
               itsBreed = breed;
           }

           void wagTail() {
               cout << "Tail wagging..." << endl;
           }

           void begForFood() {
               cout << "Begging for food..." << endl;
           }

       protected:
           BREED itsBreed;
   };








#include "Extends.h"

void testDog() {
    Dog fido;
    fido.speak();
    fido.sleep();
    fido.wagTail();
    fido.begForFood();

    cout << "Fido is " << fido.getAge() << " years old, weight is " << fido.getWeight() << endl;
}

int main() {
    testDog();

    return 0;
}


 out :

 Mammal sound!
 Mammal sleep!
 Tail wagging...
 Begging for food...
 Fido is 2 years old, weight is 5
*/



/* 3. 构造函数和析构函数的继承性。

   构造时，先调用基类的构造函数，再调用当前子类的构造函数。
   释放时，先调用基类的析构函数，再调用当前子类的析构函数。



   覆盖基类的函数：
   Dog类可以访问Mammal类的所有成员函数，也可以访问Dog类新增的任何数据和函数。
   派生类还可以覆盖基类的函数。

   覆盖基类函数时，特征标必须与基类中的被覆盖的函数相同。特征标指的是函数原型中除返回类型外的类型，
   即函数名，参数列表，和可能用到的关键字const。

   如：

        void speak() const {
            cout << "Dog is speeking..." << endl;
        }

   java中的重载是方法名相同，参数不同，与返回类型无关。
   java中的覆盖是方法名相同，参数列表相同，返回类型比父类小或者相同，异常类型比父类大或者相同。
*/


/* 4. 隐藏基类方法。

   在子类中调用基类的方法，如在Dog中访问Mammal中的方法：
   Mammal :: speak();

          void speak() const{
               Mammal :: speak();
               cout << "Dog is speeking..." << endl;
           }

   out :
   Mammal sound!
   Dog is speeking...
*/


/* 5. 虚方法。

   c++扩展了其多态性。允许将派生类对象赋值给指向基类的指针。
   如:
   Mammal *pMammal = new Dog;

   然后该指针可以调用Mammal中的任何方法。您希望在调用被Dog覆盖的方法，将调用正确函数。
   虚函数将让您做到这一点。
   要创建虚函数，可以在函数声明前加上关键字virtual。

   Mammal.class :

        virtual void speak() const {
            cout << "Mammal sound!" << endl;
        }


  Dog.class :
        void speak() const{
            Mammal :: speak();
            cout << "Dog is speeking..." << endl;
        }

  test :
    Mammal *pMammal = new Dog;
    pMammal->speak();

  out :
    Mammal sound!
    Dog is speeking...

  如果把Mammal.class中speak的virtual去掉，结果是Mammal sound!
  指针只能调用基类中的speak()。


  经验规则，如果类中任何一个函数是虚函数，那么析构函数也应该是虚函数。
  如果析构函数是虚函数，那么将调用派生类的析构函数，而派生类的析构函数会自动调用基类的析构函数,
  因此整个对象都会被正确的销毁。

  子类的构造器，也会自动调用基类的构造器。



  使用虚函数的代价：
  由于包含虚函数的类必须维护一个v-table，因此使用虚函数会有一定的开销。
  如果类很小，不打算从它派生除其他类，那么根本没必要使用虚函数。
*/


/* 6. 私有继承。

   在前面的示例中，Dog是Mammal派生来的。
   class Dog :: public Mammal

   有时候，程序员想利用现有的基类（即通过派生使用现有功能），但is-a关系没有意义，甚至是一种糟糕的
   编程方式，在这种情况下，私有继承将派上用场。

   假设要重用ElectriMotor来创建Fan类。Fan不是一个ElectriMotor，而只是使用ElectriMotor，因此公有继承在这种场景
   下不适合，但程序员可以这样做：
   class Fan :: private ElectriMotor

   这种派生让Fan能使用ElectriMotor的所有功能，甚至覆盖其虚函数。就是Fan本身能使用ElectriMotor的所有功能，甚至覆盖
   其虚函数，但是，Fan的调用者，这个基类ElectriMotor的所有内容都是私有的，而不管其使用的访问限定符。

   class ElectriMotor {

       public:
           ElectriMotor() {}
           virtual ~ElectriMotor() {}

           void startMotor() {
               accelerate();
               cruise();
           }

           void stopMotor() {
               cout << "Motor stopped!" << endl;
           }

       private:
           void accelerate() {
               cout << "Motor started!" << endl;
           }

           void cruise() {
               cout << "Motor running at const speed!" << endl;
           }
   };

   // 私有继承
   class Fan : private ElectriMotor {
       public:
          Fan() {}
          ~Fan() {}

          void startFan() {
              startMotor();
          }

          void stopFan() {
              stopMotor();
          }
   };


   test:

   void testPrivateExtends() {
       Fan mFan;
       mFan.startFan();
       mFan.stopFan();
   }


   out:

   Motor started!
   Motor running at const speed!
   Motor stopped!


   // 但是外部调用者不能调用基类的方法
   // mFan.startMotor();
   // mFan.stopMotor();
*/



/* 7. 私有继承和聚合。

   可以对上面的私有继承程序修改一下，将ElectriMotor作为Fan的私有成员，而不是以私有方式从ElectriMotor
   派生出Fan。这称为聚合或组合。

   // 聚合或组合
   class Fan {
       public:
           Fan() {}
           ~Fan() {}

           void startFan() {
               mMotor.startMotor();
           }

           void stopFan() {
               mMotor.stopMotor();
           }

       private:
           ElectriMotor mMotor;
   };
*/


#include "Extends.h"

void testDog() {
    Dog fido;
    fido.speak();
    fido.sleep();
    fido.wagTail();
    fido.begForFood();

    cout << "Fido is " << fido.getAge() << " years old, weight is " << fido.getWeight() << endl;
}

void testDog1() {
    Mammal *pMammal = new Dog;
    pMammal->speak();
}

void testPrivateExtends() {
    Fan mFan;
    mFan.startFan();
    mFan.stopFan();
    // 但是外部调用者不能调用基类的方法
    //mFan.startMotor();
    //mFan.stopMotor();
}

int main() {
    // testDog1();
    testPrivateExtends();

    return 0;
}