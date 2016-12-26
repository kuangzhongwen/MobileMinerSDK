//
// Created by Mr.Kuang on 11/2/16.
// 第十四章－类型转换运算符。
//

/* 1. 类型转换是一种机制，让程序员能够暂时或永久性编译器中对对象的解释。

   为什么需要类型转换？
   看一个例子，虽然c++编译器支持bool，但很多年前用c写的库还在。这些针对c语言写的库必须依赖于整型来
   保存布尔值。

   为此可以使用类型转换：
   bool bcPPResult = (bool) isX();


   为啥c++程序员不喜欢c风格的类型转换,大多数c++编译器不会让下面的语句通过编译：
   char *pszString = "hello world";
   int *pBuf = pszString;

   当前c++编译器仍需要向后兼容以确保遗留代码能够编译，因此支持如下的做法：
   int *pBuf = (int *) pszString;
*/


/* 2. c++的类型转换符。

   虽然类型转换有缺点，但也不能抛弃类型转换的概念。在很多情况下，类型转换是合理要求，可解决重要的兼容性问题。
   c++提供了一种新的类型运算符，专门用于基于继承的情形。

   4个c++类型转换符如下：
   static_cast
   dynamic_cast
   reinterpret_cast
   const_cast

   这四个类型转换符的基本用法：

   destination_type result = cast_type <destination_type> (object_to_be_casted)



   >>> static_cast:
   static_cast用于相关类型的指针之间进行转换，还可以显式地执行标准数据类型的类型转换。
   用于指针时，static_cast实现了基本的编译阶段检查，确保指针被转换为相关类型。
   这改进了c语言的类型转换，c语言中，可以将一个对象的指针转换成完全不相关的类型，而编译不会报错。

   使用static_cast可以将指针向上转换为基本类型，也可以向下转换为派生类型。

   CBase *pBase = new CDerived();
   CDerived *pDerived = static_cast <CDerived *> (pBase);

   // 不能转换成一个没关系的类型
   CUnrelate *pUnrelate = static_cast <CDerived *> (pBase);

   然而static_cast只验证指针类型是否相关，而不会做任何运行阶段检查，因此程序员可以用static_cast
   编写如下代码：

   CBase *pBase = new CBase();
   CDerived *pDerived = static_cast <CDerived *> (pBase);

   pDerived指向一个不完整的CDerived对象，在编译时不会报错，但是在运行阶段可能就会出问题。





   >>> dynamic_cast:

   顾名思义，与静态类型转换相比，动态类型转换在运行阶段（即应用程序运行时）执行类型转换。
   可检查dynamic_cast操作的结果以判断类型转换是否成功。使用dynamic_cast运算符的典型语法是：


   destination_type *pDest = dynamic_cast <class_type *> (pSource);
   if (pDest) {
       pDest->CallFunc();
   }

   如上述代码所述：
   给定一个指向基类的对象，程序员可以使用dynamic_cast转换，并且在使用指针前检查指针指向的对象的类型。

   #include <iostream>

   using namespace std;

   void determineType(Animal *animal);

   //void test1() {
       //char *pszString = "hello world";
       //int *pBuf = (int *) pszString;
   //}

   class Animal {
       public:
           virtual void speak() = 0;
   };

   class Dog : public Animal {

       public:
           void wagTail() {
               cout << "Dog wag tail!" << endl;
           }

           void speak() {
               cout << "Dog speak!" << endl;
           }
   };

   class Cat : public Animal {
       public:
           void catchMice() {
               cout << "Cat catch mice!" << endl;
           }

           void speak() {
               cout << "Cat speak!" << endl;
           }
   };

   void testAnimal() {
       Animal *animal1 = new Dog();
       Animal *animal2 = new Cat();

       determineType(animal1);
       determineType(animal2);

       animal1 -> speak();
       animal2 -> speak();
   }

   int main() {
       // test1();
       testAnimal();

       return 0;
   }

   void determineType(Animal *animal) {
       Dog *dog = dynamic_cast <Dog*> (animal);
       if (dog) {
           cout << "The animal is dog" << endl;
           dog -> wagTail();
       }
       Cat *cat = dynamic_cast <Cat*> (animal);
       if (cat) {
           cout << "The animal is cat" << endl;
           cat -> catchMice();
       }
   }
*/



/* 3. 使用reinterpret_cast。

   reinterpret_cast是c++中与c最接近的类型转换符。它让程序员能将一种对象类型转换成另一种。
   不管它们是否相关。也就是强制类型转换。

   CBase *base = new CBase();
   CUnrelate *unrelate = reinterpret_cast <CUrelate*> (base);

   但是被c++认为类型转换不安全，会有警告。
*/



/* 4. const_cast。

   const_cast能够让程序员关闭对象的访问修饰符 const，您可能要问：为什么要做这种转换？在理想的情况下，
   程序员将经常在正确的地方使用const，但是现实是不理想的，经常看到该用const的地方没有用。如：

   class CSomeClass {
        public:
            // 输出显示的函数，没有改变成员变量，应该定义为const
            void displayMembers();
   }

   在下面函数中，以const引用的方式传递mData对象显然是正确的。毕竟显示函数应该是只读的，不应该是非const
   函数，即不应该调用修改对象状态的函数。然后displayMembers()本应该是const的，但却没有这样定义。
   如果源码在你这，你可以修改去控制，但很多时候是第三方的代码，无法对其进行修改。
   在这种情况下，const_cast是您的救星。

   void displayAllData(const CSomeClass& mdata) {
        // 会编译失败，调用一个非常量函数在一个常量引用上
        mdata.displayMembers();
   }

   改成这样：
   void displayAllData(const CSomeClass& mdata) {
        CSomeClass& refData = const_cast <CSomeClass&> (mdata);
        refData.displayMembers();
   }

   另外const_cast也可用于指针：
   void displayAllData(const CSomeClass* mdata) {
        CSomeClass* cdata = const_cast <CSomeClass*> (mdata);
        cdata.displayMembers();
   }
*/




#include <iostream>

using namespace std;

void determineType(Animal *animal);

//void test1() {
    //char *pszString = "hello world";
    //int *pBuf = (int *) pszString;
//}

class Animal {
    public:
        virtual void speak() = 0;
};

class Dog : public Animal {

    public:
        void wagTail() {
            cout << "Dog wag tail!" << endl;
        }

        void speak() {
            cout << "Dog speak!" << endl;
        }
};

class Cat : public Animal {
    public:
        void catchMice() {
            cout << "Cat catch mice!" << endl;
        }

        void speak() {
            cout << "Cat speak!" << endl;
        }
};

void testAnimal() {
    Animal *animal1 = new Dog();
    Animal *animal2 = new Cat();

    determineType(animal1);
    determineType(animal2);

    animal1 -> speak();
    animal2 -> speak();
}

int main() {
    // test1();
    testAnimal();

    return 0;
}

void determineType(Animal *animal) {
    Dog *dog = dynamic_cast <Dog*> (animal);
    if (dog) {
        cout << "The animal is dog" << endl;
        dog -> wagTail();
    }
    Cat *cat = dynamic_cast <Cat*> (animal);
    if (cat) {
        cout << "The animal is cat" << endl;
        cat -> catchMice();
    }
}